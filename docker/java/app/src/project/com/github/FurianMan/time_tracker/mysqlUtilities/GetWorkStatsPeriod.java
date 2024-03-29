package com.github.FurianMan.time_tracker.mysqlUtilities;

import com.github.FurianMan.time_tracker.exceptions.ApplicationException;
import com.github.FurianMan.time_tracker.utilities.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static com.github.FurianMan.time_tracker.Constants.getWorkStatsPeriodLogger;
import static com.github.FurianMan.time_tracker.mysqlUtilities.ConnectToDB.connectToDatabase;
import static com.github.FurianMan.time_tracker.mysqlUtilities.DisconnectToDB.disconnectToDatabase;

public class GetWorkStatsPeriod {
    /**
     * Метода для получения периодов работы пользователя
     * Пример результата: {"user_id":1,"timeStatsPeriods":
     * [{"task_num":501,"start_time":"2022-05-26 19:51:00","end_time":"2022-07-27 11:21:25"},
     * {"task_num":888,"start_time":"2022-07-27 09:47:36","end_time":"2022-07-27 09:49:28"},
     * {"task_num":999,"start_time":"2022-07-27 09:47:47","end_time":"2022-07-27 11:21:18"}]}
     *
     * Всю работу с данными выполняет mysql, мы их лишь сохраняем в класс
     * Поиск статистики осуществляется по полям:
     * user_id, start_time, end_time
     *
     * Берем их значения из
     * @param reqData - экземпляр RequestUserStats со значениям от пользователя
     *                Из БД мы получим уже отсортированные по start_time данные, т.е. по времени начала трека.
     */
    public static ResponseStatsTimePeriod getWorkStatsPeriod(RequestUserStats reqData) {
        int user_id = reqData.getUser_id();
        String start_time = reqData.getStart_time();
        String end_time = reqData.getEnd_time();

        Connection conn;

        //Создаем класс, который будем возвращать пользователю как ответ при успехе
        ResponseStatsTimePeriod respStats = new ResponseStatsTimePeriod();
        respStats.setUser_id(user_id);

        // Проверка полей start_time и end_time
        Utilities.validateDateTime(reqData);

        conn = connectToDatabase();
        try {
            Statement statmt = conn.createStatement();
            String sqlQuery = (String.format("SELECT task_num, start_time, end_time " +
                    "FROM (" +
                    "  SELECT * " +
                    "  from tasks " +
                    "  where user_id=%d " +
                    "  AND (start_time >= '%2$s' AND start_time <= '%3$s')" +
                    "  AND (end_time >= '%2$s' AND end_time <= '%3$s')" +
                    "  ORDER BY start_time) AS query;", user_id, start_time, end_time));
            getWorkStatsPeriodLogger.debug(sqlQuery);
            ResultSet resSet = statmt.executeQuery(sqlQuery);
            if (!resSet.next()) {
                getWorkStatsPeriodLogger.error(String.format("Cannot find in database period stats for user_id = %d", user_id));
                throw new ApplicationException(String.format("Cannot find in database period stats for user_id = %d", user_id), 404);
            } else {
                do {
                    TimeStatsPeriod timeStatsPeriodPeerTask = new TimeStatsPeriod();
                    timeStatsPeriodPeerTask.setTask_num(resSet.getInt("task_num"));
                    timeStatsPeriodPeerTask.setStart_time(resSet.getString("start_time"));
                    timeStatsPeriodPeerTask.setEnd_time(resSet.getString("end_time"));
                    respStats.addStats(timeStatsPeriodPeerTask);
                } while (resSet.next());
            }
            getWorkStatsPeriodLogger.info(String.format("Period stats has been found successfully for user_id = %d", user_id));
        } catch (SQLException e) {
            getWorkStatsPeriodLogger.error("Cannot execute query `getWorkStatsPeriod` to database", e);
            throw new ApplicationException("Cannot execute query `getWorkStatsPeriod` to database", e, 500);
        } finally {
            disconnectToDatabase(conn);
        }
        return respStats;
    }
}
