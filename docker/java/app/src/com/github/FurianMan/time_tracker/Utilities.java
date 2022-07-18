package com.github.FurianMan.time_tracker;

import com.google.gson.*;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Utilities {
    static private Map<String, String> env = System.getenv();
    static String getConstants (String keyName) {
        return env.get(keyName);
    }
    public static Gson gsonBuilderSet () { return  null;}

    public static void checkContentType (HttpExchange exchange) throws IOException {
        if (String.valueOf(exchange.getRequestHeaders().get("Content-type")) != "application/json") {
            String respText = "Header 'Content-Type' has to be equal 'application/json'";
            exchange.sendResponseHeaders(400, respText.getBytes(StandardCharsets.UTF_8).length);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            OutputStream output = exchange.getResponseBody();
            output.write(respText.getBytes());
            output.flush();
        }
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
