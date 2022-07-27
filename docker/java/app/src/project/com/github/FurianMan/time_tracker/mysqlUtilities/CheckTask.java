package com.github.FurianMan.time_tracker.mysqlUtilities;

import com.github.FurianMan.time_tracker.Constants;
import com.github.FurianMan.time_tracker.exceptions.ApplicationException;
import com.github.FurianMan.time_tracker.mysqlTables.TableTasks;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static com.github.FurianMan.time_tracker.mysqlUtilities.ConnectToDB.connectToDatabase;
import static com.github.FurianMan.time_tracker.mysqlUtilities.DisconnectToDB.disconnectToDatabase;

public class CheckTask {
    private static final Logger mysqlLogger = Constants.getMysqlLogger();
    private static Statement statmt;
    private static ResultSet resSet;
    private static String sqlQuery;

    /**
     * Проверяем в БД существует ли уже task с таким пользователем и номером задания
     * но у которого end_time IS NULL. Таким образом предотвращаем повторного трека задачи
     * Если наши задачу - поднимаем исключение. Если её нет, то молчим.
     *
     * @param taskForCheck - передаем объект с данными задания
     */
    public static void checkOpenTaskErr(TableTasks taskForCheck) throws ApplicationException {
        int user_id = taskForCheck.getUser_id();
        int task_num = taskForCheck.getTask_num();

        Connection conn;
        conn = connectToDatabase();
        try {
            statmt = conn.createStatement();
            sqlQuery = (String.format("SELECT * FROM tasks WHERE user_id=%d AND task_num=%d AND end_time IS NULL;", user_id, task_num));
            mysqlLogger.debug(String.format(sqlQuery));
            resSet = statmt.executeQuery(sqlQuery);
            if (resSet.next()) {
                mysqlLogger.error(String.format("The task already exists: task_num=%d user_id=%d. Please, firstly close the previous task", user_id, task_num));
                throw new ApplicationException(String.format("The task already exists: task_num= %d user_id= %d. Please, firstly close the previous task", user_id, task_num), 404);
            }
            mysqlLogger.info(String.format("Previous opened task has not been found: task_num= %d user_id= %d", user_id, task_num));
        } catch (SQLException e) {
            mysqlLogger.error("Can't execute query 'checkOpenTaskErr' to database", e);
            throw new ApplicationException("Can't execute query 'checkOpenTaskErr' to database", e, 500);
        } finally {
            disconnectToDatabase(conn);
        }
    }

    /**
     * Проверяем в БД существует ли уже task с таким пользователем и номером задания
     * и у которого end_time IS NULL.
     * Если такого task нет, то нечего обновлять, возвращаем исключение
     *
     * @param taskForCheck - передаем объект с данными задания
     */
    public static void checkOpenTask(TableTasks taskForCheck) throws ApplicationException {
        int task_id = taskForCheck.getTask_id();
        int user_id = taskForCheck.getUser_id();

        Connection conn;
        conn = connectToDatabase();
        try {
            statmt = conn.createStatement();
            sqlQuery = (String.format("SELECT * FROM tasks WHERE user_id=%d AND task_id=%d AND end_time IS NULL;", user_id, task_id));
            mysqlLogger.debug(String.format(sqlQuery));
            resSet = statmt.executeQuery(sqlQuery);
            if (!resSet.next()) {
                mysqlLogger.error(String.format("The task is not exists: task_id=%d user_id= %d", task_id, user_id));
                throw new ApplicationException(String.format("The task is not exists: task_id=%d user_id= %d", task_id, user_id), 404);
            }
            mysqlLogger.info(String.format("The task with task_id=%d and user_id= %d has been found successfully", task_id, user_id));
        } catch (SQLException e) {
            mysqlLogger.error("Can't execute query 'checkOpenTask' to database", e);
            throw new ApplicationException("Can't execute query 'checkOpenTask' to database", e, 500);
        } finally {
            disconnectToDatabase(conn);
        }
    }
    /**
     * Проверяем в БД существует ли уже task с таким пользователем и номером задания
     * и у которого end_time уже имеется, т.е. проверяем закрыт ли уже по ней трекинг.
     * Если трекинг закрыт - поднимаем исключение
     * @param taskForCheck - передаем объект с данными задания
     */
    public static void checkCloseTask(TableTasks taskForCheck) throws ApplicationException {
        int task_id = taskForCheck.getTask_id();
        int user_id = taskForCheck.getUser_id();

        Connection conn;
        conn = connectToDatabase();
        try {
            statmt = conn.createStatement();
            sqlQuery = (String.format("SELECT * FROM tasks WHERE user_id=%d AND task_id=%d AND end_time IS NOT NULL;", user_id, task_id));
            mysqlLogger.debug(String.format(sqlQuery));
            resSet = statmt.executeQuery(sqlQuery);
            if (resSet.next()) {
                mysqlLogger.error(String.format("The task already closed: task_id=%d user_id=%d", task_id, user_id));
                throw new ApplicationException(String.format("The task already closed: task_id=%d user_id=%d", task_id, user_id), 404);
            }
            mysqlLogger.info(String.format("The opened task with task_id=%d and user_id=%d has been found successfully", task_id, user_id));
        } catch (SQLException e) {
            mysqlLogger.error("Can't execute query 'checkCloseTask' to database", e);
            throw new ApplicationException("Can't execute query 'checkCloseTask' to database", e, 500);
        } finally {
            disconnectToDatabase(conn);
        }
    }
}
