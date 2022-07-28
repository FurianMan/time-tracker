package com.github.FurianMan.time_tracker.mysqlUtilities;

import com.github.FurianMan.time_tracker.Constants;
import com.github.FurianMan.time_tracker.utilities.ResponseUserId;
import com.github.FurianMan.time_tracker.exceptions.ApplicationException;
import com.github.FurianMan.time_tracker.mysqlTables.TableUsers;
import com.github.FurianMan.time_tracker.utilities.Utilities;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static com.github.FurianMan.time_tracker.mysqlUtilities.ConnectToDB.connectToDatabase;
import static com.github.FurianMan.time_tracker.mysqlUtilities.DisconnectToDB.disconnectToDatabase;
import static com.github.FurianMan.time_tracker.mysqlUtilities.GetUser.getUser;

public class InsertUser {
    private static final Logger mysqlLogger = Constants.getMysqlLogger();

    /**
     * Метод необходим для внесения пользователя в таблицу users
     * Уникальным пользователь в mysql является по сумме полей:
     * name, surname, position, birthday.
     * Поэтому чтобы внести пользователя нужно минимум 4 поля.
     * Так же вносим отчество, но оно может быть null.
     * В БД генерируется время создание пользователя, есть триггер на insert в таблицу
     *
     * @param newUser - передаем объект с данными пользователя из запроса
     *
     * Метод возвращает user_id упакованный в класс
    * */
    public static ResponseUserId insertUser(TableUsers newUser) throws ApplicationException { //TODO если пользователь уже есть, то мы никак не проверям, просто выдаем ошибку
        newUser.setUser_id(0); // нужно занулить, чтобы если пользователь укажет его, то мы по нему после не искали в getUser
        String name = newUser.getName();
        String surname = newUser.getSurname();
        String patronymic = newUser.getPatronymic();
        String position = newUser.getPosition();
        String birthday = newUser.getBirthday();
        if (name == null || surname == null || birthday == null || position == null) {
            mysqlLogger.error("One or more required fields are empty in POST query, check fields: name, surname, birthday, position");
            throw new ApplicationException("One or more required fields are empty in POST query, check fields: name, surname, birthday, position", 415);
        }
        //Проверяем поля на корректность
        Utilities.validateUserFields(newUser);

        Connection conn = connectToDatabase();
        try {
            Statement statmt = conn.createStatement();
            String sqlQuery = (String.format("INSERT INTO users (name, surname, patronymic, position, birthday) VALUES ('%s', '%s', %s, '%s', '%s');", name, surname, patronymic, position, birthday));
            mysqlLogger.debug(sqlQuery);
            statmt.executeUpdate(sqlQuery);
            mysqlLogger.info(String.format("User has been created successfully: name=%s, surname=%s, position=%s, birthday=%s", name, surname, position, birthday));

        } catch (SQLException e) {
            mysqlLogger.error("Cannot execute query `insertUser` in database", e);
            throw new ApplicationException("Cannot execute query `insertUser` in database", e, 500);
        } finally {
            disconnectToDatabase(conn);
        }

        /*
         * respUserId - экземпляр класса, которому мы присвоим user_id.
         * Чтобы получить этот user_id, мы передаем полученный экземпляр newUser.
         * Таким образом в getUser мы ищем нашего пользователя и получаем его user_id, который передаем в respUserId
         *
         * Далее respUserId уходит на упаковку в json к пользователю
        * */
        ResponseUserId respUserId = new ResponseUserId();
        respUserId.setUser_id(getUser(newUser).getUser_id());
        return respUserId;
    }
}
