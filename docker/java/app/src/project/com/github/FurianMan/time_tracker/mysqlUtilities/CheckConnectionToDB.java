package com.github.FurianMan.time_tracker.mysqlUtilities;

import com.github.FurianMan.time_tracker.exceptions.ApplicationException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.concurrent.TimeUnit;

import static com.github.FurianMan.time_tracker.Constants.*;

public class CheckConnectionToDB {
    static Connection conn;

    /**
     * Метод используется для проверки подключения к БД
     * При старте контейнера time-tracker
     * Используется бесконечный цикл
     * Условием выхода является подключение к БД
    * */
    public static void checkConnectionToDB () { //TODO в целом работает, но можно еще проверять, если вдруг база упадет и переподключаться к ней
        checkConnectionToDBLogger.info("Connecting to database...");
        while (true) {
            try {
                Class.forName(driverName);
                conn = DriverManager.getConnection(connectionString, loginDB, passwordDB);
                break;
            } catch (Exception e) {
                checkConnectionToDBLogger.error("Connection failed, new try starting...");
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException ex) {
                    checkConnectionToDBLogger.error(e.getMessage());
                }
            }
        }
        checkConnectionToDBLogger.info("Database connection has been installed successfully");
        try {
            DisconnectToDB.disconnectToDatabase(conn);
        } catch (ApplicationException e) {
            checkConnectionToDBLogger.error(e.getMessage());
        }
    }
}