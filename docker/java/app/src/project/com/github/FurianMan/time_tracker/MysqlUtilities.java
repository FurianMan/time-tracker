package com.github.FurianMan.time_tracker;

import com.github.FurianMan.time_tracker.Exceptions.MysqlConnectException;
import org.json.JSONArray;
import org.json.JSONObject;
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
    private String query;
    private static JSONObject jsonObject;

    private static Connection connectToDatabase() throws MysqlConnectException {
        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            mysqlLogger.error("Can't get class for database. No driver found", e);
            throw new MysqlConnectException("Can't get class for database. No driver found", e);
        }
        //Connection conn = null;
        try {
            conn = DriverManager.getConnection(connectionString, login, password);
        } catch (SQLException e) {
            mysqlLogger.error("Can't get connection to database", e);
            throw new MysqlConnectException("Can't get connection to database", e);
        }
        return conn;
    }
    private static void disconnectToDatabase(Connection conn) throws MysqlConnectException {
        try {
            conn.close();
        } catch (SQLException e) {
            mysqlLogger.error("Can't close connection to database", e);
            throw new MysqlConnectException("Can't close connection to database", e);
        }
    }
    public static void insertInto(String name, String surname, String position, String birthday) throws MysqlConnectException {
        conn = connectToDatabase();
        try {
            statmt = conn.createStatement();
            statmt.executeUpdate(String.format("INSERT INTO users (name, surname, position, birthday) VALUES ('%s', '%s', '%s', '%s');", name, surname, position, birthday));
            mysqlLogger.info(String.format("Пользователь успешно создан: name=%s, surname=%s, position=%s, birthday=%s", name, surname, position, birthday));
        }
        catch (SQLException e) {
            mysqlLogger.error("Can't insert query in database", e);
            throw new MysqlConnectException("Can't insert query in database", e);
        } finally {
            disconnectToDatabase(conn);
        }
    }
    public static String getUser(String name, String surname, String position, String birthday) throws MysqlConnectException {
        conn = connectToDatabase();
        try {
            statmt = conn.createStatement();
            resSet = statmt.executeQuery(String.format("SELECT surname, name, patronymic, position, birthday FROM users WHERE name='%s' AND surname='%s' AND position='%s' AND birthday='%s';", name, surname, position, birthday));
            jsonObject = MysqlUtilities.convertToJSON(resSet);
        } catch (SQLException e) {
            mysqlLogger.error("Can't get result of query to database", e);
            throw new MysqlConnectException("Can't get result of query to database", e);
        } finally {
            disconnectToDatabase(conn);
        }
        return jsonObject.toString();
    }
    public static JSONObject convertToJSON(ResultSet resultSet) throws MysqlConnectException {
        JSONArray jsonArray = new JSONArray();
        try {
            while (resultSet.next()) {
                int total_columns = resultSet.getMetaData().getColumnCount();
                JSONObject obj = new JSONObject();
                for (int i = 0; i < total_columns; i++) {
                    obj.put(resultSet.getMetaData().getColumnLabel(i + 1).toLowerCase(), resultSet.getObject(i + 1));
                }
                jsonArray.put(obj);
            }
        } catch (SQLException e) {
            mysqlLogger.error("Can't convertToJson", e);
            throw new MysqlConnectException("Can't convertToJson", e);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("User",jsonArray);
        return jsonObject;
    }
}
