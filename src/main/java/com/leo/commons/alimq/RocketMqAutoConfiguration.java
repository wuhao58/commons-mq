package com.leo.commons.alimq;

import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.bean.OrderProducerBean;
import com.aliyun.openservices.ons.api.bean.ProducerBean;
import com.leo.commons.alimq.config.RocketMqProperties;
import com.leo.commons.alimq.producer.RocketMqOrderTemplate;
import com.leo.commons.alimq.producer.RocketMqTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @author : LEO
 * @Description : 初始化配置
 * @Date :  2019/12/4
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(RocketMqProperties.class)
public class RocketMqAutoConfiguration {

    @Autowired
    private RocketMqProperties propConfig;

    @Bean(name = "producer", initMethod = "start", destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    public ProducerBean producer() {
        ProducerBean producerBean = new ProducerBean();
        producerBean.setProperties(createProperties());
        producerBean.start();
        return producerBean;
    }

    @Bean
    @ConditionalOnMissingBean
    public RocketMqTemplate rocketMqTemplate(ProducerBean producer) {
        RocketMqTemplate rocketMqTemplate = new RocketMqTemplate(producer, propConfig.getSuffix(), propConfig.isShowLog());
        log.info("RocketMqTemplate--->初始化完成！！！");
        return rocketMqTemplate;
    }

    @Bean(name = "orderProducer", initMethod = "start", destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "aliyun.mq.producer", value = "order-enabled", havingValue = "true")
    public OrderProducerBean orderProducer() {
        OrderProducerBean producerBean = new OrderProducerBean();
        producerBean.setProperties(createProperties());
        producerBean.start();
        return producerBean;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "aliyun.mq.producer", value = "order-enabled", havingValue = "true")
    public RocketMqOrderTemplate rocketMqOrderTemplate(OrderProducerBean orderProducer) {
        RocketMqOrderTemplate rocketMqOrderTemplate = new RocketMqOrderTemplate(orderProducer, propConfig.getSuffix(), propConfig.isShowLog());
        log.info("RocketMqOrderTemplate--->初始化完成！！！");
        return rocketMqOrderTemplate;
    }

    private Properties createProperties() {
        Properties p = new Properties();
        p.put(PropertyKeyConst.AccessKey, propConfig.getAccessKey());
        p.put(PropertyKeyConst.SecretKey, propConfig.getSecretKey());
        p.put(PropertyKeyConst.NAMESRV_ADDR, propConfig.getAddress());
        p.put(PropertyKeyConst.MessageModel, propConfig.getMessageModel());
        return p;
    }

}
