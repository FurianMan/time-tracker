package com.github.FurianMan.time_tracker.Exceptions;

public class MysqlConnectException extends ApplicationException {
    public MysqlConnectException(String message) {
        super(message);
    }

    public MysqlConnectException(String message, Throwable cause) {
        super(message, cause);
    }
}
