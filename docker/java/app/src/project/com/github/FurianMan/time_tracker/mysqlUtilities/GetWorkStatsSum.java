package com.github.FurianMan.time_tracker.mysqlUtilities;

import com.github.FurianMan.time_tracker.Constants;
import com.github.FurianMan.time_tracker.exceptions.ApplicationException;
import com.github.FurianMan.time_tracker.mysqlTables.TableUsers;
import com.github.FurianMan.time_tracker.utilities.RequestUserStats;
import com.github.FurianMan.time_tracker.utilities.ResponseStatsTimeSum;
import com.github.FurianMan.time_tracker.utilities.TimeStats;
import com.github.FurianMan.time_tracker.utilities.Utilities;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static com.github.FurianMan.time_tracker.mysqlUtilities.ConnectToDB.connectToDatabase;
import static com.github.FurianMan.time_tracker.mysqlUtilities.DisconnectToDB.disconnectToDatabase;
import static com.github.FurianMan.time_tracker.mysqlUtilities.GetUser.getUser;

public class GetWorkStatsSum {
    private static final Logger mysqlLogger = Constants.getMysqlLogger();

    /**
     * Метода для получения статистики по пользователю
     * Пример результата: {"user_id":1,"allTimeStats":[
     * {"task_num":501,"duration":"838:59"},
     * {"task_num":1337,"duration":"00:02"},
     * {"task_num":7331,"duration":"00:02"}
     * ]}
     * Поиск статистики осуществляется по полям:
     * user_id, start_time, end_time
     * Берем их значения из
     * @param reqData - экземпляр TableTasks с значениям от пользователя
     * Из БД мы получим уже отсортированные по start_time данные, т.е. по времени начала трека.
     */
    public static ResponseStatsTimeSum getWorkStatsSum(RequestUserStats reqData) throws ApplicationException {
        int user_id = reqData.getUser_id();
        String start_time = reqData.getStart_time();
        String end_time = reqData.getEnd_time();

        Connection conn;
        ResponseStatsTimeSum respStats = new ResponseStatsTimeSum();
        respStats.setUser_id(user_id);
        // Проверка полей start_time и end_time
        Utilities.validateDateTime(reqData);

        if (user_id != 0) {
            /*
             * Создаем класс пользователя и проверяем его существавание в db
             * */
            TableUsers userDB = new TableUsers();
            userDB.setUser_id(user_id);
            getUser(userDB);

            conn = connectToDatabase();
            try {
                Statement statmt = conn.createStatement();
                String sqlQuery = (String.format("SELECT task_num, TIME_FORMAT(timediff(end_time, start_time), '%%H:%%i') AS 'Duration'" +
                        "FROM (" +
                        "  SELECT * " +
                        "  from tasks " +
                        "  where user_id=%d " +
                        "  AND (start_time >= '%2$s' AND start_time <= '%3$s')" +
                        "  AND (end_time >= '%2$s' AND end_time <= '%3$s')" +
                        "  ORDER BY start_time) AS query;", user_id, start_time, end_time));
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
            mysqlLogger.error("Request does not have required fields for getting stats, please check documentation");
            throw new ApplicationException("Request does not have required fields for getting stats, please check documentation", 415);
        }
    }
}
