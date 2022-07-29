package com.github.FurianMan.time_tracker.httpserver;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import static com.github.FurianMan.time_tracker.Constants.*;

public class HandleReqVersion {
    /**
     * Метод для обработки запросов по url /time-tracker/version
     *
     * @param exchange - http запрос от пользователя
     * */
    public static void handleReqVersion(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            handleReqVersionLogger.info("Request from: " + exchange.getRemoteAddress().getAddress()
                    + " Method: " + exchange.getRequestMethod()
                    + " On server: " + exchange.getLocalAddress().getAddress()
                    + exchange.getHttpContext().getPath());
            String respText = appVersion;
            exchange.sendResponseHeaders(200, respText.getBytes(StandardCharsets.UTF_8).length);
            OutputStream output = exchange.getResponseBody();
            output.write(respText.getBytes());
            output.flush();
        } else {
            exchange.sendResponseHeaders(405, -1);// 405 Method Not Allowed
        }
        exchange.close();
    }
}
