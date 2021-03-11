package com.leo.commons.alimq.utils;

import com.alibaba.fastjson.JSON;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.leo.commons.alimq.exception.RocketMqException;
import com.leo.commons.alimq.message.RocketMqMessage;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Properties;

/**
 * @author LEO
 * 兼容老代码
 */
public class MqUtil {

    private static final String DEV = "-dev";
    private static final String TEST = "-test";
    private static final String PRE = "-pre";
    private static final String ONLINE = "-online";

    public static String getGroupId(String groupId, String suffix) {
        return groupId + suffix;
    }

    public static String getTag(String tag, String suffix) {
        return getFinalTag(tag, suffix);
    }

    public static String getTopic(String topic, String suffix) {
        return topic + getSuffix(suffix);
    }

    private static String getFinalTag(String tag, String suffix) {
        String more = "||";
        // consume适配订阅多个tag的情况
        if (tag.contains(more)) {
            String[] list = tag.split("\\|\\|");

            StringBuffer sb = new StringBuffer();
            for (int i = 0, size = list.length; i < size; i++) {
                String tagTmp = list[i];
                if (!StringUtils.isEmpty(tagTmp)) {
                    sb.append(tagTmp);
                    sb.append(suffix);
                    if (i != size - 1) {
                        sb.append(more);
                    }
                }
            }
            return sb.toString();
        }
        return tag + suffix;
    }

    private static String getSuffix(String suffix) {
        // 开发的producer和topic后缀使用-test，预发使用-online，节约成本
        if (DEV.equalsIgnoreCase(suffix)) {
            return TEST;
        } else if (PRE.equalsIgnoreCase(suffix)) {
            return ONLINE;
        } else {
            return suffix;
        }
    }

    public static Message getMqMessage(RocketMqMessage event, String suffix) {
        if (event == null) {
            throw new RocketMqException("event is null.");
        }
        if (StringUtils.isEmpty(event.getTopic())) {
            throw new RocketMqException("topic is null.");
        }
        if (StringUtils.isEmpty(event.getDomain())) {
            throw new RocketMqException("domain is null.");
        }
        String topic = getTopic(event.getTopic(), suffix);
        String tag = getTag(event.getTag(), suffix);
        Message message = new Message(topic, tag, event.getDomain().getBytes());
        message.setKey(event.getMessageKey());
        return message;
    }

    public static String objParser(Object obj) {
        if (null != obj) {
            // @JSONField暂未处理
            return JSON.toJSONString(obj);
        }
        return null;
    }

    /**
     * 检查配置是否合法
     * @param properties 配置
     */
    public static boolean checkProperties(Properties properties) {
        return properties != null
                && properties.get(PropertyKeyConst.GROUP_ID) != null
                && properties.get(PropertyKeyConst.AccessKey) != null
                && properties.get(PropertyKeyConst.SecretKey) != null
                && properties.get(PropertyKeyConst.NAMESRV_ADDR) != null;
    }

    /**
     * 解析当前索引泛型
     * @param cls 类
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public static Type parseType(Class<?> cls, Class<?> iCls) {
        Type[] genTypes = cls.getGenericInterfaces();
        if (genTypes.length == 0) {
            return null;
        }

        ParameterizedType genType = null;
        for (Type type : genTypes) {
            if (!(type instanceof ParameterizedType)) {
                continue;
            }
            ParameterizedType tmpType = (ParameterizedType) type;
            if (iCls.isAssignableFrom(((Class<?>) tmpType.getRawType()))) {
                genType = tmpType;
                break;
            }
        }
        if (genType == null) {
            return null;
        }
        Type[] typeParams = genType.getActualTypeArguments();
        if (typeParams.length != 1) {
            return null;
        }
        return typeParams[0];
    }

}
