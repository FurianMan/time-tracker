package com.github.FurianMan.time_tracker.mysqlUtilities;

import com.github.FurianMan.time_tracker.Constants;
import com.github.FurianMan.time_tracker.exceptions.ApplicationException;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

public class DisconnectToDB {
    private static final Logger mysqlLogger = Constants.getMysqlLogger();

    static void disconnectToDatabase(Connection conn) throws ApplicationException {
        try {
            conn.close();
        } catch (SQLException e) {
            mysqlLogger.error("Can't close connection to database", e);
            throw new ApplicationException("Can't close connection to database", e, 500);
        }
        mysqlLogger.info("Connection to database has been closed successfully");
    }
}
