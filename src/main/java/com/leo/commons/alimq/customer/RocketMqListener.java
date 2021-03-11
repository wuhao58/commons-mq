package com.leo.commons.alimq.customer;

import org.springframework.lang.NonNull;

/**
 * @author LEO
 * 消息监听者
 */
public interface RocketMqListener<T> {

    /**
     * 消费者
     * @param message
     */
    void onConsumer(@NonNull T message);

    /**
     * 是否消息重试
     * @return
     */
    default boolean onRetry() { return true; }

    /**
     * 消费线程数量
     * @return
     */
    default int consumeThreadNums() { return -1; }

}
