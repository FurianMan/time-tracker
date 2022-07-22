package com.github.FurianMan.time_tracker.MysqlUtilities;

import com.github.FurianMan.time_tracker.Constants;
import com.github.FurianMan.time_tracker.Exceptions.ApplicationException;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DisconnectToDB {
    private static final Logger mysqlLogger = Constants.getMysqlLogger();

    static void disconnectToDatabase(Connection conn) throws ApplicationException {
        try {
            conn.close();
        } catch (SQLException e) {
            mysqlLogger.error("Can't close connection to database", e);
            throw new ApplicationException("Can't close connection to database", e, 500);
        }
    }
}
