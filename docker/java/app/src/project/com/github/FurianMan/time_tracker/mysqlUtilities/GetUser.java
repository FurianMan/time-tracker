package com.github.FurianMan.time_tracker.mysqlUtilities;

import com.github.FurianMan.time_tracker.Constants;
import com.github.FurianMan.time_tracker.exceptions.ApplicationException;
import com.github.FurianMan.time_tracker.mysqlTables.TableUsers;
import com.github.FurianMan.time_tracker.utilities.Utilities;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static com.github.FurianMan.time_tracker.mysqlUtilities.ConnectToDB.connectToDatabase;
import static com.github.FurianMan.time_tracker.mysqlUtilities.DisconnectToDB.disconnectToDatabase;

public class GetUser {
    private static final Logger mysqlLogger = Constants.getMysqlLogger();

    /**
     * Ищем user в БД, доступны 3 варианта поиска
     * 1 - по полю user_id
     * 2 - по полям name,surname,patronymic,birthday.
     * 3 - по полям name surname. Если записей больше, чем 1, то вернем последнего созданного
     * Если ничего не подошло, то поднимаем исключение и информируем пользователя
     *
     * @param userForSearch - передаем объект с данными пользователя из запроса
     *
     * Получаем все колонки, что есть в mysql.
     * Возвращаем полученные данные упакованные в класс.
     */
    public static TableUsers getUser(TableUsers userForSearch) throws ApplicationException {
        int user_id = userForSearch.getUser_id();
        String name = userForSearch.getName();
        String surname = userForSearch.getSurname();
        String position = userForSearch.getPosition();
        String birthday = userForSearch.getBirthday();
        Connection conn;
        TableUsers userInstance = new TableUsers();
        Statement statmt;
        ResultSet resSet;
        String sqlQuery;

//        Utilities.validateUserFields(userForSearch); // не ясно нужно ли валидировать пользователя при GET. если да, то надо логически вставить функцию

        if (user_id != 0) {
            conn = connectToDatabase();
            try {
                statmt = conn.createStatement();
                sqlQuery = (String.format("SELECT * FROM users WHERE user_id='%d';", user_id));
                mysqlLogger.debug(String.format(sqlQuery));
                resSet = statmt.executeQuery(sqlQuery);
                if (!resSet.next()) {
                    mysqlLogger.error(String.format("Cannot find in database user: user_id=%d", user_id));
                    throw new ApplicationException("Cannot find the user in database", 404);
                } else {
                    do { //TODO можно сделать без цикла
                        userInstance.setUser_id(resSet.getInt("user_id"));
                        userInstance.setName(resSet.getString("name"));
                        userInstance.setSurname(resSet.getString("surname"));
                        userInstance.setPatronymic(resSet.getString("patronymic"));
                        userInstance.setPosition(resSet.getString("position"));
                        userInstance.setBirthday(resSet.getString("birthday"));
                        userInstance.setDateCreating(resSet.getString("date_creating"));
                    } while (resSet.next());
                }
                mysqlLogger.info(String.format("User has been found successfully: user_id=%d", user_id));
            } catch (SQLException e) {
                mysqlLogger.error("Cannot execute query `getUser` to database", e);
                throw new ApplicationException("Cannot execute query `getUser` to database", e, 500);
            } finally {
                disconnectToDatabase(conn);
            }
            return userInstance;
        } else if (name != null && surname != null && birthday != null && position != null) {
            conn = connectToDatabase();
            try {
                statmt = conn.createStatement();
                sqlQuery = String.format("SELECT * FROM users WHERE name='%s' AND surname='%s' AND position='%s' AND birthday='%s';", name, surname, position, birthday);
                mysqlLogger.debug(sqlQuery);
                resSet = statmt.executeQuery(sqlQuery);
                if (!resSet.next()) {
                    mysqlLogger.error(String.format("Cannot find in database user: name=%s, surname=%s, position=%s, birthday=%s", name, surname, position, birthday));
                    throw new ApplicationException("Cannot find the user in database", 404);
                } else {
                    do { //TODO можно сделать без цикла
                        userInstance.setUser_id(resSet.getInt("user_id"));
                        userInstance.setName(resSet.getString("name"));
                        userInstance.setSurname(resSet.getString("surname"));
                        userInstance.setPatronymic(resSet.getString("patronymic"));
                        userInstance.setPosition(resSet.getString("position"));
                        userInstance.setBirthday(resSet.getString("birthday"));
                        userInstance.setDateCreating(resSet.getString("date_creating"));
                    } while (resSet.next());
                }
                mysqlLogger.info(String.format("User has been found successfully: name=%s, surname=%s, position=%s, birthday=%s", name, surname, position, birthday));
            } catch (SQLException e) {
                mysqlLogger.error("Cannot execute query `getUser` to database", e);
                throw new ApplicationException("Cannot execute query `getUser` to database", e, 500);
            } finally {
                disconnectToDatabase(conn);
            }
            return userInstance;
        } else if (surname != null && name != null) {
            conn = connectToDatabase();
            try {
                statmt = conn.createStatement();
                sqlQuery = String.format("SELECT * FROM users WHERE name='%s' AND surname='%s';", name, surname);
                mysqlLogger.debug(sqlQuery);
                resSet = statmt.executeQuery(sqlQuery);
                if (!resSet.next()) {
                    mysqlLogger.error(String.format("Cannot find in database user: name=%s, surname=%s", name, surname));
                    throw new ApplicationException("Cannot find the user in database", 404);
                } else {
                    do {
                        userInstance.setUser_id(resSet.getInt("user_id"));
                        userInstance.setName(resSet.getString("name"));
                        userInstance.setSurname(resSet.getString("surname"));
                        userInstance.setPatronymic(resSet.getString("patronymic"));
                        userInstance.setPosition(resSet.getString("position"));
                        userInstance.setBirthday(resSet.getString("birthday"));
                        userInstance.setDateCreating(resSet.getString("date_creating"));
                    } while (resSet.next());
                }
                mysqlLogger.info(String.format("User has been found successfully: name=%s, surname=%s", name, surname));
            } catch (SQLException e) {
                mysqlLogger.error("Cannot execute query `getUser` to database", e);
                throw new ApplicationException("Cannot execute query `getUser` to database", e, 500);
            } finally {
                disconnectToDatabase(conn);
            }
            return userInstance;
        } else {
            mysqlLogger.error("Request does not have required fields for method `getUser`");
            mysqlLogger.debug(String.format("Request does not have required fields for method `getUser`. " +
                    "user_id=%d, name=%s, surname=%s, position=%s, birthday=%s", user_id, name, surname, position, birthday));
            throw new ApplicationException("Request does not have required fields for searching, please check documentation", 415);
        }
    }
}
