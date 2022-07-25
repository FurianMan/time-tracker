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

public class GetWorkStats {
    private static final Logger mysqlLogger = Constants.getMysqlLogger();

    /**
     * Ищем task в БД, доступен 1 вариант поиска
     * 1 - по полям user_id, task_num, start_time.
     * Если не нашли, то поднимаем исключение и информируем пользователя
     *
     * @param taskForSearch - передаем объект с данными пользователя
     */
    public static TableTasks getWorkStats(TableTasks taskForSearch) throws ApplicationException {
        int user_id = taskForSearch.getUser_id();

        Connection conn;
        TableTasks taskInstance = new TableTasks();
        if (user_id != 0) {
            conn = connectToDatabase();
            try {
                Statement statmt = conn.createStatement();
                String sqlQuery = (String.format("SELECT task_num, TIME_FORMAT(timediff(end_time, start_time), '%H:%i') AS 'Duration'" +
                        " FROM tasks WHERE user_id=%d AND end_time IS NOT NULL;", user_id));
                ResultSet resSet = statmt.executeQuery(sqlQuery);
                mysqlLogger.debug(String.format(sqlQuery));
                if (!resSet.next()) {
                    mysqlLogger.error(String.format("Can't find in database stats for user_id=%d", user_id));
                    throw new ApplicationException("Can't find stats in database", 404);
                } else {
                    do {
                        taskInstance.setTask_id(resSet.getInt("task_id"));
                        taskInstance.setUser_id(resSet.getInt("user_id"));
                        taskInstance.setTask_num(resSet.getInt("task_num"));
                        taskInstance.setStart_time(resSet.getString("start_time"));
                        taskInstance.setEnd_time(resSet.getString("end_time"));
                    } while (resSet.next());
                }
                mysqlLogger.info(String.format("Stats has been found successfully for user_id=%d", user_id));
            } catch (SQLException e) {
                mysqlLogger.error("Can't execute query 'getWorkStats' to database", e);
                throw new ApplicationException("Can't execute query 'getWorkStats' to database", e, 500);
            } finally {
                disconnectToDatabase(conn);
            }
            return taskInstance;
        } else {
            mysqlLogger.error("Request does not have required fields for searching, please check documentation");
            throw new ApplicationException("Request does not have required fields for searching, please check documentation", 415);
        }
    }
}
