package com.leo.commons.mian.demo.consumer;

import com.alibaba.fastjson.JSONObject;
import com.leo.commons.alimq.annotation.RocketMqConsume;
import com.leo.commons.alimq.customer.RocketMqListener;
import com.leo.commons.mian.demo.utils.MqMessageEvent.Constants;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: LEO
 * @Date: 2021-03-11 17:08
 * @Description:
 */
@Slf4j
@RocketMqConsume(topic = Constants.TOPIC_DETAIL, tag = Constants.TAG_V1, groupId = Constants.GROUPID_V1)
public class V1Consumer implements RocketMqListener<JSONObject> {

    @Override
    public void onConsumer(JSONObject message) {
        log.info("{}", message.toString());
        // 业务处理
    }

    @Override
    public boolean onRetry() {
        // 消费失败不进行重试
        return false;
    }

}