package com.github.FurianMan.time_tracker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.FurianMan.time_tracker.Exceptions.HttpHeaderException;
import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Utilities {
    static private Map<String, String> env = System.getenv();
    static String getConstants (String keyName) {
        return env.get(keyName);
    }
    public static Gson gsonBuilderSet () { return  null;}
    public static void checkContentType (HttpExchange exchange) throws IOException, HttpHeaderException {
        /**
         * Метод предназначен для проверки наличия поля 'Content-type: application/json'
         * Таким образом мы обрабатываем дату только в формате json
         * @param exchange: принимаем объект 'HttpExchange'.
         *                Через этот объект мы взаимодействуем с входящим запросом
         * Если происходит исключение, то за его обработку отвечает метод, который вызвал функцию.
         * */
        if (!exchange.getRequestHeaders().get("Content-type").contains("application/json")) {
            throw new HttpHeaderException("Header 'Content-Type' has to be equal 'application/json'");
        }
    }
    public static String serializeErrToJson (String data) {
        Gson g = new Gson();
        String str = g.toJson("{message:" + data + "}");
        return str;
    }
//    public static Map<String, String> serializeDataToJson (String data) {
//
//    }
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
                    } catch (IllegalArgumentException ex) {
                        Logger.getLogger(TableUsersDeserializer.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IllegalAccessException ex) {
                        Logger.getLogger(TableUsersDeserializer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            return pojo;

        }
    }
}
