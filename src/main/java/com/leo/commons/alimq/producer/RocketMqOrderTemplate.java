package com.leo.commons.alimq.producer;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.api.bean.OrderProducerBean;
import com.leo.commons.alimq.message.IMessageEvent;
import com.leo.commons.alimq.message.RocketMqMessage;
import com.leo.commons.alimq.message.RocketMqOrderType;
import com.leo.commons.alimq.utils.MqUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author LEO
 * 顺序消息生产者
 */
@Slf4j
public class RocketMqOrderTemplate {

    private OrderProducerBean orderProducer;
    private String suffix;
    /** 打印消息日志 */
    private boolean showLog;

    public RocketMqOrderTemplate(OrderProducerBean orderProducer, String suffix, boolean showLog) {
        this.orderProducer = orderProducer;
        this.suffix = suffix;
        this.showLog = showLog;
    }

    /**
     * 同步发送全局顺序消息
     * @param event  event事件
     * @param domain 对象
     */
    public SendResult send(IMessageEvent event, Object domain) {
        String domainStr = MqUtil.objParser(domain);
        return send(new RocketMqMessage(event, domainStr));
    }

    /**
     * 同步发送全局顺序消息
     * @param event 事件
     */
    public SendResult send(RocketMqMessage event) {
        return send(event, RocketMqOrderType.GLOBAL);
    }

    /**
     * 同步发送顺序消息
     * @param event  event事件
     * @param domain 对象
     */
    public SendResult send(IMessageEvent event, Object domain, RocketMqOrderType orderType) {
        String domainStr = MqUtil.objParser(domain);
        return send(new RocketMqMessage(event, domainStr), orderType);
    }

    /**
     * 同步发送顺序消息
     * @param event     事件
     * @param orderType 类型
     */
    public SendResult send(RocketMqMessage event, RocketMqOrderType orderType) {
        String sharding;
        switch (orderType) {
            case TOPIC:
                sharding = "#" + event.getTopic() + "#";
                break;
            case TAG:
                sharding = "#" + event.getTopic() + "#" + event.getTag() + "#";
                break;
            case GLOBAL:
            default:
                sharding = "#global#";
                break;
        }
        return send(event, sharding);
    }

    /**
     * 同步发送顺序消息
     * @param event  event事件
     * @param domain 对象
     */
    public SendResult send(IMessageEvent event, Object domain, String sharding) {
        String domainStr = MqUtil.objParser(domain);
        return send(new RocketMqMessage(event, domainStr), sharding);
    }

    /**
     * 同步发送顺序消息
     * @param event    事件
     * @param sharding 分区顺序消息中区分不同分区的关键字段，sharding key 于普通消息的 key 是完全不同的概念。
     */
    public SendResult send(RocketMqMessage event, String sharding) {
        Message message = MqUtil.getMqMessage(event, suffix);

        if (showLog) {
            log.info("RocketMqOrderTemplate(topic={}, tag={}, messageKey={}, startDeliverTime={}, body={})",
                    message.getTopic(), message.getTag(), message.getKey(),
                    message.getStartDeliverTime(), event.getDomain());
        }
        return orderProducer.send(message, sharding);
    }

}
