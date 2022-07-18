package com.github.FurianMan.time_tracker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

class MyHttpServer {
    private static String appVersion = Constants.getAppVersion();
    private static int serverPort = Constants.getServerPort();
    private static com.sun.net.httpserver.HttpServer server = null;
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(TableUsers.class, new Utilities.TableUsersDeserializer<TableUsers>())
            .create();

    static void startServer() {
        try {
            server = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(serverPort), 0);
            HttpContext contVer = server.createContext("/time-tracker/version");
            HttpContext contUser = server.createContext("/time-tracker/user");
            contVer.setHandler(MyHttpServer::handleRequestVersion);
            contUser.setHandler(MyHttpServer::handleRequestUser);
            server.setExecutor(null);
            server.start();
        } catch (IOException e) {
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
            switch (exchange.getRequestMethod()) {
                case "GET":
                    respText = "This is GET";
                    exchange.sendResponseHeaders(200, respText.getBytes(StandardCharsets.UTF_8).length);
                    output = exchange.getResponseBody();
                    output.write(respText.getBytes());
                    output.flush();
                case "POST":
                    respText = "This is POST";
                    request = new String(exchange.getRequestBody().readAllBytes());
                    try {
                        Utilities.checkContentType(exchange);
                        TableUsers newUser = gson.fromJson(request, TableUsers.class);
                        MysqlUtilities.insertInto(newUser.getName(), newUser.getSurname(), newUser.getPosition(), newUser.getBirthday());
                        exchange.sendResponseHeaders(200, respText.getBytes(StandardCharsets.UTF_8).length);
                        output = exchange.getResponseBody();
                        output.write(respText.getBytes());
                        output.flush();
                    } catch (JsonParseException e) {
                        exchange.sendResponseHeaders(400, e.getMessage().getBytes(StandardCharsets.UTF_8).length);
                        exchange.getResponseHeaders().set("Content-Type", "application/json");
                        output = exchange.getResponseBody();
                        output.write(e.getMessage().getBytes());
                        output.flush();
                    } catch (IOException e) {
                        System.out.println("Поймали общее исключение");
                        throw new RuntimeException(e);
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