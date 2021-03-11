package com.leo.commons.alimq.message;

import lombok.Getter;

/**
 * @author LEO
 * 顺序消息，消息分区类型
 */
@Getter
public enum RocketMqOrderType {

    /**
     * 全局顺序消息
     */
    GLOBAL(1),
    /**
     * topic作为分区的顺序消息,即同一topic消息保证为顺序消息
     */
    TOPIC(2),
    /**
     * topic 和tag 作为分区的顺序消息,即同一topic和tag消息保证为顺序消息
     */
    TAG(3);

    private int type;

    RocketMqOrderType(int code) {
        this.type = code;
    }

    public static RocketMqOrderType valueOf(byte type) {
        for (RocketMqOrderType item : RocketMqOrderType.values()) {
            if (item.getType() == type) {
                return item;
            }
        }
        return null;
    }
}
