package com.climb.seata.lcn.exception;

import com.climb.common.exception.ErrorMessage;
import com.climb.common.exception.GlobalException;

/**
 * @author lht
 * @since 2021/1/22 11:34
 */
public class SeataException extends GlobalException {
    public SeataException() {
    }

    public SeataException(ErrorMessage errorMessage) {
        super(errorMessage);
    }

    public SeataException(ErrorMessage errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }

    public SeataException(String message, Throwable cause) {
        super(message, cause);
    }

    public SeataException(String message) {
        super(message);
    }

    public SeataException(Throwable cause) {
        super(cause);
    }

    public SeataException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public SeataException(int code) {
        super(code);
    }

    public SeataException(String message, int code) {
        super(message, code);
    }

    public SeataException(String message, Throwable cause, int code) {
        super(message, cause, code);
    }

    public SeataException(Throwable cause, int code) {
        super(cause, code);
    }

    public SeataException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, int code) {
        super(message, cause, enableSuppression, writableStackTrace, code);
    }
}
