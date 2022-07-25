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
import static com.github.FurianMan.time_tracker.mysqlUtilities.GetUser.getUser;

public class UpdateUser {
    private static final Logger mysqlLogger = Constants.getMysqlLogger();

    /**
     * Перед изменением информации у пользователя, пытаемся его найти через метод getUser
     * @param userForUpdate: экземпляр класса, из которого мы получаем всю необходимую информацию
     * Если какое-то поля не передал пользователь для изменения, то берется значение из БД
     * Изменить можно name, surname, patronymic, position, birthday.
     * */
    public static void updateUser(TableUsers userForUpdate) throws ApplicationException {
        if (userForUpdate.getUser_id() == 0 &&
                (userForUpdate.getName() == null || userForUpdate.getSurname() == null || userForUpdate.getBirthday() == null)) {
            mysqlLogger.error("One or more required fields are empty in PUT query, check fields: name, surname, birthday, user_id");
            throw new ApplicationException("One or more required fields are empty in PUT query, check fields: name, surname, birthday, user_id", 415);
        }
        TableUsers userDB = getUser(userForUpdate);
        int user_id = userDB.getUser_id();
        String name;
        String surname;
        String patronymic;
        String position;
        String birthday;
        name = userForUpdate.getNewName() == null ? userDB.getName() : userForUpdate.getNewName();
        surname = userForUpdate.getNewSurname() == null ? userDB.getSurname() : userForUpdate.getNewSurname();
        patronymic = userForUpdate.getNewPatronymic() == null ? userDB.getPatronymic() : userForUpdate.getNewPatronymic();
        position = userForUpdate.getNewPosition() == null ? userDB.getPosition() : userForUpdate.getNewPosition();
        birthday = userForUpdate.getNewBirthday() == null ? userDB.getBirthday() : userForUpdate.getNewBirthday();
        Connection conn = connectToDatabase();
        try {
            Statement statmt = conn.createStatement();
            String sqlQuery = String.format("UPDATE users SET " +
                    "name='%s', surname='%s', patronymic='%s', position='%s', birthday='%s' WHERE user_id=%d;", name, surname, patronymic, position, birthday, user_id);
            mysqlLogger.debug(sqlQuery);
            statmt.executeUpdate(sqlQuery);
            mysqlLogger.info(String.format("User has been updated successfully, new values: name=%s, surname=%s, position=%s, birthday=%s", name, surname, position, birthday));
        } catch (SQLException e) {
            mysqlLogger.error("Can't execute query 'updateUser' to database", e);
            throw new ApplicationException("Can't execute query 'updateUser' to database", e, 500);
        } finally {
            disconnectToDatabase(conn);
        }
    }
}
