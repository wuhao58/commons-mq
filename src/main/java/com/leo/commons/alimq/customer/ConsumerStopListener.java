package com.leo.commons.alimq.customer;

import com.leo.commons.alimq.annotation.RocketMqConsume;
import com.leo.commons.alimq.config.RocketMqProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author : LEO
 * @Description : Consumer延迟停止
 * @Date :  2019/12/4
 */
@Slf4j
@Component
public class ConsumerStopListener implements ApplicationListener<ContextClosedEvent>, Ordered {

    @Autowired
    private RocketMqProperties properties;

    @Value("${aliyun.mq.consumer.enabled:false}")
    private boolean consumerEnabled;

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent closedEvent) {
        if (consumerEnabled) {
            ApplicationContext springContext = closedEvent.getApplicationContext();

            Map<String, Object> consumeBeans = springContext.getBeansWithAnnotation(RocketMqConsume.class);
            ConsumerProcessor processor = new ConsumerProcessor(properties, consumeBeans);
            processor.shutdown();
        }
    }

}