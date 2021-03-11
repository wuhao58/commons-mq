package com.leo.commons.alimq.customer;

import com.leo.commons.alimq.annotation.RocketMqConsume;
import com.leo.commons.alimq.config.RocketMqProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author : LEO
 * @Description : Consumer延迟启动
 * @Date :  2019/12/4
 */
@Slf4j
@Component
public class ConsumerStartListener implements ApplicationListener<ApplicationReadyEvent>, Ordered {

    @Autowired
    private RocketMqProperties properties;

    @Value("${aliyun.mq.consumer.enabled:false}")
    private boolean consumerEnabled;

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent readyEvent) {
        if (consumerEnabled) {
            ApplicationContext springContext = readyEvent.getApplicationContext();

            Map<String, Object> consumeBeans = springContext.getBeansWithAnnotation(RocketMqConsume.class);
            ConsumerProcessor processor = new ConsumerProcessor(properties, consumeBeans);
            processor.start();
            log.info("RocketMqConsumer--->全部初始化完成！！！");
        }
    }

}
