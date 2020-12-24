package com.climb.redis.exception;

import com.climb.common.exception.ErrorMessage;
import lombok.Getter;

/**
 * @author lht
 * @since 2020/12/23 16:14
 */
@Getter
public enum ErrorCode implements ErrorMessage {
    /**
     * 获取分布式失败
     * @author lht
     * @since  2020/12/23 16:17
     */
    LOCK_NOT_FIND(9001,"获取分布式失败"),
    LOCK_PARSING_CONTEXT(9002,"解析分布式锁Context异常"),
    ;

    private final String msg;
    private final int code;

    ErrorCode( int code,String msg) {
        this.msg = msg;
        this.code = code;
    }
}
