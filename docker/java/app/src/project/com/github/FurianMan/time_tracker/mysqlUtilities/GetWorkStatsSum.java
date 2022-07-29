package com.github.FurianMan.time_tracker.mysqlUtilities;

import com.github.FurianMan.time_tracker.exceptions.ApplicationException;
import com.github.FurianMan.time_tracker.utilities.RequestUserStats;
import com.github.FurianMan.time_tracker.utilities.ResponseStatsTimeSum;
import com.github.FurianMan.time_tracker.utilities.TimeStatsSum;
import com.github.FurianMan.time_tracker.utilities.Utilities;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static com.github.FurianMan.time_tracker.Constants.getWorkStatsSumLogger;
import static com.github.FurianMan.time_tracker.mysqlUtilities.ConnectToDB.connectToDatabase;
import static com.github.FurianMan.time_tracker.mysqlUtilities.DisconnectToDB.disconnectToDatabase;

public class GetWorkStatsSum {
    /**
     * Метода для получения статистики по пользователю,
     * где отражены задачи и время затрат для каждой
     * Пример результата: {"user_id":1,"allTimeStats":[
     * {"task_num":501,"duration":"838:59"},
     * {"task_num":1337,"duration":"00:02"},
     * {"task_num":7331,"duration":"00:02"}
     * ]}
     * Всю работу с данными выполняет mysql, мы их лишь сохраняем в класс
     *
     * Поиск статистики осуществляется по полям:
     * user_id, start_time, end_time
     * Берем их значения из
     *
     * @param reqData - экземпляр RequestUserStats со значениям от пользователя
     *                Из БД мы получим уже отсортированные по start_time данные, т.е. по времени начала трека.
     */
    public static ResponseStatsTimeSum getWorkStatsSum(RequestUserStats reqData) {
        int user_id = reqData.getUser_id();
        String start_time = reqData.getStart_time();
        String end_time = reqData.getEnd_time();

        Connection conn;

        //Создаем класс, который будем возвращать пользователю как ответ при успехе
        ResponseStatsTimeSum respStats = new ResponseStatsTimeSum();
        respStats.setUser_id(user_id);

        // Проверка полей start_time и end_time
        Utilities.validateDateTime(reqData);

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
            getWorkStatsSumLogger.debug(sqlQuery);
            ResultSet resSet = statmt.executeQuery(sqlQuery);
            if (!resSet.next()) {
                getWorkStatsSumLogger.error(String.format("Cannot find in database sum stats for user_id=%d", user_id));
                throw new ApplicationException(String.format("Cannot find in database sum stats for user_id=%d", user_id), 404);
            } else {
                do {
                    TimeStatsSum timeStatsSumPeerTask = new TimeStatsSum();
                    timeStatsSumPeerTask.setTask_num(resSet.getInt("task_num"));
                    timeStatsSumPeerTask.setDuration(resSet.getString("Duration"));
                    respStats.addStats(timeStatsSumPeerTask);
                } while (resSet.next());
            }
            getWorkStatsSumLogger.info(String.format("Stats has been found successfully for user_id=%d", user_id));
        } catch (SQLException e) {
            getWorkStatsSumLogger.error("Cannot execute query `getWorkStatsSum` to database", e);
            throw new ApplicationException("Cannot execute query `getWorkStatsSum` to database", e, 500);
        } finally {
            disconnectToDatabase(conn);
        }
        return respStats;
    }
}
