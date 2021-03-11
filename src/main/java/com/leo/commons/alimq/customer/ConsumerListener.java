package com.leo.commons.alimq.customer;

import com.alibaba.fastjson.JSON;
import com.aliyun.openservices.ons.api.*;
import com.leo.commons.alimq.exception.RocketMqException;
import com.leo.commons.alimq.utils.MqUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;

/**
 * @author : LEO
 * @Description :
 * @Date :  2019/12/4
 */
@Slf4j
public class ConsumerListener<T> implements MessageListener {

    private Type clsType;
    private RocketMqListener<T> rocketMqListener;
    /** 打印消息日志 */
    private boolean showLog;

    ConsumerListener(RocketMqListener<T> rocketMqListener, boolean showLog) {
        this.clsType = MqUtil.parseType(rocketMqListener.getClass(), RocketMqListener.class);
        if (null == this.clsType) {
            throw new RocketMqException(String.format("%s缺少泛型", rocketMqListener.getClass().getSimpleName()));
        }
        this.rocketMqListener = rocketMqListener;
        this.showLog = showLog;
    }

    @Override
    public Action consume(Message message, ConsumeContext context) {
        try {
            String messageBody = new String(message.getBody());
            if (showLog) {
                log.info("ConsumeMessage(topic={}, tag={}, messageId={}, messageKey={}, startDeliverTime={}, body={})",
                        message.getTopic(), message.getTag(), message.getMsgID(), message.getKey(),
                        message.getStartDeliverTime(), messageBody);
            }

            T value = JSON.parseObject(messageBody, clsType);
            if (value == null) {
                throw new NullPointerException("value is null");
            }
            rocketMqListener.onConsumer(value);
            return Action.CommitMessage;
        } catch (Throwable throwable) {
            boolean onRetry = rocketMqListener.onRetry();
            log.error("ConsumeMessage error: topic={}, tag={}, messageId={}, messageKey={}, onRetry={} ",
                    message.getTopic(), message.getTag(), message.getMsgID(), message.getKey(), onRetry, throwable);
            if (!onRetry) {
                return Action.CommitMessage;
            }
            return Action.ReconsumeLater;
        }
    }

}
