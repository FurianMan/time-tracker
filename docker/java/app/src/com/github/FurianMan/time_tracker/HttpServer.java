package com.github.FurianMan.time_tracker;

import com.google.gson.Gson;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

class MyHttpServer {
    private static String appVersion = "Time Tracker Version 0.1\n";
    private static int serverPort = Integer.parseInt(Utilities.getConstants("APP_PORT"));
    private static com.sun.net.httpserver.HttpServer server = null;
    static void startServer () {
        try {
            server = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(serverPort), 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        server.createContext("/time-tracker/version", (httpExchange -> {
            if ("GET".equals(httpExchange.getRequestMethod())) {
                String respText = appVersion;
                httpExchange.sendResponseHeaders(200, respText.getBytes(StandardCharsets.UTF_8).length);
                OutputStream output = httpExchange.getResponseBody();
                output.write(respText.getBytes());
                output.flush();
            } else {
                httpExchange.sendResponseHeaders(405, -1);// 405 Method Not Allowed
            }
            httpExchange.close();
        }));
        server.createContext("/time-tracker/user", (httpExchange -> {
            String respText;
            OutputStream output;
            String request;
            Map<String,Object> requestMap;
            BufferedReader objReader;
            switch (httpExchange.getRequestMethod()) {
                case "GET":
                    respText = "This is GET";
                    httpExchange.sendResponseHeaders(200, respText.getBytes(StandardCharsets.UTF_8).length);
                    output = httpExchange.getResponseBody();
                    output.write(respText.getBytes());
                    output.flush();
                case "POST":
                    respText = "This is POST";
//                    requestMap = (HashMap<String, String>) httpExchange.getRequestBody().readAllBytes();
                    request = new String(httpExchange.getRequestBody().readAllBytes());
//                    checkFields(request);
//                    requestMap = new Gson().fromJson(request, MysqlTables.Users.class);
//                    System.out.println(requestMap.values());
                    MysqlTables.Users newUser = new Gson().fromJson(request, MysqlTables.Users.class);
                    MysqlUtilities insertNewUser = new MysqlUtilities();
                    insertNewUser.insertInto(newUser.getName(), newUser.getSurname(), newUser.getPosition(), newUser.getBirthday());
                    httpExchange.sendResponseHeaders(200, respText.getBytes(StandardCharsets.UTF_8).length);
                    output = httpExchange.getResponseBody();
                    output.write(respText.getBytes());
                    output.flush();

                case "PUT":
                    respText = "This is PUT";
                    httpExchange.sendResponseHeaders(200, respText.getBytes(StandardCharsets.UTF_8).length);
                    output = httpExchange.getResponseBody();
                    output.write(respText.getBytes());
                    output.flush();

                case "DELETE":
                    respText = "This is DELETE";
                    httpExchange.sendResponseHeaders(200, respText.getBytes(StandardCharsets.UTF_8).length);
                    output = httpExchange.getResponseBody();
                    output.write(respText.getBytes());
                    output.flush();

                default:
                    httpExchange.sendResponseHeaders(405, -1);// 405 Method Not Allowed
            }
            httpExchange.close();
        }));
        server.setExecutor(null);
        server.start();
    }
    static void checkFields (String request) {
        System.out.println(request);
    }
}