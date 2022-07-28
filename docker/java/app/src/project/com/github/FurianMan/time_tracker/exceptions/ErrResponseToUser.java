package com.github.FurianMan.time_tracker.exceptions;

import com.google.gson.annotations.Expose;

/**
 * Класс используется как обертка для ApplicationException
 * Это нужно, чтобы до пользователя дошла только высокоуровневая ошибка,
 * т.е. cause мы игнорируем, т.к. такие данные мы отражаем в логе.
 * Делается это через класс, т.к. так проще упаковать в json через gson
 *
* */
public class ErrResponseToUser {
    @Expose
    int httpCode;
    String message;

    public ErrResponseToUser(String message) {
        this.message = message;
    }

    public ErrResponseToUser(String message, int httpCode) {
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
