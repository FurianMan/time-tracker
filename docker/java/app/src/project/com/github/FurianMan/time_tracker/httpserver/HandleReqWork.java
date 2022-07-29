package com.github.FurianMan.time_tracker.httpserver;

import com.github.FurianMan.time_tracker.exceptions.ApplicationException;
import com.github.FurianMan.time_tracker.mysqlTables.TableTasks;
import com.github.FurianMan.time_tracker.mysqlUtilities.InsertTask;
import com.github.FurianMan.time_tracker.mysqlUtilities.UpdateTask;
import com.github.FurianMan.time_tracker.utilities.Utilities;
import com.google.gson.JsonParseException;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import static com.github.FurianMan.time_tracker.Constants.*;
import static com.github.FurianMan.time_tracker.httpserver.HttpServerMain.*;

public class HandleReqWork {
    /**
     * Метод для обработки запросов по url /time-tracker/user/work
     *
     * @param exchange - http запрос от пользователя
     * */
    static void handleReqWork(HttpExchange exchange) throws IOException {
        String respText;
        OutputStream output;
        switch (exchange.getRequestMethod()) {
            case "POST":
                try {
                    handleReqWorkLogger.info("Request from: " + exchange.getRemoteAddress().getAddress()
                            + " Method: " + exchange.getRequestMethod()
                            + " On server: " + exchange.getLocalAddress().getAddress()
                            + exchange.getHttpContext().getPath());
                    String requestHeaders = exchange.getRequestHeaders().entrySet().toString();
                    String requestBody = new String(exchange.getRequestBody().readAllBytes());
                    handleReqWorkLogger.debug("Headers: " + requestHeaders + "\n"
                            + "Body: " + requestBody);
                    Utilities.checkContentType(exchange);
                    TableTasks newTask = gson.fromJson(requestBody, TableTasks.class);
                    respText = gson.toJson((InsertTask.insertTask(newTask)));
                    exchange.getResponseHeaders().set(contentType, jsonFormat);
                    exchange.sendResponseHeaders(200, respText.getBytes(StandardCharsets.UTF_8).length);
                    output = exchange.getResponseBody();
                    output.write(respText.getBytes());
                    output.flush();
                } catch (ApplicationException e) {
                    respText = gson.toJson(Utilities.makeErrResponseBody(e.getMessage()));
                    exchange.getResponseHeaders().set(contentType, jsonFormat);
                    exchange.sendResponseHeaders(e.getHttpCode(), respText.getBytes(StandardCharsets.UTF_8).length);
                    output = exchange.getResponseBody();
                    output.write(respText.getBytes());
                    output.flush();
                } catch (JsonParseException e) { // экспериметально обрабатываю ошибки при парсинге
                    handleReqWorkLogger.error("Failed json parsing, cause: " + e.getMessage());
                    respText = gson.toJson(Utilities.makeErrResponseBody("Failed json parsing"));
                    exchange.getResponseHeaders().set(contentType, jsonFormat);
                    exchange.sendResponseHeaders(415, respText.getBytes(StandardCharsets.UTF_8).length);
                    output = exchange.getResponseBody();
                    output.write(respText.getBytes());
                    output.flush();
                }
                break;

            case "PUT":
                try {
                    handleReqWorkLogger.info("Request from: " + exchange.getRemoteAddress().getAddress()
                            + " Method: " + exchange.getRequestMethod()
                            + " On server: " + exchange.getLocalAddress().getAddress()
                            + exchange.getHttpContext().getPath());
                    String requestHeaders = exchange.getRequestHeaders().entrySet().toString();
                    String requestBody = new String(exchange.getRequestBody().readAllBytes());
                    handleReqWorkLogger.debug("Headers: " + requestHeaders + "\n"
                            + "Body: " + requestBody);
                    Utilities.checkContentType(exchange);
                    TableTasks taskForUpdate = gson.fromJson(requestBody, TableTasks.class);
                    UpdateTask.updateTask(taskForUpdate);
                    exchange.getResponseHeaders().set(contentType, jsonFormat);
                    exchange.sendResponseHeaders(200, -1);
                } catch (ApplicationException e) {
                    respText = gson.toJson(Utilities.makeErrResponseBody(e.getMessage()));
                    exchange.getResponseHeaders().set(contentType, jsonFormat);
                    exchange.sendResponseHeaders(e.getHttpCode(), respText.getBytes(StandardCharsets.UTF_8).length);
                    output = exchange.getResponseBody();
                    output.write(respText.getBytes());
                    output.flush();
                } catch (JsonParseException e) { // экспериметально обрабатываю ошибки при парсинге
                    handleReqWorkLogger.error("Failed json parsing, cause: " + e.getMessage());
                    respText = gson.toJson(Utilities.makeErrResponseBody("Failed json parsing"));
                    exchange.getResponseHeaders().set(contentType, jsonFormat);
                    exchange.sendResponseHeaders(415, respText.getBytes(StandardCharsets.UTF_8).length);
                    output = exchange.getResponseBody();
                    output.write(respText.getBytes());
                    output.flush();
                }
                break;
            default:
                exchange.sendResponseHeaders(405, -1);// 405 Method Not Allowed
                break;
        }
        exchange.close();
    }
}
