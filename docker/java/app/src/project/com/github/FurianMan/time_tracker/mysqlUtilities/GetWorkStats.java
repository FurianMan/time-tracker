package com.github.FurianMan.time_tracker.mysqlUtilities;

import com.github.FurianMan.time_tracker.Constants;
import com.github.FurianMan.time_tracker.exceptions.ApplicationException;
import com.github.FurianMan.time_tracker.mysqlTables.TableTasks;
import com.github.FurianMan.time_tracker.utilities.ResponseStatsTime;
import com.github.FurianMan.time_tracker.utilities.ResponseUserId;
import com.github.FurianMan.time_tracker.utilities.TimeStats;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;

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
    public static ResponseStatsTime getWorkStats(TableTasks taskForSearch) throws ApplicationException {
        int user_id = taskForSearch.getUser_id();

        Connection conn;
        ResponseStatsTime respStats = new ResponseStatsTime();
        respStats.setUser_id(user_id);
        try {
            if (user_id != 0) {
                conn = connectToDatabase();
                try {
                    Statement statmt = conn.createStatement();
                    String sqlQuery = (String.format("SELECT task_num, TIME_FORMAT(timediff(end_time, start_time), '%%H:%%i')" +
                            " AS 'Duration' FROM tasks WHERE user_id=%d AND end_time IS NOT NULL;", user_id));
                    mysqlLogger.debug(sqlQuery);
                    ResultSet resSet = statmt.executeQuery(sqlQuery);
                    if (!resSet.next()) {
                        mysqlLogger.error(String.format("Can't find in database stats for user_id=%d", user_id));
                        throw new ApplicationException("Can't find stats in database", 404);
                    } else {
                        do {
                            TimeStats timeStatsPeerTask = new TimeStats();
                            timeStatsPeerTask.setTask_num(resSet.getInt("task_num"));
                            timeStatsPeerTask.setDuration(resSet.getString("Duration"));
                            respStats.addStats(timeStatsPeerTask);
                        } while (resSet.next());
                    }
                    mysqlLogger.info(String.format("Stats has been found successfully for user_id=%d", user_id));
                } catch (SQLException e) {
                    mysqlLogger.error("Can't execute query 'getWorkStats' to database", e);
                    throw new ApplicationException("Can't execute query 'getWorkStats' to database", e, 500);
                } finally {
                    disconnectToDatabase(conn);
                }
                return respStats;
            } else {
                mysqlLogger.error("Request does not have required fields for searching, please check documentation");
                throw new ApplicationException("Request does not have required fields for searching, please check documentation", 415);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return respStats;
    }
}
