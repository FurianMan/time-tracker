package com.github.FurianMan.time_tracker.mysqlUtilities;

import com.github.FurianMan.time_tracker.exceptions.ApplicationException;
import com.github.FurianMan.time_tracker.mysqlTables.TableUsers;
import com.github.FurianMan.time_tracker.utilities.Utilities;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static com.github.FurianMan.time_tracker.Constants.updateUserkLogger;
import static com.github.FurianMan.time_tracker.mysqlUtilities.ConnectToDB.connectToDatabase;
import static com.github.FurianMan.time_tracker.mysqlUtilities.DisconnectToDB.disconnectToDatabase;
import static com.github.FurianMan.time_tracker.mysqlUtilities.GetUser.getUser;

public class UpdateUser {
    /**
     * Перед изменением информации у пользователя, пытаемся его найти через метод getUser
     * @param userForUpdate: экземпляр класса, из которого мы получаем всю необходимую информацию
     * Если какое-то поля не передал пользователь для изменения, то берется значение из БД
     * Изменить можно name, surname, patronymic, position, birthday.
     * */
    public static void updateUser(TableUsers userForUpdate) {
        if (userForUpdate.getUser_id() == 0 &&
                (userForUpdate.getName() == null || userForUpdate.getSurname() == null || userForUpdate.getBirthday() == null || userForUpdate.getPosition() == null)) {
            updateUserkLogger.error(String.format("One or more required fields are empty in `updateUser`, " +
                            "check fields: name=%s, surname=%s, birthday=%s, position=%s, user_id=%d",
                    userForUpdate.getName(), userForUpdate.getSurname(), userForUpdate.getBirthday(), userForUpdate.getPosition(), userForUpdate.getUser_id()));
            throw new ApplicationException("One or more required fields are empty in PUT query, check fields: name, surname, birthday, position, user_id", 415);
        }
        // получаем экземпляр класса с текущими значениями из БД и назначаем id
        TableUsers userDB = getUser(userForUpdate);
        int user_id = userDB.getUser_id();
        String name;
        String surname;
        String patronymic;
        String position;
        String birthday;

        // Валидируем полученные поля
        Utilities.validateUserFields(userForUpdate);

        // если не указали новое поле для изменения, то берем старое из экземпляра с текущими значениями из БД
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
            updateUserkLogger.debug(sqlQuery);
            statmt.executeUpdate(sqlQuery);
            updateUserkLogger.info(String.format("User has been updated successfully, new values: name=%s, surname=%s, position=%s, birthday=%s", name, surname, position, birthday));
        } catch (SQLException e) {
            updateUserkLogger.error("Cannot execute query `updateUser` to database", e);
            throw new ApplicationException("Can't execute query `updateUser` to database", e, 500);
        } finally {
            disconnectToDatabase(conn);
        }
    }
}
