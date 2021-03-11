package com.leo.commons.alimq.producer;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.OnExceptionContext;
import com.aliyun.openservices.ons.api.SendCallback;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.api.bean.ProducerBean;
import com.leo.commons.alimq.exception.RocketMqException;
import com.leo.commons.alimq.message.IMessageEvent;
import com.leo.commons.alimq.message.RocketMqMessage;
import com.leo.commons.alimq.utils.MqUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.concurrent.CompletableFuture;

/**
 * @author LEO
 * 普通消息、定时消息、延迟消息生产者
 */
@Slf4j
public class RocketMqTemplate {

    private static final long DELAY_DAY_30 = 30L * 24 * 60 * 60 * 1000;

    private ProducerBean producer;
    private String suffix;
    /** 打印消息日志 */
    private boolean showLog;

    public RocketMqTemplate(ProducerBean producer, String suffix, boolean showLog) {
        this.producer = producer;
        this.suffix = suffix;
        this.showLog = showLog;
    }

    /**
     * 同步发送
     * @param event  event事件
     * @param domain 对象，支持传list
     */
    public SendResult send(IMessageEvent event, Object domain) {
        String domainStr = MqUtil.objParser(domain);
        return send(new RocketMqMessage(event, domainStr));
    }

    /**
     * 同步发送
     * @param event  event事件
     * @param domain 对象，支持传list
     * @param messageKey 唯一标示
     */
    public SendResult send(IMessageEvent event, Object domain, String messageKey) {
        String domainStr = MqUtil.objParser(domain);
        return send(new RocketMqMessage(event, domainStr, messageKey));
    }

    /**
     * 同步发送
     * @param event 事件
     */
    public SendResult send(RocketMqMessage event) {
        return sendTime(event, 0L);
    }

    /**
     * 同步发送，延迟发送
     * @param event 事件
     * @param delay 延迟时间
     */
    public SendResult send(RocketMqMessage event, long delay) {
        return sendTime(event, System.currentTimeMillis() + delay);
    }

    /**
     * 同步发送，定时发送
     * @param event 事件
     * @param date  发送时间
     */
    public SendResult send(RocketMqMessage event, Date date) {
        return sendTime(event, date.getTime());
    }

    /**
     * 同步发送，延迟发送
     * @param event       事件
     * @param deliverTime 发送时间
     */
    public SendResult sendTime(RocketMqMessage event, long deliverTime) {
        checkStartTime(deliverTime);

        Message message = MqUtil.getMqMessage(event, suffix);
        if (deliverTime > 0) {
            message.setStartDeliverTime(deliverTime);
        }
        if (showLog) {
            log.info("RocketMqTemplate(topic={}, tag={}, messageKey={}, startDeliverTime={}, body={})",
                    message.getTopic(), message.getTag(), message.getKey(),
                    message.getStartDeliverTime(), event.getDomain());
        }
        return producer.send(message);
    }

    /**
     * 异步发送
     * @param event  event事件
     * @param domain 对象
     */
    public CompletableFuture<SendResult> sendAsync(IMessageEvent event, Object domain) {
        String domainStr = MqUtil.objParser(domain);
        return sendAsync(new RocketMqMessage(event, domainStr));
    }

    /**
     * 异步发送
     * @param event 事件
     */
    public CompletableFuture<SendResult> sendAsync(RocketMqMessage event) {
        return sendAsyncTime(event, 0L);
    }

    /**
     * 异步发送，定时发送
     * @param event 事件
     * @param date  发送时间
     */
    public CompletableFuture<SendResult> sendAsync(RocketMqMessage event, Date date) {
        return sendAsyncTime(event, date.getTime());
    }

    /**
     * 异步发送，延迟发送
     * @param event 事件
     * @param delay 延迟时间
     */
    public CompletableFuture<SendResult> sendAsync(RocketMqMessage event, long delay) {
        return sendAsyncTime(event, System.currentTimeMillis() + delay);
    }

    /**
     * 异步发送，延迟发送
     * @param event     事件
     * @param startTime 发送时间
     */
    public CompletableFuture<SendResult> sendAsyncTime(RocketMqMessage event, long startTime) {
        checkStartTime(startTime);
        if (showLog) {
            log.info("{}", event.toString());
        }
        CompletableFuture<SendResult> future = new CompletableFuture<>();
        Message message = MqUtil.getMqMessage(event, suffix);
        if (startTime > 0) {
            message.setStartDeliverTime(startTime);
        }

        producer.sendAsync(message, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                future.complete(sendResult);
            }

            @Override
            public void onException(OnExceptionContext context) {
                future.completeExceptionally(new RocketMqException(context));
            }
        });
        return future;
    }

    private static void checkStartTime(long startTime) {
        if (startTime == 0) {
            return;
        }
        long nowTime = System.currentTimeMillis();
        if (startTime <= nowTime) {
            throw new RocketMqException("发送时间不能小于当前时间！");
        } else if (startTime > nowTime + DELAY_DAY_30) {
            throw new RocketMqException("发送时间不能大于30天时间！");
        }
    }

}
