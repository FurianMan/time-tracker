package com.github.FurianMan.time_tracker.utilities;

import com.github.FurianMan.time_tracker.Constants;
import com.github.FurianMan.time_tracker.exceptions.ApplicationException;
import com.github.FurianMan.time_tracker.exceptions.ErrResponse;
import com.github.FurianMan.time_tracker.mysqlTables.TableUsers;
import com.github.FurianMan.time_tracker.mysqlUtilities.GetWorkStatsSum;
import com.sun.net.httpserver.HttpExchange;

import java.util.*;
import java.util.regex.Pattern;

public class Utilities {
    static private Map<String, String> env = System.getenv();
    private static final org.slf4j.Logger utilitieslLogger = Constants.getUtilitieslLogger();

    public static String getConstants(String keyName) {
        return env.get(keyName);
    }

    public static void checkContentType(HttpExchange exchange) throws ApplicationException {
        /**
         * Метод предназначен для проверки наличия поля 'Content-type: application/json'
         * Таким образом мы обрабатываем дату только в формате json
         * @param exchange: принимаем объект 'HttpExchange'.
         *                Через этот объект мы взаимодействуем с входящим запросом
         * Если происходит исключение, то за его обработку отвечает метод, который вызвал функцию.
         * */
        if (!exchange.getRequestHeaders().get("Content-type").contains("application/json")) {
            utilitieslLogger.error("Can't find Header 'Content-Type: application/json' in http-request");
            throw new ApplicationException("Header 'Content-Type' has to be equal 'application/json'", 415);
        }
    }

    public static ErrResponse makeErrResponseBody(String message) {
        ErrResponse bodyInstance = new ErrResponse(message);
        return bodyInstance;
    }
    /**
     * Метод валидации полей для таблицы users, которые указал пользователь.
     * Для валидации используются регулярные выражения. Строки ограничены размером в 255 символа.
     * Ограничение идет от mysql.
     * Если во время валидации что-то идет не так, то выдаем исключение.
     * @param newUser - пользователь со значениями из http запроса.
    * */
    public static void validateUserFields(TableUsers newUser) throws ApplicationException {
        String regexLetters = "(^[а-яА-ЯёЁ]*$)|(^[A-Za-z]*$)"; // русский или англ алфавит, но не вместе.
        String regexDate = "^(((20[012]\\d|19\\d\\d)|(1\\d|2[0123]))-((0[0-9])|(1[012]))-((0[1-9])|([12][0-9])|(3[01])))$"; // дата формата 2023-12-31
        ArrayList<String> fieldsList = newUser.getValues();
        for (String field : fieldsList) {//TODO переделать на словарь, чтобы в дебаге было видно не только значения
            if (field != null && !Pattern.matches(regexLetters, field)) {
                utilitieslLogger.error(String.format("Inappropriate json field value: %s", field));
                throw new ApplicationException(String.format("Inappropriate json field value: %s", field), 415);
            }
            utilitieslLogger.debug("Поля прошло валидацию: " + field);
        }
        /*
        * Дату проверяем отдельно, а т.к. их может быть две (вторая используется для update), то приходится проверять обе
        * */
        if (newUser.getNewBirthday() != null && !Pattern.matches(regexDate, newUser.getNewBirthday()) && !Pattern.matches(regexDate, newUser.getBirthday())) {
            utilitieslLogger.error("Inappropriate json field value for birthday or newBirthday");
            throw new ApplicationException("Inappropriate json field value for birthday or newBirthday", 415);
        }
        utilitieslLogger.info("Function validateUserFields has passed successfully");
    }
    /**
     * Метод валидации полей для получения статистики, которые указал пользователь.
     * Для валидации используются регулярные выражения.
     * Условия прохождения валидации:
     * 1 - поля  start_time и end_time не пустые
     * 2 - эти же поля проходят регулярку, формат 2022-12-31 23:59:59
     * Если во время валидации что-то идет не так, то выдаем исключение.
     * @param reqForGettingStats - пользователь со значениями из http запроса.
     * */
    public static void validateDateTime(RequestUserStats reqForGettingStats) throws ApplicationException {
        String start_time = reqForGettingStats.getStart_time();
        String end_time = reqForGettingStats.getEnd_time();
        String regexDateTime = "^(((20[012]\\d|19\\d\\d)|(1\\d|2[0123]))-((0[0-9])|(1[012]))" +
                "-((0[1-9])|([12][0-9])|(3[01]))) ([0-1]\\d|2[0-3])(:[0-5]\\d){2}$";

        if (start_time == null || end_time == null) {
            utilitieslLogger.error("start_time or end_time must not be empty!");
            throw new ApplicationException("start_time or end_time must not be empty!", 415);
        }
        if (!Pattern.matches(regexDateTime, start_time) || !Pattern.matches(regexDateTime, end_time)) {
            utilitieslLogger.error("Inappropriate json value for fields start_time or end_time, please check documentation");
            throw new ApplicationException("Inappropriate json value for fields start_time or end_time, please check documentation", 415);
        }
    }

    /**
     * Метод для перенаправления на сбор статистики
     * в зависимости от значения mode
    * */
    public static ResponseStats defineWayToGetStats (RequestUserStats reqData) throws ApplicationException {
        String mode = reqData.getMode().toLowerCase();
        if (mode.equals("sum")) {
            return GetWorkStatsSum.getWorkStatsSum(reqData);
        } else {
            utilitieslLogger.error("Inappropriate json value for field mode, please check documentation");
            throw new ApplicationException("Inappropriate json value for field mode, please check documentation", 415);
        }
    }
}
