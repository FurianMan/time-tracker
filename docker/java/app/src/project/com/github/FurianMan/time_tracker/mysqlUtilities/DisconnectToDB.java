package com.github.FurianMan.time_tracker.mysqlUtilities;

import com.github.FurianMan.time_tracker.exceptions.ApplicationException;

import java.sql.Connection;
import java.sql.SQLException;

import static com.github.FurianMan.time_tracker.Constants.disconnectToDBLogger;

public class DisconnectToDB {

    static void disconnectToDatabase(Connection conn) {
        try {
            conn.close();
        } catch (SQLException e) {
            disconnectToDBLogger.error("Cannot close connection to database", e);
            throw new ApplicationException("Cannot close connection to database", e, 500);
        }
        disconnectToDBLogger.debug("Connection to database has been closed successfully");
    }
}
