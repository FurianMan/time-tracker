package com.github.FurianMan.time_tracker.httpserver;

import com.google.gson.*;
import com.google.gson.annotations.Expose;
import com.sun.net.httpserver.HttpContext;

import java.io.*;
import java.net.InetSocketAddress;

import static com.github.FurianMan.time_tracker.Constants.httpServerMainLogger;
import static com.github.FurianMan.time_tracker.Constants.serverPort;

public class HttpServerMain {
    public static final Gson gson = new GsonBuilder()
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

    /**
     * Метод для запуска http-сервера
     *
     * */
    public static void startServer() {
        try {
            com.sun.net.httpserver.HttpServer server = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(serverPort), 0);
            HttpContext contVer = server.createContext("/time-tracker/version");
            HttpContext contUser = server.createContext("/time-tracker/user");
            HttpContext contWork = server.createContext("/time-tracker/user/work");
            HttpContext contWorkStats = server.createContext("/time-tracker/user/work/stats");
            contVer.setHandler(HandleReqVersion::handleReqVersion);
            contUser.setHandler(HandleReqUser::handleReqUser);
            contWork.setHandler(HandleReqWork::handleReqWork);
            contWorkStats.setHandler(HandleReqWorkStats::handleReqWorkStats);
            server.start();
            httpServerMainLogger.info("HTTP Server has been started successfully");
        } catch (
                IOException e) { // TODO непонятно стоит ли вообще обрабатывать исключение. Докер например не даст поднять на занятом порту
            throw new RuntimeException(e);
        }
    }
}