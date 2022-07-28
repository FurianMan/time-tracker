package com.github.FurianMan.time_tracker.mysqlUtilities;

import com.github.FurianMan.time_tracker.Constants;
import com.github.FurianMan.time_tracker.exceptions.ApplicationException;
import com.github.FurianMan.time_tracker.mysqlTables.TableUsers;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static com.github.FurianMan.time_tracker.mysqlUtilities.ConnectToDB.connectToDatabase;
import static com.github.FurianMan.time_tracker.mysqlUtilities.DisconnectToDB.disconnectToDatabase;

public class DeleteUser {
    private static final Logger mysqlLogger = Constants.getMysqlLogger();
    private static Connection conn;
    private static Statement statmt;
    private static String sqlQuery;

    /**
     * Метод предназначен для удаления пользователя
     * Удалить можно следующими способами
     * 1 - по user_id
     * 2 - по name,surname,birthday,position. Эта связка является уникальной на бд
     * Если удалить не получилось - поднимаем исключение
     * @param delUser - данные из запроса от пользователя упакованные в класс
     * */
    public static void deleteUser(TableUsers delUser) throws ApplicationException {
        int user_id = delUser.getUser_id();
        String name = delUser.getName();
        String surname = delUser.getSurname();
        String birthday = delUser.getBirthday();
        String position = delUser.getPosition();

        // Делаем проверку переменных, чтобы убедиться, что хотя бы один из способов удаления у нас доступен
        if (user_id == 0 &&
                (name == null || surname == null || birthday == null || position == null)) {
            mysqlLogger.error("Request does not have required fields for deleting, please check documentation");
            throw new ApplicationException("Cannot delete user from database", 415);
        }
        if (delUser.getUser_id() != 0) {
            conn = connectToDatabase();
            try {
                statmt = conn.createStatement();
                sqlQuery = String.format("DELETE FROM users WHERE user_id=%d;", user_id);
                mysqlLogger.debug(String.format(sqlQuery));
                statmt.executeUpdate(sqlQuery);
                mysqlLogger.info(String.format("User has been deleted successfully: user_id=%d", user_id));

            } catch (SQLException e) {
                mysqlLogger.error("Cannot execute query `deleteUser` in database", e);
                throw new ApplicationException("Cannot execute query `deleteUser` in database", e, 500);
            } finally {
                disconnectToDatabase(conn);
            }
        } else {
            conn = connectToDatabase();
            try {
                statmt = conn.createStatement();
                sqlQuery = String.format("DELETE FROM users WHERE name='%s' AND surname='%s' AND position='%s' AND birthday='%s';", name, surname, position, birthday);
                mysqlLogger.debug(String.format(sqlQuery));
                statmt.executeUpdate(sqlQuery);
                mysqlLogger.info(String.format("User has been deleted successfully: name=%s, surname=%s, position=%s birthday=%s", name, surname, position, birthday));

            } catch (SQLException e) {
                mysqlLogger.error("Cannot execute query `deleteUser` in database", e);
                throw new ApplicationException("Cannot execute query `deleteUser` in database", e, 500);
            } finally {
                disconnectToDatabase(conn);
            }
        }
    }
}
