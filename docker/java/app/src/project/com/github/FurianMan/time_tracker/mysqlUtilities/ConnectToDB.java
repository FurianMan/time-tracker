package com.github.FurianMan.time_tracker.mysqlUtilities;

import com.github.FurianMan.time_tracker.Constants;
import com.github.FurianMan.time_tracker.exceptions.ApplicationException;
import org.slf4j.Logger;

import java.sql.*;

import static com.github.FurianMan.time_tracker.Constants.*;

public class ConnectToDB {
    /**
     * Метод для подключения к БД
     * Возвращает экземпляр подключения
     * Если что-то пошло не так - вызываем исключения
    * */
    static Connection connectToDatabase() {
        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            connectToDBLogger.error("Can't get class for database. No driver found", e);
            throw new ApplicationException("Can't get class for database. No driver found", e, 500);
        }
        Connection conn;
        try {
            conn = DriverManager.getConnection(connectionString, loginDB, passwordDB);
        } catch (SQLException e) {
            connectToDBLogger.error("Can't get connection to database", e);
            throw new ApplicationException("Can't get connection to database", e, 500);
        }
        connectToDBLogger.debug("Connection to database has been created successfully");
        return conn;
    }
}
