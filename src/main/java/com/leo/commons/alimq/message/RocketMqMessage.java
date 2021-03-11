package com.leo.commons.alimq.message;

import com.aliyun.openservices.shade.io.netty.util.internal.StringUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author : LEO
 * @Description :
 * @Date :  2019/12/4
 */
@Data
public class RocketMqMessage implements Serializable {

    public RocketMqMessage() {

    }

    public RocketMqMessage(IMessageEvent event, String domain) {
        this.topic = event.topic();
        this.tag = event.tag();
        this.domain = domain;
    }

    public RocketMqMessage(IMessageEvent event, String domain, String messageKey) {
        this.topic = event.topic();
        this.tag = event.tag();
        this.domain = domain;
        this.messageKey = messageKey;
    }

    /**
     * topic name
     */
    private String topic;

    /**
     * topic tag
     */
    private String tag;

    /**
     * 对象
     */
    private String domain;

    /**
     * 消息唯一表示
     */
    private String messageKey;


    /**
     * 事件创建时间
     */
    private long createdDate = System.currentTimeMillis();

    /**
     * 生成messageKey
     */
    public String getMessageKey() {
        String temp = String.format("%s:%s:", getTopic(), getTag());
        if (StringUtil.isNullOrEmpty(messageKey)) {
            String uuid = UUID.randomUUID().toString();
            return String.format("%s%s:%s", temp, getCreatedDate(), uuid);
        }
        return temp + messageKey;
    }

}
