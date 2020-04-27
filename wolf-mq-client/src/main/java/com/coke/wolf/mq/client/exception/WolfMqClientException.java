package com.coke.wolf.mq.client.exception;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/23 10:52 上午
 */
public class WolfMqClientException extends RuntimeException {

    public WolfMqClientException() {
    }

    public WolfMqClientException(String message) {
        super(message);
    }

    public WolfMqClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public WolfMqClientException(Throwable cause) {
        super(cause);
    }

    public WolfMqClientException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
