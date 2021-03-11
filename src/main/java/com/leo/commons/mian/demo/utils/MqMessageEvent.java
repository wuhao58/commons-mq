package com.leo.commons.mian.demo.utils;

import com.leo.commons.alimq.message.IMessageEvent;
import lombok.Getter;

/**
 * @author : LEO
 * @Description : MQ定义(topic,tag,groupId)
 * @Date :  2019/12/05
 */
@Getter
public enum MqMessageEvent implements IMessageEvent {

    DETAIL_V1(Constants.TOPIC_DETAIL, Constants.TAG_V1);

    private String topic;
    private String tag;

    MqMessageEvent(String topic, String tag) {
        this.topic = topic;
        this.tag = tag;
    }

    @Override
    public String topic() {
        return this.topic;
    }

    @Override
    public String tag() {
        return this.tag;
    }

    /**
     * 常量定义
     */
    public static class Constants {
        public static final String TOPIC_DETAIL = "detail";

        public static final String TAG_V1 = "v1";
        public static final String GROUPID_V1 = "GID-demo";
    }

}