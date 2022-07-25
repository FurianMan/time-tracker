package com.github.FurianMan.time_tracker.mysqlUtilities;

import com.github.FurianMan.time_tracker.Constants;
import com.github.FurianMan.time_tracker.exceptions.ApplicationException;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.concurrent.TimeUnit;

public class CheckConnectionToDB {
    private static final String driverName = Constants.getDriverName();
    private static final String connectionString = Constants.getConnectionString();
    private static final String login = Constants.getDbLogin();
    private static final String password = Constants.getDbPassword();
    private static final Logger mysqlLogger = Constants.getMysqlLogger();
    static Connection conn;
    public static void checkConnectionToDB () { //TODO в целом работает, но можно еще проверять, если вдруг база упадет и переподключаться к ней
        mysqlLogger.info("Connecting to database...");
        while (true) {
            try {
                Class.forName(driverName);
                conn = DriverManager.getConnection(connectionString, login, password);
                break;
            } catch (Exception e) {
                mysqlLogger.error("Connection failed, new try starting...");
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException ex) {
                    mysqlLogger.error(e.getMessage());
                }
            }
        }
        mysqlLogger.info("Database connection has been installed successfully");
        try {
            DisconnectToDB.disconnectToDatabase(conn);
        } catch (ApplicationException e) {
            mysqlLogger.error(e.getMessage());
        }
    }
}