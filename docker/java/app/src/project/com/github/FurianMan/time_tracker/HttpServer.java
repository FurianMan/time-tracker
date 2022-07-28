package com.github.FurianMan.time_tracker;

import com.github.FurianMan.time_tracker.exceptions.ApplicationException;
import com.github.FurianMan.time_tracker.mysqlTables.TableTasks;
import com.github.FurianMan.time_tracker.mysqlTables.TableUsers;
import com.github.FurianMan.time_tracker.mysqlUtilities.*;
import com.github.FurianMan.time_tracker.utilities.RequestUserStats;
import com.github.FurianMan.time_tracker.utilities.Utilities;
import com.google.gson.*;
import com.google.gson.annotations.Expose;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import org.slf4j.Logger;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

class MyHttpServer {
    private static final String contentType = Constants.getHeaderContentType();
    private static final String jsonFormat = Constants.getApplicationJson();
    private static final String appVersion = Constants.getAppVersion();
    private static final int serverPort = Constants.getServerPort();
//    private static String encoding = Constants.getEncoding();
    private static final Gson gson = new GsonBuilder()
            .setExclusionStrategies(new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes f) {
                    return f.getAnnotation(Expose.class) != null;
                }

                @Override
                public boolean shouldSkipClass(Class<?> c) {
                    return false;
                }
            })
            .create();
    private static final Logger httpServerLogger = Constants.getHttpServerLogger();

    static void startServer() {
        try {
            com.sun.net.httpserver.HttpServer server = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(serverPort), 0);
            HttpContext contVer = server.createContext("/time-tracker/version");
            HttpContext contUser = server.createContext("/time-tracker/user");
            HttpContext contWork = server.createContext("/time-tracker/user/work");
            HttpContext contWorkStats = server.createContext("/time-tracker/user/work/stats");
            contVer.setHandler(MyHttpServer::handleRequestVersion);
            contUser.setHandler(MyHttpServer::handleRequestUser);
            contWork.setHandler(MyHttpServer::handleRequestWork);
            contWorkStats.setHandler(MyHttpServer::handleRequestWorkStats);
            server.start();
            httpServerLogger.info("HTTP Server has been started successfully");
        } catch (
                IOException e) { // TODO непонятно стоит ли вообще обрабатывать исключение. Докер например не даст поднять на занятом порту
            throw new RuntimeException(e);
        }
    }

    static void handleRequestVersion(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            httpServerLogger.info("Request from: " + exchange.getRemoteAddress().getAddress()
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

    static void handleRequestUser(HttpExchange exchange) throws IOException {
        String respText;
        OutputStream output;
        switch (exchange.getRequestMethod()) {
            case "GET": //TODO сделать возможным поиск по любому параметру
                httpServerLogger.info("Request from: " + exchange.getRemoteAddress().getAddress()
                        + " Method: " + exchange.getRequestMethod()
                        + " On server: " + exchange.getLocalAddress().getAddress()
                        + exchange.getHttpContext().getPath());
                try {
//                    httpServerLogger.info("Request from: " + exchange.getRemoteAddress().getAddress()
//                            + " Method: " + exchange.getRequestMethod()
//                            + " On server: " + exchange.getLocalAddress().getAddress()
//                            + exchange.getHttpContext().getPath());
                    Utilities.checkContentType(exchange);
                    String request = new String(exchange.getRequestBody().readAllBytes());
                    TableUsers userForSearching = gson.fromJson(request, TableUsers.class);
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
                    httpServerLogger.error("Failed json parsing \n" + e.getMessage());
                    respText = gson.toJson(Utilities.makeErrResponseBody(e.getMessage()));
                    exchange.getResponseHeaders().set(contentType, jsonFormat);
                    exchange.sendResponseHeaders(415, respText.getBytes(StandardCharsets.UTF_8).length);
                    output = exchange.getResponseBody();
                    output.write(respText.getBytes());
                    output.flush();
                }
            case "POST":
                httpServerLogger.info("Request from: " + exchange.getRemoteAddress().getAddress()
                        + " Method: " + exchange.getRequestMethod()
                        + " On server: " + exchange.getLocalAddress().getAddress()
                        + exchange.getHttpContext().getPath());
                try {
//                    httpServerLogger.info("Request from: " + exchange.getRemoteAddress().getAddress()
//                            + " Method: " + exchange.getRequestMethod()
//                            + " On server: " + exchange.getLocalAddress().getAddress()
//                            + exchange.getHttpContext().getPath());
                    Utilities.checkContentType(exchange);
                    String request = new String(exchange.getRequestBody().readAllBytes());
                    TableUsers newUser = gson.fromJson(request, TableUsers.class);
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
                    httpServerLogger.error("Failed json parsing \n" + e.getMessage());
                    respText = gson.toJson(Utilities.makeErrResponseBody(e.getMessage()));
                    exchange.getResponseHeaders().set(contentType, jsonFormat);
                    exchange.sendResponseHeaders(415, respText.getBytes(StandardCharsets.UTF_8).length);
                    output = exchange.getResponseBody();
                    output.write(respText.getBytes());
                    output.flush();
                }

            case "PUT":
                try {
                    httpServerLogger.info("Request from: " + exchange.getRemoteAddress().getAddress()
                            + " Method: " + exchange.getRequestMethod()
                            + " On server: " + exchange.getLocalAddress().getAddress()
                            + exchange.getHttpContext().getPath());
                    Utilities.checkContentType(exchange);
                    String request = new String(exchange.getRequestBody().readAllBytes());
                    TableUsers userForUpdate = gson.fromJson(request, TableUsers.class);
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
                    httpServerLogger.error("Failed json parsing \n" + e.getMessage());
                    respText = gson.toJson(Utilities.makeErrResponseBody(e.getMessage()));
                    exchange.getResponseHeaders().set(contentType, jsonFormat);
                    exchange.sendResponseHeaders(415, respText.getBytes(StandardCharsets.UTF_8).length);
                    output = exchange.getResponseBody();
                    output.write(respText.getBytes());
                    output.flush();
                }


            case "DELETE":
                try {
                    httpServerLogger.info("Request from: " + exchange.getRemoteAddress().getAddress()
                            + " Method: " + exchange.getRequestMethod()
                            + " On server: " + exchange.getLocalAddress().getAddress()
                            + exchange.getHttpContext().getPath());
                    Utilities.checkContentType(exchange);
                    String request = new String(exchange.getRequestBody().readAllBytes());
                    TableUsers userForDel = gson.fromJson(request, TableUsers.class);
                    DeleteUser.deleteUser(userForDel);
                    exchange.getResponseHeaders().set(contentType, jsonFormat);
                    exchange.sendResponseHeaders(200, -1);
                } catch (ApplicationException e) {
                    respText = gson.toJson(Utilities.makeErrResponseBody(e.getMessage()));
                    exchange.getResponseHeaders().set(Constants.getHeaderContentType(), jsonFormat);
                    exchange.sendResponseHeaders(e.getHttpCode(), respText.getBytes(StandardCharsets.UTF_8).length);
                    output = exchange.getResponseBody();
                    output.write(respText.getBytes());
                    output.flush();
                } catch (JsonParseException e) { // экспериметально обрабатываю ошибки при парсинге
                    httpServerLogger.error("Failed json parsing \n" + e.getMessage());
                    respText = gson.toJson(Utilities.makeErrResponseBody(e.getMessage()));
                    exchange.getResponseHeaders().set(contentType, jsonFormat);
                    exchange.sendResponseHeaders(415, respText.getBytes(StandardCharsets.UTF_8).length);
                    output = exchange.getResponseBody();
                    output.write(respText.getBytes());
                    output.flush();
                }

            default:
                exchange.sendResponseHeaders(405, -1);// 405 Method Not Allowed
        }
        exchange.close();
    }
    static void handleRequestWork(HttpExchange exchange) throws IOException {
        String respText;
        OutputStream output;
        switch (exchange.getRequestMethod()) {
            case "POST":
                try {
                    httpServerLogger.info("Request from: " + exchange.getRemoteAddress().getAddress()
                            + " Method: " + exchange.getRequestMethod()
                            + " On server: " + exchange.getLocalAddress().getAddress()
                            + exchange.getHttpContext().getPath());
                    Utilities.checkContentType(exchange);
                    String request = new String(exchange.getRequestBody().readAllBytes());
                    TableTasks newTask = gson.fromJson(request, TableTasks.class);
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
                    httpServerLogger.error("Failed json parsing \n" + e.getMessage());
                    respText = gson.toJson(Utilities.makeErrResponseBody(e.getMessage()));
                    exchange.getResponseHeaders().set(contentType, jsonFormat);
                    exchange.sendResponseHeaders(415, respText.getBytes(StandardCharsets.UTF_8).length);
                    output = exchange.getResponseBody();
                    output.write(respText.getBytes());
                    output.flush();
                }

            case "PUT":
                try {
                    httpServerLogger.info("Request from: " + exchange.getRemoteAddress().getAddress()
                            + " Method: " + exchange.getRequestMethod()
                            + " On server: " + exchange.getLocalAddress().getAddress()
                            + exchange.getHttpContext().getPath());
                    Utilities.checkContentType(exchange);
                    String request = new String(exchange.getRequestBody().readAllBytes());
                    TableTasks taskForUpdate = gson.fromJson(request, TableTasks.class);
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
                    httpServerLogger.error("Failed json parsing \n" + e.getMessage());
                    respText = gson.toJson(Utilities.makeErrResponseBody(e.getMessage()));
                    exchange.getResponseHeaders().set(contentType, jsonFormat);
                    exchange.sendResponseHeaders(415, respText.getBytes(StandardCharsets.UTF_8).length);
                    output = exchange.getResponseBody();
                    output.write(respText.getBytes());
                    output.flush();
                }
            default:
                exchange.sendResponseHeaders(405, -1);// 405 Method Not Allowed
        }
        exchange.close();
    }

    static void handleRequestWorkStats(HttpExchange exchange) throws IOException {
        String respText;
        OutputStream output;
        switch (exchange.getRequestMethod()) {
            case "GET": //TODO сделать возможным поиск по любому параметру
                try {
                    httpServerLogger.info("Request from: " + exchange.getRemoteAddress().getAddress()
                            + " Method: " + exchange.getRequestMethod()
                            + " On server: " + exchange.getLocalAddress().getAddress()
                            + exchange.getHttpContext().getPath());
                    Utilities.checkContentType(exchange);
                    String request = new String(exchange.getRequestBody().readAllBytes());
                    RequestUserStats reqParams = gson.fromJson(request, RequestUserStats.class);
                    respText = gson.toJson(Utilities.defineWayToGetStats(reqParams));
//                    respText = gson.toJson(GetWorkStats.getWorkStats(taskForSearchStats));
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
                    httpServerLogger.error("Failed json parsing \n" + e.getMessage());
                    respText = gson.toJson(Utilities.makeErrResponseBody(e.getMessage()));
                    exchange.getResponseHeaders().set(contentType, jsonFormat);
                    exchange.sendResponseHeaders(415, respText.getBytes(StandardCharsets.UTF_8).length);
                    output = exchange.getResponseBody();
                    output.write(respText.getBytes());
                    output.flush();
                }
//                } catch (UnexpectedErr e) {
//                    respText = gson.toJson(Utilities.makeErrResponseBody(e.getMessage()));
//                    exchange.getResponseHeaders().set(Constants.getHeaderContentType(), jsonFormat);
//                    exchange.sendResponseHeaders(500, respText.getBytes(StandardCharsets.UTF_8).length);
//                    output = exchange.getResponseBody();
//                    output.write(respText.getBytes());
//                    output.flush();
//                }
            case "DELETE":
                try {
                    httpServerLogger.info("Request from: " + exchange.getRemoteAddress().getAddress()
                            + " Method: " + exchange.getRequestMethod()
                            + " On server: " + exchange.getLocalAddress().getAddress()
                            + exchange.getHttpContext().getPath());
                    Utilities.checkContentType(exchange);
                    String request = new String(exchange.getRequestBody().readAllBytes());
                    TableTasks clearStats = gson.fromJson(request, TableTasks.class);
                    ClearStats.clearUserStats(clearStats);
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
                    httpServerLogger.error("Failed json parsing \n" + e.getMessage());
                    respText = gson.toJson(Utilities.makeErrResponseBody(e.getMessage()));
                    exchange.getResponseHeaders().set(contentType, jsonFormat);
                    exchange.sendResponseHeaders(415, respText.getBytes(StandardCharsets.UTF_8).length);
                    output = exchange.getResponseBody();
                    output.write(respText.getBytes());
                    output.flush();
                }

            default:
                exchange.sendResponseHeaders(405, -1);// 405 Method Not Allowed
        }
        exchange.close();
    }
}