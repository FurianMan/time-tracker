package com.github.FurianMan.time_tracker.exceptions;

import com.google.gson.annotations.Expose;

/**
 * Основной класс для вызова ошибок в приложении.
 * Вместе с причиной имеет поле httpCode, куда
 * мы вносим номер ответа пользователю по http
* */
public class ApplicationException extends RuntimeException {
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
