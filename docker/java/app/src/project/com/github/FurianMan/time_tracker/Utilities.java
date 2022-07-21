package com.github.FurianMan.time_tracker;

import com.github.FurianMan.time_tracker.Exceptions.ApplicationException;
import com.github.FurianMan.time_tracker.Exceptions.ErrResponse;
import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Map;

public class Utilities {
    static private Map<String, String> env = System.getenv();
    private static final org.slf4j.Logger utilitieslLogger = Constants.getUtilitieslLogger();

    static String getConstants(String keyName) {
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

    static class TableUsersDeserializer<T> implements JsonDeserializer<T> {

        public T deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
            T pojo = (T) new Gson().fromJson(je, type);

            Field[] fields = pojo.getClass().getDeclaredFields();
            for (java.lang.reflect.Field f : fields) {
                if (f.getAnnotation(JsonRequired.class) != null) {
                    try {
                        f.setAccessible(true);
                        if (f.get(pojo) == null) {
                            throw new JsonParseException("Missing field in JSON: " + f.getName());
                        }
                    } catch (IllegalArgumentException e) {
                        utilitieslLogger.error("Can't get class for database. No driver found", e);
                    } catch (IllegalAccessException e) {
                        utilitieslLogger.error("Can't get class for database. No driver found", e);
                    }
                }
            }
            return pojo;

        }
    }
}
