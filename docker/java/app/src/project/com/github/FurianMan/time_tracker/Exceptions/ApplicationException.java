package com.github.FurianMan.time_tracker.Exceptions;

public class ApplicationException extends Exception {
    ApplicationException(String message) {super(message);}
    ApplicationException(String message, Throwable cause) {super(message, cause);}
}
