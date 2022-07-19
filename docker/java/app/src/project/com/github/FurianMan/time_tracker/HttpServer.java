package com.github.FurianMan.time_tracker;

import com.github.FurianMan.time_tracker.Exceptions.HttpHeaderException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import org.slf4j.Logger;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

class MyHttpServer {
    private static String appVersion = Constants.getAppVersion();
    private static int serverPort = Constants.getServerPort();
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(TableUsers.class, new Utilities.TableUsersDeserializer<TableUsers>())
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
        } catch (IOException e) { // TODO непонятно стоит ли вообще обрабатывать исключение. Докер например не даст поднять на занятом порту
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
            String request;
            request = new String(exchange.getRequestBody().readAllBytes());
            TableUsers newUser = gson.fromJson(request, TableUsers.class);
            switch (exchange.getRequestMethod()) {
                case "GET":
                    respText = "This is GET";
                    try {
                        Utilities.checkContentType(exchange);
                    } catch (HttpHeaderException e) {
                        throw new RuntimeException(e);
                    }
                    exchange.sendResponseHeaders(200, respText.getBytes(StandardCharsets.UTF_8).length);
                    output = exchange.getResponseBody();
                    output.write(respText.getBytes());
                    output.flush();
                case "POST":
                    try {
                        Utilities.checkContentType(exchange);
                        MysqlUtilities.insertInto(newUser.getName(), newUser.getSurname(), newUser.getPosition(), newUser.getBirthday());
                        exchange.sendResponseHeaders(200, 0);
                        httpServerLogger.info("Пользователь успешно создан" + newUser);
                    } catch (JsonParseException e) {
                        httpServerLogger.error(e.getMessage());
                        respText = Utilities.serializeErrToJson(e.getMessage());
                        exchange.sendResponseHeaders(400, respText.getBytes(StandardCharsets.UTF_8).length);
                        output = exchange.getResponseBody();
                        output.write(respText.getBytes());
                        output.flush();
                    } catch (IOException e) {
                        httpServerLogger.error(e.getMessage());
                        throw new RuntimeException(e);
                    } catch (HttpHeaderException e) {
                        httpServerLogger.error(e.getMessage());
                        respText = Utilities.serializeErrToJson(e.getMessage());
                        exchange.getResponseHeaders().set(Constants.getHeaderContentType(), Constants.getApplicationJson());
                        exchange.sendResponseHeaders(415, respText.getBytes(StandardCharsets.UTF_8).length);
                        output = exchange.getResponseBody();
                        output.write(respText.getBytes());
                        output.flush();
                }

                case "PUT":
                    respText = "This is PUT";
                    exchange.sendResponseHeaders(200, respText.getBytes(StandardCharsets.UTF_8).length);
                    output = exchange.getResponseBody();
                    output.write(respText.getBytes());
                    output.flush();

                case "DELETE":
                    respText = "This is DELETE";
                    exchange.sendResponseHeaders(200, respText.getBytes(StandardCharsets.UTF_8).length);
                    output = exchange.getResponseBody();
                    output.write(respText.getBytes());
                    output.flush();

                default:
                    exchange.sendResponseHeaders(405, -1);// 405 Method Not Allowed
            }
        exchange.close();
        }
    }