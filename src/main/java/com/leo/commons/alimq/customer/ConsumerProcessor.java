package com.leo.commons.alimq.customer;

import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.leo.commons.alimq.annotation.RocketMqConsume;
import com.leo.commons.alimq.config.RocketMqProperties;
import com.leo.commons.alimq.exception.RocketMqException;
import com.leo.commons.alimq.utils.MqUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author LEO
 * 消费者管理
 */
@Slf4j
public class ConsumerProcessor  {

    private RocketMqProperties mqProperties;
    private Map<String, Object> consumeBeans;

    private static Map<String, Consumer> consumerMap = new LinkedHashMap<>(10);

    public ConsumerProcessor(RocketMqProperties properties, Map<String, Object> consumeBeans) {
        this.mqProperties = properties;
        this.consumeBeans = consumeBeans;
    }

    public void start() {
        for (String consumeKey : consumeBeans.keySet()) {
            Object bean = consumeBeans.get(consumeKey);
            Class<?> clazz = AopUtils.getTargetClass(bean);
            RocketMqConsume annotation = clazz.getAnnotation(RocketMqConsume.class);
            if (null != annotation) {
                RocketMqListener mqListener = (RocketMqListener) bean;
                String suffix = mqProperties.getSuffix();
                String groupId = MqUtil.getGroupId(annotation.groupId(), suffix);
                String tag = MqUtil.getTag(annotation.tag(), suffix);
                String topic = MqUtil.getTopic(annotation.topic(), suffix);

                // 配置属性
                int maxReconsumeTimes = mqProperties.getMaxReconsumeTimes();
                if (maxReconsumeTimes < 1) {
                    throw new RocketMqException("Consumer重试次数配置错误，初始化失败！");
                }
                Properties p = new Properties();
                p.setProperty(PropertyKeyConst.GROUP_ID, groupId);
                p.setProperty(PropertyKeyConst.AccessKey, mqProperties.getAccessKey());
                p.setProperty(PropertyKeyConst.SecretKey, mqProperties.getSecretKey());
                p.setProperty(PropertyKeyConst.NAMESRV_ADDR, mqProperties.getAddress());
                p.put(PropertyKeyConst.MessageModel, mqProperties.getMessageModel());
                p.put(PropertyKeyConst.MaxReconsumeTimes, maxReconsumeTimes);
                // 消费线程数量
                int consumeThreadNums = mqListener.consumeThreadNums();
                if (consumeThreadNums > 0) {
                    p.put(PropertyKeyConst.ConsumeThreadNums, consumeThreadNums);
                }

                if (!MqUtil.checkProperties(p)) {
                    throw new RocketMqException("Consumer配置错误，初始化失败！");
                }

                Consumer consumer = ONSFactory.createConsumer(p);
                ConsumerListener consumerListener = new ConsumerListener<>(mqListener, mqProperties.isShowLog());
                consumer.subscribe(topic, tag, consumerListener);
                consumer.start();

                consumerMap.put(groupId, consumer);
                log.info("RocketMqConsumer--->[{}]初始化完成！！！", groupId);
            }
        }
    }

    public void shutdown() {
        for (String key : consumerMap.keySet()) {
            Consumer consumer = consumerMap.get(key);
            try {
                if (consumer != null && consumer.isStarted()) {
                    log.info("RocketMqConsumer--->[{}]停止完成！！！", key);
                    consumer.shutdown();
                }
            } catch (IllegalStateException e) {
                // ignore
            }
        }
    }

}
