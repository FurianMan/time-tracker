package com.github.FurianMan.time_tracker;

import com.github.FurianMan.time_tracker.Exceptions.ApplicationException;
import org.slf4j.Logger;

import java.sql.*;


public class MysqlUtilities {
    private static final String driverName = Constants.getDriverName();
    private static final String connectionString = Constants.getConnectionString();
    private static final String login = Constants.getDbLogin();
    private static final String password = Constants.getDbPassword();
    private static final Logger mysqlLogger = Constants.getMysqlLogger();
    private static Connection conn;
    private static Statement statmt;
    private static ResultSet resSet;
//    private String query;

    private static Connection connectToDatabase() throws ApplicationException {
        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            mysqlLogger.error("Can't get class for database. No driver found", e);
            throw new ApplicationException("Can't get class for database. No driver found", e, 500);
        }
        try {
            conn = DriverManager.getConnection(connectionString, login, password);
        } catch (SQLException e) {
            mysqlLogger.error("Can't get connection to database", e);
            throw new ApplicationException("Can't get connection to database", e, 500);
        }
        return conn;
    }

    private static void disconnectToDatabase(Connection conn) throws ApplicationException {
        try {
            conn.close();
        } catch (SQLException e) {
            mysqlLogger.error("Can't close connection to database", e);
            throw new ApplicationException("Can't close connection to database", e, 500);
        }
    }

    public static TableUsers insertUser(TableUsers newUser) throws ApplicationException {
        String name = newUser.getName();
        String surname = newUser.getSurname();
        String patronymic = newUser.getPatronymic();
        String position = newUser.getPosition();
        String birthday = newUser.getBirthday();
        conn = connectToDatabase();
        try {
            statmt = conn.createStatement();
            statmt.executeUpdate(String.format("INSERT INTO users (name, surname, patronymic, position, birthday) VALUES ('%s', '%s', '%s', '%s', '%s');", name, surname, patronymic, position, birthday));
            mysqlLogger.info(String.format("Пользователь успешно создан: name=%s, surname=%s, position=%s, birthday=%s", name, surname, position, birthday));
        } catch (SQLException e) {
            mysqlLogger.error("Can't insert query in database", e);
            throw new ApplicationException("Can't insert query in database", e, 500);
        } finally {
            disconnectToDatabase(conn);
        }
        TableUsers newUserFromDB = getUser(newUser);
        return newUserFromDB; //TODO научить возвращать не весь класс, а только user_id
    }

    public static TableUsers getUser(TableUsers userForSearching) throws ApplicationException {
        String name = userForSearching.getName();
        String surname = userForSearching.getSurname();
        String position = userForSearching.getPosition();
        String birthday = userForSearching.getBirthday();
        conn = connectToDatabase();
        TableUsers userInstance = new TableUsers();
        try {
            statmt = conn.createStatement();
            resSet = statmt.executeQuery(String.format("SELECT user_id, surname, name, patronymic, position, birthday FROM users WHERE name='%s' AND surname='%s' AND position='%s' AND birthday='%s';", name, surname, position, birthday));
            if (!resSet.next()) {
                mysqlLogger.error(String.format("Can't find in database user: name=%s, surname=%s, position=%s, birthday=%s", name, surname, position, birthday));
                throw new ApplicationException("Can't find user in database", 404);
            } else {
                do {
                    userInstance.setUser_id(resSet.getInt("user_id"));
                    userInstance.setName(resSet.getString("name"));
                    userInstance.setSurname(resSet.getString("surname"));
                    userInstance.setPatronymic(resSet.getString("patronymic"));
                    userInstance.setPosition(resSet.getString("position"));
                    userInstance.setBirthday(resSet.getString("birthday"));
                } while (resSet.next());
            }
            mysqlLogger.info(String.format("User has been found successfully: name=%s, surname=%s, position=%s, birthday=%s", name, surname, position, birthday));
        } catch (SQLException e) {
            mysqlLogger.error("Can't get result of query to database", e);
            throw new ApplicationException("Can't get result of query to database", e, 500);
        } finally {
            disconnectToDatabase(conn);
        }
        return userInstance;
    }

    public static void updateUser (TableUsers userForUpdate) throws ApplicationException {
        /**
         * Перед изменением информации у пользователя, пытаемся его найти через метод getUser
         * @param userForUpdate: экземпляр класса, из которого мы получаем всю необходимую информацию
         * Если какое-то поля не передал пользователь для изменения, то берется значение из БД
        * */
        TableUsers userDB = getUser(userForUpdate);
        int user_id = userDB.getUser_id();
        String name;
        String surname;
        String patronymic;
        String position;
        String birthday;
        name = userForUpdate.getNewName() == null  ? userDB.getName() : userForUpdate.getNewName();
        surname = userForUpdate.getNewSurname() == null ? userDB.getSurname() : userForUpdate.getNewSurname();
        patronymic = userForUpdate.getNewPatronymic() == null ? userDB.getPatronymic() : userForUpdate.getNewPatronymic();
        position = userForUpdate.getNewPosition() == null ? userDB.getPosition() : userForUpdate.getNewPosition();
        birthday = userForUpdate.getNewBirthday() == null ? userDB.getBirthday() : userForUpdate.getNewBirthday();
        conn = connectToDatabase();
        try {
            statmt = conn.createStatement();
            String query = String.format("UPDATE users SET " +
                    "name='%s', surname='%s', patronymic='%s', position='%s', birthday='%s' WHERE user_id=%d;", name, surname, patronymic, position, birthday, user_id);
            mysqlLogger.debug(query);
            statmt.executeUpdate(query);
            mysqlLogger.info(String.format("Пользователь успешно обновлен, новые значения: name=%s, surname=%s, position=%s, birthday=%s", name, surname, position, birthday));
        } catch (SQLException e) {
            mysqlLogger.error("Can't update database user", e);
            throw new ApplicationException("Can't update database user", e, 500);
        } finally {
            disconnectToDatabase(conn);
        }
    }
}
