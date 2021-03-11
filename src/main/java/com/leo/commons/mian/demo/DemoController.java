package com.leo.commons.mian.demo;

import com.alibaba.fastjson.JSONObject;
import com.leo.commons.alimq.producer.RocketMqTemplate;
import com.leo.commons.mian.demo.utils.MqMessageEvent;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @author: LEO
 * @Date: 2021-03-11 17:03
 * @Description:
 */
@AllArgsConstructor
@RestController
@RequestMapping("/demo")
public class DemoController {

    private RocketMqTemplate rocketMqTemplate;

    @GetMapping(value = "/send")
    public String send() {
        String detailId = UUID.randomUUID().toString();

        JSONObject json = new JSONObject(2);
        json.put("detailId", detailId);
        json.put("k1", "demo");

        String messageKey = detailId;
        rocketMqTemplate.send(MqMessageEvent.DETAIL_V1, json, messageKey);
        return "ok";
    }

}
