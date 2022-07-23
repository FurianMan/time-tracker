package com.github.FurianMan.time_tracker.MysqlUtilities;

import com.github.FurianMan.time_tracker.Constants;
import com.github.FurianMan.time_tracker.Exceptions.ApplicationException;
import com.github.FurianMan.time_tracker.MysqlTables;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static com.github.FurianMan.time_tracker.MysqlUtilities.ConnectToDB.connectToDatabase;
import static com.github.FurianMan.time_tracker.MysqlUtilities.DisconnectToDB.disconnectToDatabase;

public class GetUser {
    private static final Logger mysqlLogger = Constants.getMysqlLogger();
    private static Statement statmt;
    private static ResultSet resSet;
    private static String sqlQuery;
    /**
     * Ищем user в БД, доступны 3 варианта поиска
     * 1 - по полю user_id
     * 2 - по полям name,surname,position,birthday.
     * 3 - по полям name surname. Если записей больше, чем 1, то вернем последнего созданного
     * Если ничего не подошло, то поднимаем исключение и информируемпользователя
     * @param userForSearching - передаем объект с данными пользователя
    * */
    public static MysqlTables.TableUsers getUser(MysqlTables.TableUsers userForSearching) throws ApplicationException {
        int user_id = userForSearching.getUser_id();
        String name = userForSearching.getName();
        String surname = userForSearching.getSurname();
        String position = userForSearching.getPosition();
        String birthday = userForSearching.getBirthday();
        Connection conn;
        MysqlTables.TableUsers userInstance = new MysqlTables.TableUsers();
        if (user_id != 0) {
            conn = connectToDatabase();
            try {
                statmt = conn.createStatement();
                sqlQuery = (String.format("SELECT * FROM users WHERE user_id='%d';", user_id));
                resSet = statmt.executeQuery(sqlQuery);
                mysqlLogger.debug(String.format(sqlQuery));
                if (!resSet.next()) {
                    mysqlLogger.error(String.format("Can't find in database user: user_id=%d", user_id));
                    throw new ApplicationException("Can't find user in database", 404);
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
                mysqlLogger.info(String.format("User has been found successfully: user_id=%d", user_id));
            } catch (SQLException e) {
                mysqlLogger.error("Can't get result of query to database", e);
                throw new ApplicationException("Can't get result of query to database", e, 500);
            } finally {
                disconnectToDatabase(conn);
            }
            return userInstance;
        } else if (name != null && surname != null && birthday != null) {
            conn = connectToDatabase();
            try {
                statmt = conn.createStatement();
                sqlQuery = String.format("SELECT * FROM users WHERE name='%s' AND surname='%s' AND birthday='%s';", name, surname, birthday);
                mysqlLogger.debug(sqlQuery);
                resSet = statmt.executeQuery(sqlQuery);
                if (!resSet.next()) {
                    mysqlLogger.error(String.format("Can't find in database user: name=%s, surname=%s, birthday=%s", name, surname, birthday));
                    throw new ApplicationException("Can't find user in database", 404);
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
                mysqlLogger.info(String.format("User has been found successfully: name=%s, surname=%s, position=%s, birthday=%s", name, surname, position, birthday));
            } catch (SQLException e) {
                mysqlLogger.error("Can't get result of query to database", e);
                throw new ApplicationException("Can't get result of query to database", e, 500);
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
                    mysqlLogger.error(String.format("Can't find in database user: name=%s, surname=%s", name, surname));
                    throw new ApplicationException("Can't find user in database", 404);
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
                mysqlLogger.error("Can't get result of query to database", e);
                throw new ApplicationException("Can't get result of query to database", e, 500);
            } finally {
                disconnectToDatabase(conn);
            }
            return userInstance;
        } else {
            mysqlLogger.error("Request does not have required fields for searching, please check documentation");
            throw new ApplicationException("Request does not have required fields for searching, please check documentation",415);
        }
    }
}
