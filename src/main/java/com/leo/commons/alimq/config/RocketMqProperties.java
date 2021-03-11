package com.leo.commons.alimq.config;

import com.aliyun.openservices.ons.api.PropertyValueConst;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author LEO
 * @Description : mq配置
 */
@Data
@ConfigurationProperties(prefix = "aliyun.mq")
public class RocketMqProperties {

    private String address;

    private String accessKey;

    private String secretKey;

    /**
     * 后缀
     */
    private String suffix;

    /**
     * 是否打印日志
     */
    private boolean showLog = false;

    /**
     * GroupID的最大消息重试次数为20次
     * 默认为5次
     */
    private int maxReconsumeTimes = 5;

    /**
     * 消费模式, 默认集群消费
     * @see com.aliyun.openservices.ons.api.PropertyValueConst
     */
    private String messageModel = PropertyValueConst.CLUSTERING;

}
