package com.github.FurianMan.time_tracker.mysqlUtilities;

import com.github.FurianMan.time_tracker.Constants;
import com.github.FurianMan.time_tracker.exceptions.ApplicationException;
import com.github.FurianMan.time_tracker.mysqlTables.TableUsers;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static com.github.FurianMan.time_tracker.mysqlUtilities.ConnectToDB.connectToDatabase;
import static com.github.FurianMan.time_tracker.mysqlUtilities.DisconnectToDB.disconnectToDatabase;

public class DeleteUser {
    private static final Logger mysqlLogger = Constants.getMysqlLogger();
    private static Connection conn;
    private static Statement statmt;
    private static String sqlQuery;

    public static void deleteUser(TableUsers delUser) throws ApplicationException {
        int user_id = delUser.getUser_id();
        String name = delUser.getName();
        String surname = delUser.getSurname();
        String birthday = delUser.getBirthday();
        if (delUser.getUser_id() == 0 &&
                (delUser.getName() == null || delUser.getSurname() == null || delUser.getBirthday() == null)) {
            mysqlLogger.error("Request does not have required fields for updating, please check documentation");
            throw new ApplicationException("Can't delete user from database", 415);
        }
        if (delUser.getUser_id() != 0) {
            conn = connectToDatabase();
            try {
                statmt = conn.createStatement();
                sqlQuery = String.format("DELETE FROM users WHERE user_id=%d;", user_id);
                mysqlLogger.debug(String.format(sqlQuery));
                statmt.executeUpdate(sqlQuery);
                mysqlLogger.info(String.format("User has been deleted successfully: user_id=%d", user_id));

            } catch (SQLException e) {
                mysqlLogger.error("Can't execute query 'deleteUser' in database", e);
                throw new ApplicationException("Can't execute query 'deleteUser' in database", e, 500);
            } finally {
                disconnectToDatabase(conn);
            }
        } else {
            conn = connectToDatabase();
            try {
                statmt = conn.createStatement();
                sqlQuery = String.format("DELETE FROM users WHERE name='%s' AND surname='%s' AND birthday='%s';", name, surname, birthday);
                mysqlLogger.debug(String.format(sqlQuery));
                statmt.executeUpdate(sqlQuery);
                mysqlLogger.info(String.format("User has been deleted successfully: name=%s, surname=%s, birthday=%s", name, surname, birthday));

            } catch (SQLException e) {
                mysqlLogger.error("Can't execute query 'deleteUser' in database", e);
                throw new ApplicationException("Can't execute query 'deleteUser' in database", e, 500);
            } finally {
                disconnectToDatabase(conn);
            }
        }
    }
}
