package com.github.FurianMan.time_tracker.mysqlUtilities;

import com.github.FurianMan.time_tracker.Constants;
import com.github.FurianMan.time_tracker.exceptions.ApplicationException;
import org.slf4j.Logger;

import java.sql.*;

public class ConnectToDB {
    private static final String driverName = Constants.getDriverName();
    private static final String connectionString = Constants.getConnectionString();
    private static final String login = Constants.getDbLogin();
    private static final String password = Constants.getDbPassword();
    private static final Logger mysqlLogger = Constants.getMysqlLogger();
    private static Connection conn;

    static Connection connectToDatabase() throws ApplicationException {
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
        mysqlLogger.info("Connection to database has been created successfully");
        return conn;
    }
}
