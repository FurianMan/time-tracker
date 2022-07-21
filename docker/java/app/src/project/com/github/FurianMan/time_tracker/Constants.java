package com.github.FurianMan.time_tracker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants {
    private static final Logger httpServerLogger = LoggerFactory.getLogger(MyHttpServer.class);
    private static final Logger mysqlLogger = LoggerFactory.getLogger(MysqlUtilities.class);
    private static final Logger utilitieslLogger = LoggerFactory.getLogger(Utilities.class);
    private static final String driverName = "com.mysql.cj.jdbc.Driver";
    private static final String connectionString = "jdbc:mysql://db:3306/time_tracker";
    private static final String dbLogin = "javauser";
    private static final String dbPassword = "javapassword";
    private static String appVersion = "Time Tracker Version 0.1\n";
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json; charset=UTF-8";
    private static int serverPort = Integer.parseInt(Utilities.getConstants("APP_PORT"));

    public static Logger getHttpServerLogger() {
        return httpServerLogger;
    }

    public static Logger getMysqlLogger() {
        return mysqlLogger;
    }

    public static Logger getUtilitieslLogger() {
        return utilitieslLogger;
    }

    public static String getDbPassword() {
        return dbPassword;
    }

    public static int getServerPort() {
        return serverPort;
    }

    public static String getAppVersion() {
        return appVersion;
    }

    public static String getDbLogin() {
        return dbLogin;
    }

    public static String getConnectionString() {
        return connectionString;
    }

    public static String getDriverName() {
        return driverName;
    }

    public static String getApplicationJson() {
        return APPLICATION_JSON;
    }

    public static String getHeaderContentType() {
        return HEADER_CONTENT_TYPE;
    }
}
