package com.github.FurianMan.time_tracker.mysqlUtilities;

import com.github.FurianMan.time_tracker.Constants;
import com.github.FurianMan.time_tracker.exceptions.ApplicationException;
import com.github.FurianMan.time_tracker.mysqlTables.TableTasks;
import com.github.FurianMan.time_tracker.mysqlTables.TableUsers;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static com.github.FurianMan.time_tracker.Constants.clearStatsLogger;
import static com.github.FurianMan.time_tracker.mysqlUtilities.ConnectToDB.connectToDatabase;
import static com.github.FurianMan.time_tracker.mysqlUtilities.DisconnectToDB.disconnectToDatabase;
import static com.github.FurianMan.time_tracker.mysqlUtilities.GetUser.getUser;

public class ClearStats {
    private static Connection conn;
    private static Statement statmt;
    private static String sqlQuery;

    /**
     * Метод предназначен для отчистки статистики по пользователю
     * Отчистить можно только по user_id
     * Если отчистить не получилось - поднимаем исключение
     * @param reqData - данные из запроса от пользователя упакованные в класс
     * */
    public static void clearUserStats(TableTasks reqData) {
        int user_id = reqData.getUser_id();

        if (user_id == 0) {
            clearStatsLogger.error("In request of clearing stats user_id must not be equal 0");
            throw new ApplicationException("Cannot clear stats from database. user_id must not be equal 0", 415);
        }

        /*
         * Создаем класс пользователя и проверяем его существавание в db
         * */
        TableUsers userDB = new TableUsers();
        userDB.setUser_id(user_id);
        getUser(userDB);

        conn = connectToDatabase();
        try {
            statmt = conn.createStatement();
            sqlQuery = String.format("DELETE FROM tasks WHERE user_id=%d;", user_id);
            clearStatsLogger.debug(String.format(sqlQuery));
            statmt.executeUpdate(sqlQuery);
            clearStatsLogger.info(String.format("User`s stats has been deleted successfully: user_id=%d", user_id));

        } catch (SQLException e) {
            clearStatsLogger.error("Cannot execute query `clearStats` in database", e);
            throw new ApplicationException("Cannot execute query `clearStats` in database", e, 500);
        } finally {
            disconnectToDatabase(conn);
        }
    }
}