package com.github.FurianMan.time_tracker.mysqlUtilities;

import com.github.FurianMan.time_tracker.Constants;
import com.github.FurianMan.time_tracker.exceptions.ApplicationException;
import com.github.FurianMan.time_tracker.mysqlTables.TableUsers;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static com.github.FurianMan.time_tracker.mysqlUtilities.ConnectToDB.connectToDatabase;
import static com.github.FurianMan.time_tracker.mysqlUtilities.DisconnectToDB.disconnectToDatabase;
import static com.github.FurianMan.time_tracker.mysqlUtilities.GetUser.getUser;

public class UpdateUser {
    private static final Logger mysqlLogger = Constants.getMysqlLogger();
    private static Connection conn;
    private static Statement statmt;
    private static ResultSet resSet;
    private static String sqlQuery;
    public static void updateUser (TableUsers userForUpdate) throws ApplicationException {
        /**
         * Перед изменением информации у пользователя, пытаемся его найти через метод getUser
         * @param userForUpdate: экземпляр класса, из которого мы получаем всю необходимую информацию
         * Если какое-то поля не передал пользователь для изменения, то берется значение из БД
         * Изменить можно name, surname, patronymic, position, birthday.
         * */
        if (userForUpdate.getUser_id()==0 &&
                (userForUpdate.getName()==null || userForUpdate.getSurname()==null || userForUpdate.getBirthday()==null)) {
            mysqlLogger.error("Request does not have required fields for updating, please check documentation");
            throw new ApplicationException("Can't update database user", 415);
        }
        TableUsers userDB = getUser(userForUpdate);
        int user_id = userDB.getUser_id();
        String name;
        String surname;
        String patronymic;
        String position;
        String birthday;
        name = userForUpdate.getNewName() == null  ? userDB.getName() : userForUpdate.getNewName();
        surname = userForUpdate.getNewSurname() == null ? userDB.getSurname() : userForUpdate.getNewSurname();
        patronymic = userForUpdate.getNewPatronymic() == null ? userDB.getPatronymic() : userForUpdate.getNewPatronymic();
        position = userForUpdate.getNewPosition() == null ? userDB.getPosition() : userForUpdate.getNewPosition();
        birthday = userForUpdate.getNewBirthday() == null ? userDB.getBirthday() : userForUpdate.getNewBirthday();
        conn = connectToDatabase();
        try {
            statmt = conn.createStatement();
            String query = String.format("UPDATE users SET " +
                    "name='%s', surname='%s', patronymic='%s', position='%s', birthday='%s' WHERE user_id=%d;", name, surname, patronymic, position, birthday, user_id);
            mysqlLogger.debug(query);
            statmt.executeUpdate(query);
            mysqlLogger.info(String.format("Пользователь успешно обновлен, новые значения: name=%s, surname=%s, position=%s, birthday=%s", name, surname, position, birthday));
        } catch (SQLException e) {
            mysqlLogger.error("Can't update database user", e);
            throw new ApplicationException("Can't update database user", e, 500);
        } finally {
            disconnectToDatabase(conn);
        }
    }
}
