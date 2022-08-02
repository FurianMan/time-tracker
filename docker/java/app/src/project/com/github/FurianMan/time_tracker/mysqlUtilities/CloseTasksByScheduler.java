package com.github.FurianMan.time_tracker.mysqlUtilities;

import com.github.FurianMan.time_tracker.exceptions.ApplicationException;
import org.quartz.JobExecutionException;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

import static com.github.FurianMan.time_tracker.Constants.updateTaskLogger;
import static com.github.FurianMan.time_tracker.mysqlUtilities.ConnectToDB.connectToDatabase;
import static com.github.FurianMan.time_tracker.mysqlUtilities.DisconnectToDB.disconnectToDatabase;

public class CloseTasksByScheduler {

    /**
     * Метод нужен для закрытие задач, которые пользователь не закрыл самостоятельно.
     * Методы вызывается из класса ScheduledTasks, там по cron устанавливается расписание.
     * В текущей реализации каждый день в 23:59 задачи закрываются текущей датой и временем 23:59:59
     * */
    public static void closeTaskByScheduler () throws JobExecutionException {

        LocalDate localDate = LocalDate.now();
        String end_time = localDate + " 23:59:59";

        Connection conn = connectToDatabase();
        try {
            Statement statmt = conn.createStatement();
            String sqlQuery = String.format("UPDATE tasks SET end_time=Cast('%s' as datetime) WHERE end_time IS NULL;", end_time);
            updateTaskLogger.debug(sqlQuery);
            statmt.executeUpdate(sqlQuery);
            updateTaskLogger.info("Tasks have been closed successfully");
        } catch (SQLException e) {
            updateTaskLogger.error("Cannot execute query closeTaskByScheduler to database", e);
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
