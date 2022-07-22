package com.github.FurianMan.time_tracker.Exceptions;

import com.google.gson.annotations.Expose;

public class ApplicationException extends Exception {
    @Expose private int httpCode;

    ApplicationException(String message) {
        super(message);
    }

    ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApplicationException(String message, Throwable cause, int httpCode) {
        super(message, cause);
        this.httpCode = httpCode;
    }

    public ApplicationException(String message, int httpCode) {
        super(message);
        this.httpCode = httpCode;
    }

    public int getHttpCode() {
        return httpCode;
    }
}
