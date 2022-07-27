package com.github.FurianMan.time_tracker.exceptions;

import com.google.gson.annotations.Expose;

public class UnexpectedErr extends Exception {

    public UnexpectedErr(String message) {
        super(message);
    }

    UnexpectedErr(String message, Throwable cause) {
        super(message, cause);
    }
}
