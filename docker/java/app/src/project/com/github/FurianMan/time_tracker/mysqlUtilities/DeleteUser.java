package com.github.FurianMan.time_tracker.mysqlUtilities;

import com.github.FurianMan.time_tracker.exceptions.ApplicationException;
import com.github.FurianMan.time_tracker.mysqlTables.TableUsers;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static com.github.FurianMan.time_tracker.Constants.deleteUserLogger;
import static com.github.FurianMan.time_tracker.mysqlUtilities.ConnectToDB.connectToDatabase;
import static com.github.FurianMan.time_tracker.mysqlUtilities.DisconnectToDB.disconnectToDatabase;
import static com.github.FurianMan.time_tracker.mysqlUtilities.GetUser.getUser;

public class DeleteUser {
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
    public static void deleteUser(TableUsers delUser) {
        int user_id = delUser.getUser_id();
        String name = delUser.getName();
        String surname = delUser.getSurname();
        String birthday = delUser.getBirthday();
        String position = delUser.getPosition();

        // Делаем проверку переменных, чтобы убедиться, что хотя бы один из способов удаления у нас доступен
        if (user_id == 0 &&
                (name == null || surname == null || birthday == null || position == null)) {
            deleteUserLogger.error("Cannot delete user from database. Request does not have required fields for deleting.");
            throw new ApplicationException("Request does not have required fields for deleting, please check documentation", 415);
        }
        if (delUser.getUser_id() != 0) {
             /*
              Создаем класс пользователя и проверяем его существавание в db
              * */
            TableUsers userDB = new TableUsers();
            userDB.setUser_id(user_id);
            getUser(userDB);
            conn = connectToDatabase();
            try {
                statmt = conn.createStatement();
                sqlQuery = String.format("DELETE FROM users WHERE user_id=%d;", user_id);
                deleteUserLogger.debug(String.format(sqlQuery));
                statmt.executeUpdate(sqlQuery);
                deleteUserLogger.info(String.format("User has been deleted successfully: user_id=%d", user_id));

            } catch (SQLException e) {
                deleteUserLogger.error("Cannot execute query `deleteUser` in database", e);
                throw new ApplicationException("Cannot execute query `deleteUser` in database", e, 500);
            } finally {
                disconnectToDatabase(conn);
            }
        } else {
             /*
              Создаем класс пользователя и проверяем его существавание в db
              * */
            TableUsers userDB = new TableUsers();
            userDB.setName(name);
            userDB.setSurname(surname);
            userDB.setPosition(position);
            userDB.setBirthday(birthday);
            getUser(userDB);
            conn = connectToDatabase();
            try {
                statmt = conn.createStatement();
                sqlQuery = String.format("DELETE FROM users WHERE name='%s' AND surname='%s' AND position='%s' AND birthday='%s';", name, surname, position, birthday);
                deleteUserLogger.debug(String.format(sqlQuery));
                statmt.executeUpdate(sqlQuery);
                deleteUserLogger.info(String.format("User has been deleted successfully: name=%s, surname=%s, position=%s birthday=%s", name, surname, position, birthday));

            } catch (SQLException e) {
                deleteUserLogger.error("Cannot execute query `deleteUser` in database", e);
                throw new ApplicationException("Cannot execute query `deleteUser` in database", e, 500);
            } finally {
                disconnectToDatabase(conn);
            }
        }
    }
}
