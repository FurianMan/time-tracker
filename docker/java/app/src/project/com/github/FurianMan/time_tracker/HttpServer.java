package com.github.FurianMan.time_tracker;

import com.google.gson.Gson;
//import com.google.gson.stream.JsonReader;
//import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

class MyHttpServer {
    private static String appVersion = "Time Tracker Version 0.1\n";
    private static int serverPort = 6699;
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
            String input;
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
                    input = new String(httpExchange.getRequestBody().readAllBytes());
                    MysqlTables.Users newUser = new Gson().fromJson(input, MysqlTables.Users.class);
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
}