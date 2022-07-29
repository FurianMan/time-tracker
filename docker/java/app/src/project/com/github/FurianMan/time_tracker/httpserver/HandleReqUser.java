package com.github.FurianMan.time_tracker.httpserver;

import com.github.FurianMan.time_tracker.exceptions.ApplicationException;
import com.github.FurianMan.time_tracker.mysqlTables.TableUsers;
import com.github.FurianMan.time_tracker.mysqlUtilities.DeleteUser;
import com.github.FurianMan.time_tracker.mysqlUtilities.GetUser;
import com.github.FurianMan.time_tracker.mysqlUtilities.InsertUser;
import com.github.FurianMan.time_tracker.mysqlUtilities.UpdateUser;
import com.github.FurianMan.time_tracker.utilities.Utilities;
import com.google.gson.JsonParseException;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import static com.github.FurianMan.time_tracker.Constants.*;
import static com.github.FurianMan.time_tracker.httpserver.HttpServerMain.*;

public class HandleReqUser {
    /**
    * Метод для обработки запросов по url /time-tracker/user
     *
     * @param exchange - http запрос от пользователя
    * */
    static void handleReqUser(HttpExchange exchange) throws IOException {
        String respText;
        OutputStream output;
        switch (exchange.getRequestMethod()) {
            case "GET":
                try {
                    handleReqUserLogger.info("Request from: " + exchange.getRemoteAddress().getAddress()
                            + " Method: " + exchange.getRequestMethod()
                            + " On server: " + exchange.getLocalAddress().getAddress()
                            + exchange.getHttpContext().getPath());
                    String requestHeaders = exchange.getRequestHeaders().entrySet().toString();
                    String requestBody = new String(exchange.getRequestBody().readAllBytes());
                    handleReqUserLogger.debug("Headers: " + requestHeaders + "\n"
                            + "Body: " + requestBody);
                    Utilities.checkContentType(exchange);
                    TableUsers userForSearching = gson.fromJson(requestBody, TableUsers.class);
                    respText = gson.toJson((GetUser.getUser(userForSearching)));
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
                    handleReqUserLogger.error("Failed json parsing, cause: " + e.getMessage());
                    respText = gson.toJson(Utilities.makeErrResponseBody("Failed json parsing"));
                    exchange.getResponseHeaders().set(contentType, jsonFormat);
                    exchange.sendResponseHeaders(415, respText.getBytes(StandardCharsets.UTF_8).length);
                    output = exchange.getResponseBody();
                    output.write(respText.getBytes());
                    output.flush();
                }
                break;
            case "POST":
                try {
                    handleReqUserLogger.info("Request from: " + exchange.getRemoteAddress().getAddress()
                            + " Method: " + exchange.getRequestMethod()
                            + " On server: " + exchange.getLocalAddress().getAddress()
                            + exchange.getHttpContext().getPath());
                    String requestHeaders = exchange.getRequestHeaders().entrySet().toString();
                    String requestBody = new String(exchange.getRequestBody().readAllBytes());
                    handleReqUserLogger.debug("Headers: " + requestHeaders + "\n"
                            + "Body: " + requestBody);
                    Utilities.checkContentType(exchange);
                    TableUsers newUser = gson.fromJson(requestBody, TableUsers.class);
                    respText = gson.toJson((InsertUser.insertUser(newUser)));
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
                    handleReqUserLogger.error("Failed json parsing, cause: " + e.getMessage());
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
                    handleReqUserLogger.info("Request from: " + exchange.getRemoteAddress().getAddress()
                            + " Method: " + exchange.getRequestMethod()
                            + " On server: " + exchange.getLocalAddress().getAddress()
                            + exchange.getHttpContext().getPath());
                    String requestHeaders = exchange.getRequestHeaders().entrySet().toString();
                    String requestBody = new String(exchange.getRequestBody().readAllBytes());
                    handleReqUserLogger.debug("Headers: " + requestHeaders + "\n"
                            + "Body: " + requestBody);
                    Utilities.checkContentType(exchange);
                    TableUsers userForUpdate = gson.fromJson(requestBody, TableUsers.class);
                    UpdateUser.updateUser(userForUpdate);
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
                    handleReqUserLogger.error("Failed json parsing, cause: " + e.getMessage());
                    respText = gson.toJson(Utilities.makeErrResponseBody("Failed json parsing"));
                    exchange.getResponseHeaders().set(contentType, jsonFormat);
                    exchange.sendResponseHeaders(415, respText.getBytes(StandardCharsets.UTF_8).length);
                    output = exchange.getResponseBody();
                    output.write(respText.getBytes());
                    output.flush();
                }
                break;

            case "DELETE":
                try {
                    handleReqUserLogger.info("Request from: " + exchange.getRemoteAddress().getAddress()
                            + " Method: " + exchange.getRequestMethod()
                            + " On server: " + exchange.getLocalAddress().getAddress()
                            + exchange.getHttpContext().getPath());
                    String requestHeaders = exchange.getRequestHeaders().entrySet().toString();
                    String requestBody = new String(exchange.getRequestBody().readAllBytes());
                    handleReqUserLogger.debug("Headers: " + requestHeaders + "\n"
                            + "Body: " + requestBody);
                    Utilities.checkContentType(exchange);
                    TableUsers userForDel = gson.fromJson(requestBody, TableUsers.class);
                    DeleteUser.deleteUser(userForDel);
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
                    handleReqUserLogger.error("Failed json parsing, cause: " + e.getMessage());
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
