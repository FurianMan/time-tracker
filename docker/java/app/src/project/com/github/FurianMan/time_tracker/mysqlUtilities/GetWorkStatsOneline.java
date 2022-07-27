package com.github.FurianMan.time_tracker.mysqlUtilities;

import com.github.FurianMan.time_tracker.Constants;
import com.github.FurianMan.time_tracker.exceptions.ApplicationException;
import com.github.FurianMan.time_tracker.utilities.*;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static com.github.FurianMan.time_tracker.mysqlUtilities.ConnectToDB.connectToDatabase;
import static com.github.FurianMan.time_tracker.mysqlUtilities.DisconnectToDB.disconnectToDatabase;

public class GetWorkStatsOneline {
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
     *
     * @param reqData - экземпляр RequestUserStats со значениям от пользователя
     *                Из БД мы получим уже отсортированные по start_time данные, т.е. по времени начала трека.
     */
    public static ResponseStatsTimeOneline getWorkStatsOneline(RequestUserStats reqData) throws ApplicationException {
        int user_id = reqData.getUser_id();
        String start_time = reqData.getStart_time();
        String end_time = reqData.getEnd_time();

        Connection conn;

        //Создаем класс, который будем возвращать пользователю как ответ при успехе
        ResponseStatsTimeOneline respStats = new ResponseStatsTimeOneline();
        respStats.setUser_id(user_id);

        // Проверка полей start_time и end_time
        Utilities.validateDateTime(reqData);

        //TODO придумать запрос sql
        conn = connectToDatabase();
        try {
            Statement statmt = conn.createStatement();
            String sqlQuery = (String.format("SELECT user_id, SUM(TIMESTAMPDIFF(MINUTE, start_time, end_time)) AS 'Duration' " +
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
                    respStats.setTimeStatsAll(resSet.getInt("Duration"));
                } while (resSet.next());
            }
            mysqlLogger.info(String.format("Stats has been found successfully for user_id=%d", user_id));
        } catch (SQLException e) {
            mysqlLogger.error("Can't execute query 'getWorkStatsOneline' to database", e);
            throw new ApplicationException("Can't execute query 'getWorkStatsOneline' to database", e, 500);
        } finally {
            disconnectToDatabase(conn);
        }
        return respStats;
    }
}
