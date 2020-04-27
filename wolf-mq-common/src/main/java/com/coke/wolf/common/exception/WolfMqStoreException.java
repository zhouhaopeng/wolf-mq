package com.coke.wolf.common.exception;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/18 8:29 上午
 */
public class WolfMqStoreException extends RuntimeException {

    public WolfMqStoreException() {
    }

    public WolfMqStoreException(String message) {
        super(message);
    }

    public WolfMqStoreException(String message, Throwable cause) {
        super(message, cause);
    }

    public WolfMqStoreException(Throwable cause) {
        super(cause);
    }

    public WolfMqStoreException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
