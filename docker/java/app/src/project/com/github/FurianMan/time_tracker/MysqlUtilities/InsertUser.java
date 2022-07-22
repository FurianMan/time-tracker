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
import static com.github.FurianMan.time_tracker.MysqlUtilities.GetUser.getUser;

public class InsertUser {
    private static final Logger mysqlLogger = Constants.getMysqlLogger();
    private static Connection conn;
    private static Statement statmt;
    private static ResultSet resSet;
    private static String sqlQuery;
    public static MysqlTables.ResponseUserId insertUser(MysqlTables.TableUsers newUser) throws ApplicationException {
        newUser.setUser_id(0); // нужно занулить, чтобы если пользователь укажет его, то мы по нему после не искали в getUser
        String name = newUser.getName();
        String surname = newUser.getSurname();
        String patronymic = newUser.getPatronymic();
        String position = newUser.getPosition();
        String birthday = newUser.getBirthday();
        if (name==null || surname==null || birthday==null) {
            mysqlLogger.error("One or more required fields are empty in POST query, check fields: name, surname, birthday");
            throw new ApplicationException("One or more required fields are empty in POST query, check fields: name, surname, birthday", 415);
        }
        conn = connectToDatabase();
        try {
            statmt = conn.createStatement();
            statmt.executeUpdate(String.format("INSERT INTO users (name, surname, patronymic, position, birthday) VALUES ('%s', '%s', '%s', '%s', '%s');", name, surname, patronymic, position, birthday));
            mysqlLogger.info(String.format("Пользователь успешно создан: name=%s, surname=%s, position=%s, birthday=%s", name, surname, position, birthday));

        } catch (SQLException e) {
            mysqlLogger.error("Can't insert query in database", e);
            throw new ApplicationException("Can't insert query in database", e, 500);
        } finally {
            disconnectToDatabase(conn);
        }
        MysqlTables.ResponseUserId respUserId = new MysqlTables.ResponseUserId();
        respUserId.setUser_id(getUser(newUser).getUser_id());
        return respUserId;
    }
}
