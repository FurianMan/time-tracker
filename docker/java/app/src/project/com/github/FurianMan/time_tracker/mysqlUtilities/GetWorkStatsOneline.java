package com.github.FurianMan.time_tracker.mysqlUtilities;

import com.github.FurianMan.time_tracker.exceptions.ApplicationException;
import com.github.FurianMan.time_tracker.utilities.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static com.github.FurianMan.time_tracker.Constants.getWorkStatsOnelineLogger;
import static com.github.FurianMan.time_tracker.mysqlUtilities.ConnectToDB.connectToDatabase;
import static com.github.FurianMan.time_tracker.mysqlUtilities.DisconnectToDB.disconnectToDatabase;

public class GetWorkStatsOneline {
    /**
     * Метода для получения статистики по пользователю
     * Пример результата: {"user_id":1,"timeStatsOneline":"04:04"}
     * Поиск статистики осуществляется по полям:
     * user_id, start_time, end_time
     *
     * Берем их значения из
     * @param reqData - экземпляр RequestUserStats со значениям от пользователя.
     *
     * Из mysql мы получаем суммарное время затраченное на задачи в минутах.
     * В возвращаемом классе мы отдаем минуты через метод, где идет преобразование в нужную форму.
     */
    public static ResponseStatsTimeOneline getWorkStatsOneline(RequestUserStats reqData) {
        int user_id = reqData.getUser_id();
        String start_time = reqData.getStart_time();
        String end_time = reqData.getEnd_time();

        Connection conn;

        //Создаем класс, который будем возвращать пользователю как ответ при успехе
        ResponseStatsTimeOneline respStats = new ResponseStatsTimeOneline();
        respStats.setUser_id(user_id);

        // Проверка полей start_time и end_time
        Utilities.validateDateTime(reqData);

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
            getWorkStatsOnelineLogger.debug(sqlQuery);
            ResultSet resSet = statmt.executeQuery(sqlQuery);
            if (!resSet.next()) {
                getWorkStatsOnelineLogger.error(String.format("Cannot find in database oneline stats for user_id=%d", user_id));
                throw new ApplicationException(String.format("Cannot find in database oneline stats for user_id=%d", user_id), 404);
            } else {
                do {
                    respStats.setTimeStatsOneline(resSet.getInt("Duration"));
                } while (resSet.next());
            }
            getWorkStatsOnelineLogger.info(String.format("Stats has been found successfully for user_id=%d", user_id));
        } catch (SQLException e) {
            getWorkStatsOnelineLogger.error("Cannot execute query `getWorkStatsOneline` to database", e);
            throw new ApplicationException("Cannot execute query `getWorkStatsOneline` to database", e, 500);
        } finally {
            disconnectToDatabase(conn);
        }
        return respStats;
    }
}
