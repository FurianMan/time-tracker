package com.github.FurianMan.time_tracker.utilities;

import com.github.FurianMan.time_tracker.exceptions.ApplicationException;
import com.github.FurianMan.time_tracker.exceptions.ErrResponseToUser;
import com.github.FurianMan.time_tracker.mysqlTables.TableUsers;
import com.github.FurianMan.time_tracker.mysqlUtilities.GetWorkStatsOneline;
import com.github.FurianMan.time_tracker.mysqlUtilities.GetWorkStatsPeriod;
import com.github.FurianMan.time_tracker.mysqlUtilities.GetWorkStatsSum;
import com.sun.net.httpserver.HttpExchange;

import java.util.*;
import java.util.regex.Pattern;

import static com.github.FurianMan.time_tracker.Constants.utilitieslLogger;
import static com.github.FurianMan.time_tracker.mysqlUtilities.GetUser.getUser;

public class Utilities {
    static private Map<String, String> env = System.getenv();
    public static String getConstants(String keyName) {
        return env.get(keyName);
    }

    /**
     * Метод предназначен для проверки наличия поля 'Content-type: application/json'
     * Таким образом мы обрабатываем дату только в формате json
     * @param exchange: принимаем объект 'HttpExchange'.
     *                Через этот объект мы взаимодействуем с входящим запросом
     * Если происходит исключение, то за его обработку отвечает метод, который вызвал функцию.
     * */
    public static void checkContentType(HttpExchange exchange) {
        if (!exchange.getRequestHeaders().get("Content-type").contains("application/json")) {
            utilitieslLogger.error("Can't find Header 'Content-Type: application/json' in http-request");
            throw new ApplicationException("Header `Content-Type` has to be equal `application/json` ", 415);
        }
    }

    /**
     * Метод нужен для генерации ответа пользователю
     * благодаря этому пользователь видит только высокоуровневую ошибку
    * */
    public static ErrResponseToUser makeErrResponseBody(String message) {
        return new ErrResponseToUser(message);
    }

    /**
     * Метод валидации полей для таблицы users, которые указал пользователь.
     * Для валидации используются регулярные выражения. Строки ограничены размером в 255 символа.
     * Ограничение идет от mysql.
     * Если во время валидации что-то идет не так, то выдаем исключение.
     * Валидируются:
     * name, surname, position, patronymic,
     * newName, newSurname, newPatronymic, newPosition
     * если поле null, то пропускаем его
     *
     * @param newUser - пользователь со значениями из http запроса.
     */
    public static void validateUserFields(TableUsers newUser) {
        String regexLetters = "(^[а-яА-ЯёЁ]*$)|(^[A-Za-z]*$)"; // русский или англ алфавит, но не вместе.
        String regexDate = "^(((20[012]\\d|19\\d\\d)|(1\\d|2[0123]))-((0[0-9])|(1[012]))-((0[1-9])|([12][0-9])|(3[01])))$"; // дата формата 2023-12-31
        ArrayList<String> fieldsList = newUser.getValues();
        for (String field : fieldsList) {//TODO переделать на словарь, чтобы в дебаге было видно не только значения
            if (field != null && !Pattern.matches(regexLetters, field)) {
                utilitieslLogger.error(String.format("Inappropriate json field value: %s", field));
                throw new ApplicationException(String.format("Inappropriate json field value: %s", field), 415);
            }
            utilitieslLogger.debug("Field has been validated successfully: " + field);
        }
        /*
         * Дату проверяем отдельно, а т.к. их может быть две (вторая используется для update), то приходится проверять обе
         * */
        String birthday = newUser.getBirthday();
        String newBirthday = newUser.getNewBirthday();
        if ((newBirthday != null && !Pattern.matches(regexDate, newBirthday)) || !Pattern.matches(regexDate, birthday)) {
            utilitieslLogger.error(String.format("Inappropriate json field value for birthday=%s or newBirthday=%s", birthday, newBirthday));
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
     *
     * @param reqForGettingStats - пользователь со значениями из http запроса.
     */
    public static void validateDateTime(RequestUserStats reqForGettingStats) {
        String start_time = reqForGettingStats.getStart_time();
        String end_time = reqForGettingStats.getEnd_time();
        String regexDateTime = "^(((20[012]\\d|19\\d\\d)|(1\\d|2[0123]))-((0[0-9])|(1[012]))" +
                "-((0[1-9])|([12][0-9])|(3[01]))) ([0-1]\\d|2[0-3])(:[0-5]\\d){2}$";

        if (start_time == null || end_time == null) {
            utilitieslLogger.error("start_time or end_time must not be empty!");
            throw new ApplicationException("start_time or end_time must not be empty!", 415);
        }
        if (!Pattern.matches(regexDateTime, start_time) || !Pattern.matches(regexDateTime, end_time)) {
            utilitieslLogger.error(String.format("Inappropriate json value for fields start_time=%s or end_time=%s", start_time, end_time));
            throw new ApplicationException("Inappropriate json value for fields start_time or end_time, please check documentation", 415);
        }
        utilitieslLogger.info("Validation of start_time and end_time has been passed successfully");
    }

    /**
     * Метод для перенаправления на сбор статистики
     * в зависимости от значения mode
     * Проверка на существование пользователя будет тут, т.к.
     * для всех методов ниже нужен пользователь
     */
    public static ResponseStats defineWayToGetStats(RequestUserStats reqData) throws ApplicationException {
        String mode = reqData.getMode();
        int user_id = reqData.getUser_id();

        if (user_id == 0) {
            utilitieslLogger.error("In request of getting stats user_id must not be equal 0");
            throw new ApplicationException("In request of getting stats user_id must not be equal 0", 415);
        }

        /*
         * Создаем класс пользователя и проверяем его существавание в db
         * */
        TableUsers userDB = new TableUsers();
        userDB.setUser_id(user_id);
        getUser(userDB);

        if ("sum".equals(mode)) {
            utilitieslLogger.info("In query of getting stats has been found mode=sum");
            return GetWorkStatsSum.getWorkStatsSum(reqData);
        } else if ("oneline".equals(mode)) {
            utilitieslLogger.info("In query of getting stats has been found mode=all");
            return GetWorkStatsOneline.getWorkStatsOneline(reqData);
        } else if ("period".equals(mode)) {
            utilitieslLogger.info("In query of getting stats has been found mode=period");
            return GetWorkStatsPeriod.getWorkStatsPeriod(reqData);
        } else {
            utilitieslLogger.error("Inappropriate json value for field mode for query of getting stats");
            throw new ApplicationException("Inappropriate json value for field mode, please check documentation", 415);
        }
    }
}
