package com.github.FurianMan.time_tracker;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        String appVersion = "Time Tracker Version 0.1\n";
        int serverPort = 6699;
        HttpServer server = null;
        try {
            server = HttpServer.create(new InetSocketAddress(serverPort), 0);
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
        server.setExecutor(null);
        server.start();
//        new com.github.FurianMan.time_tracker.MysqlUtilities().connectToDatabase();
        //MysqlTableUsers MysqlData = new MysqlTableUsers("Егор", "Иванович", "QA", "1996-05-08");
        //new MysqlUtilities().InsertInto("INSERT INTO time_tracker.Users (name, surname, position, birthday) VALUES ('Влад', 'Рих', 'QA', '1993-10-01'); ");

    }
    interface TimeTrackerApi {
        
    }
}
