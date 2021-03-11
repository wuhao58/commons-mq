package com.leo.commons.alimq.message;

/**
 * @author LEO
 * 基础枚举
 */
public interface IMessageEvent {

    /**
     * 返回topic
     * @return
     */
    String topic();

    /**
     * 返回tag
     * @return
     */
    String tag();

}
