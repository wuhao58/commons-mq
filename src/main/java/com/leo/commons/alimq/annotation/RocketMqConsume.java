package com.leo.commons.alimq.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author LEO
 * 消息者注解，监听消费消息
 */
@Component
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RocketMqConsume {

    /**
     * topic name
     */
    String topic();

    /**
     * tag name
     */
    String tag() default "*";

    /**
     * group id
     */
    String groupId() default "";

}
