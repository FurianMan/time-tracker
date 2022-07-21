package com.github.FurianMan.time_tracker.Exceptions;

public class HttpHeaderException extends ApplicationException {
    public HttpHeaderException(String message) {
        super(message);
    }

    public HttpHeaderException(String message, Throwable cause) {
        super(message, cause);
    }
}
