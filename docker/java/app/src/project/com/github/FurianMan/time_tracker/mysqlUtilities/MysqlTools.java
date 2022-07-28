package com.github.FurianMan.time_tracker.mysqlUtilities;

import com.github.FurianMan.time_tracker.Constants;
import com.github.FurianMan.time_tracker.exceptions.ApplicationException;
import org.slf4j.Logger;

import java.sql.*;


public class MysqlTools { // TODO надо понять что делаем с классом, потому что сейчас тут есть только логгер
    private static final String driverName = Constants.getDriverName();
    private static final String connectionString = Constants.getConnectionString();
    private static final String login = Constants.getDbLogin();
    private static final String password = Constants.getDbPassword();
    private static final Logger mysqlLogger = Constants.getMysqlLogger();
    private static Connection conn;
    private static Statement statmt;
    private static ResultSet resSet;
    private static String sqlQuery;


    public static void deleteUser() throws ApplicationException {

    }
}
