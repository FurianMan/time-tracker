package com.github.FurianMan.time_tracker;

public class Constants {
    private static final String driverName = "com.mysql.cj.jdbc.Driver";
    private static final String connectionString = "jdbc:mysql://db:3306/time_tracker";
    private static final String dbLogin = "javauser";
    private static final String dbPassword = "javapassword";
    private static String appVersion = "Time Tracker Version 0.1\n";
    private static int serverPort = Integer.parseInt(Utilities.getConstants("APP_PORT"));

    public static String getDriverName() {
        return driverName;
    }

    public static String getConnectionString() {
        return connectionString;
    }

    public static String getDbLogin() {
        return dbLogin;
    }

    public static String getDbPassword() {
        return dbPassword;
    }

    public static String getAppVersion() {
        return appVersion;
    }

    public static int getServerPort() {
        return serverPort;
    }
}
