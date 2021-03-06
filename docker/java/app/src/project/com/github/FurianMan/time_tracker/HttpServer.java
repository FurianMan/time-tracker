package com.github.FurianMan.time_tracker;

import com.github.FurianMan.time_tracker.exceptions.ApplicationException;
import com.github.FurianMan.time_tracker.mysqlTables.TableUsers;
import com.github.FurianMan.time_tracker.mysqlUtilities.DeleteUser;
import com.github.FurianMan.time_tracker.mysqlUtilities.GetUser;
import com.github.FurianMan.time_tracker.mysqlUtilities.InsertUser;
import com.github.FurianMan.time_tracker.mysqlUtilities.UpdateUser;
import com.google.gson.*;
import com.google.gson.annotations.Expose;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import org.slf4j.Logger;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

class MyHttpServer {
    private static String appVersion = Constants.getAppVersion();
    private static int serverPort = Constants.getServerPort();
    private static final Gson gsonUsers = new GsonBuilder()
//            .registerTypeAdapter(TableUsers.class, new Utilities.TableUsersDeserializer<TableUsers>())
//            .registerTypeAdapter(TableUsers.class, new Utilities.TableUsersDeserializer<TableUsers>())
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
//            .addSerializationExclusionStrategy(new ExclusionStrategy() {
//                @Override
//                public boolean shouldSkipField(FieldAttributes f) {
//                    Expose expose = f.getAnnotation(Expose.class);
//                    return expose != null && !expose.serialize();
//                }
//
//                @Override
//                public boolean shouldSkipClass(Class<?> c) {
//                    return false;
//                }
//            })
//            .addDeserializationExclusionStrategy(new ExclusionStrategy() {
//                @Override
//                public boolean shouldSkipField(FieldAttributes f) {
//                    Expose expose = f.getAnnotation(Expose.class);
//                    return expose != null && !expose.deserialize();
//                }
//
//                @Override
//                public boolean shouldSkipClass(Class<?> c) {
//                    return false;
//                }
//            })
            .create();
    private static final Gson gsonErrBody = new GsonBuilder()
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
            contVer.setHandler(MyHttpServer::handleRequestVersion);
            contUser.setHandler(MyHttpServer::handleRequestUser);
            server.start();
            httpServerLogger.info("HTTP Server has been started successfully");
        } catch (
                IOException e) { // TODO ?????????????????? ?????????? ???? ???????????? ???????????????????????? ????????????????????. ?????????? ???????????????? ???? ???????? ?????????????? ???? ?????????????? ??????????
            throw new RuntimeException(e);
        }
    }

    static void handleRequestVersion(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
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
            case "GET": //TODO ?????????????? ?????????????????? ?????????? ???? ???????????? ??????????????????
                try {
                    Utilities.checkContentType(exchange);
                    String request = new String(exchange.getRequestBody().readAllBytes());
                    TableUsers userForSearching = gsonUsers.fromJson(request, TableUsers.class);
                    respText = gsonUsers.toJson((GetUser.getUser(userForSearching)));
                    exchange.getResponseHeaders().set(Constants.getHeaderContentType(), Constants.getApplicationJson());
                    exchange.sendResponseHeaders(200, respText.getBytes(StandardCharsets.UTF_8).length);
                    output = exchange.getResponseBody();
                    output.write(respText.getBytes());
                    output.flush();
                } catch (ApplicationException e) {
                    respText = gsonErrBody.toJson(Utilities.makeErrResponseBody(e.getMessage()));
                    exchange.getResponseHeaders().set(Constants.getHeaderContentType(), Constants.getApplicationJson());
                    exchange.sendResponseHeaders(e.getHttpCode(), respText.getBytes(StandardCharsets.UTF_8).length);
                    output = exchange.getResponseBody();
                    output.write(respText.getBytes());
                    output.flush();
                }
            case "POST":
                try {
                    Utilities.checkContentType(exchange);
                    String request = new String(exchange.getRequestBody().readAllBytes());
                    TableUsers newUser = gsonUsers.fromJson(request, TableUsers.class);
                    Utilities.validateUserFields(newUser);
                    respText = gsonUsers.toJson((InsertUser.insertUser(newUser)));
                    exchange.getResponseHeaders().set(Constants.getHeaderContentType(), Constants.getApplicationJson());
                    exchange.sendResponseHeaders(200, respText.getBytes(StandardCharsets.UTF_8).length);
                    output = exchange.getResponseBody();
                    output.write(respText.getBytes());
                    output.flush();
                } catch (JsonParseException e) {
                    httpServerLogger.error(e.getMessage());
                    respText = gsonErrBody.toJson(Utilities.makeErrResponseBody(e.getMessage()));
                    exchange.sendResponseHeaders(400, respText.getBytes(StandardCharsets.UTF_8).length);
                    output = exchange.getResponseBody();
                    output.write(respText.getBytes());
                    output.flush();
                } catch (ApplicationException e) {
                    respText = gsonErrBody.toJson(Utilities.makeErrResponseBody(e.getMessage()));
                    exchange.getResponseHeaders().set(Constants.getHeaderContentType(), Constants.getApplicationJson());
                    exchange.sendResponseHeaders(e.getHttpCode(), respText.getBytes(StandardCharsets.UTF_8).length);
                    output = exchange.getResponseBody();
                    output.write(respText.getBytes());
                    output.flush();
                }

            case "PUT":
                try {
                    Utilities.checkContentType(exchange);
                    String request = new String(exchange.getRequestBody().readAllBytes());
                    TableUsers userForUpdate = gsonUsers.fromJson(request, TableUsers.class);
                    Utilities.validateUserFields(userForUpdate);
                    UpdateUser.updateUser(userForUpdate);
                    exchange.getResponseHeaders().set(Constants.getHeaderContentType(), Constants.getApplicationJson());
                    exchange.sendResponseHeaders(200, -1);
                } catch (ApplicationException e) {
                    respText = gsonErrBody.toJson(Utilities.makeErrResponseBody(e.getMessage()));
                    exchange.getResponseHeaders().set(Constants.getHeaderContentType(), Constants.getApplicationJson());
                    exchange.sendResponseHeaders(e.getHttpCode(), respText.getBytes(StandardCharsets.UTF_8).length);
                    output = exchange.getResponseBody();
                    output.write(respText.getBytes());
                    output.flush();
                }


            case "DELETE":
                try {
                    Utilities.checkContentType(exchange);
                    String request = new String(exchange.getRequestBody().readAllBytes());
                    TableUsers userForDel = gsonUsers.fromJson(request, TableUsers.class);
                    DeleteUser.deleteUser(userForDel);
                    exchange.getResponseHeaders().set(Constants.getHeaderContentType(), Constants.getApplicationJson());
                    exchange.sendResponseHeaders(200, -1);
                } catch (ApplicationException e) {
                    respText = gsonErrBody.toJson(Utilities.makeErrResponseBody(e.getMessage()));
                    exchange.getResponseHeaders().set(Constants.getHeaderContentType(), Constants.getApplicationJson());
                    exchange.sendResponseHeaders(e.getHttpCode(), respText.getBytes(StandardCharsets.UTF_8).length);
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