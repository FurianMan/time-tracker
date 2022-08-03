package com.github.FurianMan.time_tracker.mysqlUtilities;

import org.quartz.JobExecutionException;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static com.github.FurianMan.time_tracker.Constants.clearStatsLogger;
import static com.github.FurianMan.time_tracker.mysqlUtilities.ConnectToDB.connectToDatabase;
import static com.github.FurianMan.time_tracker.mysqlUtilities.DisconnectToDB.disconnectToDatabase;
import static com.github.FurianMan.time_tracker.Constants.clearDataTime;

public class ClearDatabaseByScheduler {
    private static Connection conn;
    private static Statement statmt;
    private static String sqlQuery;

    /**
     * Метод предназначен для отчистки устаревшей информации в БД
     * clearDataTime - получаем из конфиги docker-compose.
     * Удаляем только пользователей, т.к. задачи с ними связаны.
     * */
    public static void clearDatabase() throws JobExecutionException {


        conn = connectToDatabase();
        try {
            statmt = conn.createStatement();
            sqlQuery = String.format("DELETE FROM users WHERE date_creating >= '%s';", clearDataTime);
            clearStatsLogger.debug(String.format(sqlQuery));
            statmt.executeUpdate(sqlQuery);
            clearStatsLogger.info(String.format("Database has been clear successfully, clearDataTime=%s", clearDataTime));

        } catch (SQLException e) {
            clearStatsLogger.error("Cannot execute query `clearDatabase` in database", e);
            JobExecutionException e2 =
                    new JobExecutionException(e);
            // После неуспешной попытки, мы не будем перезапускать job. Ждем следующего события
            e2.setRefireImmediately(false);
            throw e2;
        } finally {
            disconnectToDatabase(conn);
        }
    }
}
