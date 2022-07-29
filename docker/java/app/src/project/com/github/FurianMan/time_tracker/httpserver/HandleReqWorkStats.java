package com.github.FurianMan.time_tracker.httpserver;

import com.github.FurianMan.time_tracker.exceptions.ApplicationException;
import com.github.FurianMan.time_tracker.mysqlTables.TableTasks;
import com.github.FurianMan.time_tracker.mysqlUtilities.ClearStats;
import com.github.FurianMan.time_tracker.utilities.RequestUserStats;
import com.github.FurianMan.time_tracker.utilities.Utilities;
import com.google.gson.JsonParseException;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import static com.github.FurianMan.time_tracker.Constants.*;
import static com.github.FurianMan.time_tracker.httpserver.HttpServerMain.*;

public class HandleReqWorkStats {
    /**
     * Метод для обработки запросов по url /time-tracker/user/work/stats
     *
     * @param exchange - http запрос от пользователя
     * */
    static void handleReqWorkStats(HttpExchange exchange) throws IOException {
        String respText;
        OutputStream output;
        switch (exchange.getRequestMethod()) {
            case "GET": //TODO сделать возможным поиск по любому параметру
                try {
                    handleReqWorkStatsLogger.info("Request from: " + exchange.getRemoteAddress().getAddress()
                            + " Method: " + exchange.getRequestMethod()
                            + " On server: " + exchange.getLocalAddress().getAddress()
                            + exchange.getHttpContext().getPath());
                    String requestHeaders = exchange.getRequestHeaders().entrySet().toString();
                    String requestBody = new String(exchange.getRequestBody().readAllBytes());
                    handleReqWorkStatsLogger.debug("Headers: " + requestHeaders + "\n"
                            + "Body: " + requestBody);
                    Utilities.checkContentType(exchange);
                    RequestUserStats reqParams = gson.fromJson(requestBody, RequestUserStats.class);
                    respText = gson.toJson(Utilities.defineWayToGetStats(reqParams));
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
                    handleReqWorkStatsLogger.error("Failed json parsing, cause: " + e.getMessage());
                    respText = gson.toJson(Utilities.makeErrResponseBody("Failed json parsing"));
                    exchange.getResponseHeaders().set(contentType, jsonFormat);
                    exchange.sendResponseHeaders(415, respText.getBytes(StandardCharsets.UTF_8).length);
                    output = exchange.getResponseBody();
                    output.write(respText.getBytes());
                    output.flush();
                }
                break;
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
                    handleReqWorkStatsLogger.info("Request from: " + exchange.getRemoteAddress().getAddress()
                            + " Method: " + exchange.getRequestMethod()
                            + " On server: " + exchange.getLocalAddress().getAddress()
                            + exchange.getHttpContext().getPath());
                    String requestHeaders = exchange.getRequestHeaders().entrySet().toString();
                    String requestBody = new String(exchange.getRequestBody().readAllBytes());
                    handleReqWorkStatsLogger.debug("Headers: " + requestHeaders + "\n"
                            + "Body: " + requestBody);
                    Utilities.checkContentType(exchange);
                    TableTasks clearStats = gson.fromJson(requestBody, TableTasks.class);
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
                    handleReqWorkStatsLogger.error("Failed json parsing, cause: " + e.getMessage());
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
