package com.github.FurianMan.time_tracker.exceptions;

import com.google.gson.annotations.Expose;

public class ErrResponse {
    @Expose
    int httpCode;
    String message;

    public ErrResponse(String message) {
        this.message = message;
    }

    public ErrResponse(String message, int httpCode) {
        this.message = message;
        this.httpCode = httpCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
