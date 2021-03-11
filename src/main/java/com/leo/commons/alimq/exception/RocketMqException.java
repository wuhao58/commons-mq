package com.leo.commons.alimq.exception;

import com.aliyun.openservices.ons.api.OnExceptionContext;

/**
 * @author : LEO
 * @Description : 异常处理
 * @Date :  2019/12/4
 */
public class RocketMqException extends RuntimeException {

    private OnExceptionContext exceptionContext;

    public RocketMqException(String message) {
        super(message);
    }

    public RocketMqException(OnExceptionContext exceptionContext) {
        super(exceptionContext.getException());
        this.exceptionContext = exceptionContext;
    }

}
