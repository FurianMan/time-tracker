package com.github.FurianMan.time_tracker.mysqlUtilities;

import com.github.FurianMan.time_tracker.utilities.ResponseTaskId;
import com.github.FurianMan.time_tracker.exceptions.ApplicationException;
import com.github.FurianMan.time_tracker.mysqlTables.TableTasks;
import com.github.FurianMan.time_tracker.mysqlTables.TableUsers;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.github.FurianMan.time_tracker.Constants.insertTaskLogger;
import static com.github.FurianMan.time_tracker.mysqlUtilities.ConnectToDB.connectToDatabase;
import static com.github.FurianMan.time_tracker.mysqlUtilities.DisconnectToDB.disconnectToDatabase;
import static com.github.FurianMan.time_tracker.mysqlUtilities.GetTask.getTask;
import static com.github.FurianMan.time_tracker.mysqlUtilities.GetUser.getUser;

public class InsertTask {//TODO сделать проверку, что нет уже трекающей задачи. Надо искать задачу, у которой нет end_time, но мы не знаем время
    /**
     * Медот для внесения задач в mysql
     * От пользователя нам нужны task_num и user_id
     * Дату start_time мы генирируем и вносим самостоятельно в рамках метода
     *
     * @param newTask - данные из запроса от пользователя упакованные в класс
     *
     * Отдаем пользовалю task_id, который упаковываем в класс ResponseTaskId
    * */
    public static ResponseTaskId insertTask(TableTasks newTask) {//TODO посмотреть, что будет, если внести task_id
        // генерируем дату start_time от текущего времени
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        newTask.setStart_time(dateFormat.format(date));

        int task_num = newTask.getTask_num();
        int user_id = newTask.getUser_id();
        String start_time = newTask.getStart_time();

        // проверка, что все обязательный параметры для запроса не пустые.
        if (user_id == 0 && task_num == 0) {
            insertTaskLogger.error("Request does not have required fields for 'insertTask', please check documentation");
            throw new ApplicationException("Request does not have required fields for 'insertTask'. Can't execute query to database", 415);
        }
        // проверяем существует ли уже такая открытая задача
        CheckTask.checkOpenTaskErr(newTask);
        /*
        Создаем класс пользователя и проверяем его существавание в db
        * */
        TableUsers userDB = new TableUsers();
        userDB.setUser_id(user_id);
        getUser(userDB);

        Connection conn = connectToDatabase();
        try {
            Statement statmt = conn.createStatement();
            String sqlQuery = (String.format("INSERT INTO tasks (task_num, user_id, start_time) VALUES (%d, %d, Cast('%s' as datetime));", task_num, user_id, start_time));
            insertTaskLogger.debug(sqlQuery);
            statmt.executeUpdate(sqlQuery);
            insertTaskLogger.info(String.format("The task report has been started successfully: task_num=%d, user_id=%d", task_num, user_id));

        } catch (SQLException e) {
            insertTaskLogger.error("Cannot execute query `insertTask` in database", e);
            throw new ApplicationException("Cannot execute query `insertTask` in database", e, 500);
        } finally {
            disconnectToDatabase(conn);
        }

        /*
        * respTaskId - экземпляр класса, которому мы присвоим task_id.
        * Чтобы получить этот task_id, мы передаем полученный экземпляр с текущим временем в start_time
        * Таким образом в getTask мы ищем нашу задачу и получаем её task_id, который передаем в respTaskId
        *
        * Далее respTaskId уходит на упаковку в json к пользователю
        * */
        ResponseTaskId respTaskId = new ResponseTaskId();
        respTaskId.setTask_id(getTask(newTask).getTask_id());
        return respTaskId;
    }
}
