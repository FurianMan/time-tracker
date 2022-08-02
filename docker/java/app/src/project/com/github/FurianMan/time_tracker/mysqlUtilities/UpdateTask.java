package com.github.FurianMan.time_tracker.mysqlUtilities;

import com.github.FurianMan.time_tracker.exceptions.ApplicationException;
import com.github.FurianMan.time_tracker.mysqlTables.TableTasks;
import com.github.FurianMan.time_tracker.mysqlTables.TableUsers;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.github.FurianMan.time_tracker.Constants.updateTaskLogger;
import static com.github.FurianMan.time_tracker.mysqlUtilities.ConnectToDB.connectToDatabase;
import static com.github.FurianMan.time_tracker.mysqlUtilities.DisconnectToDB.disconnectToDatabase;
import static com.github.FurianMan.time_tracker.mysqlUtilities.GetUser.getUser;

public class UpdateTask {

    /**
     * Метод нужен для закрытие задач, т.е. проставления текущего времени в поле end_time таблицы tasks
     * @param taskForUpdate: экземпляр класса, из которого мы получаем всю необходимую информацию
     * end_time мы генерируем в методе, текущее время
     * */
    public static void updateTask(TableTasks taskForUpdate) {
        int user_id = taskForUpdate.getUser_id();
        int task_id = taskForUpdate.getTask_id();

        if (task_id == 0 || user_id == 0) {
            updateTaskLogger.error(String.format("One or more required fields are empty in `updateTask`: task_id=%d, user_id=%d", task_id, user_id));
            throw new ApplicationException("One or more required fields are empty in PUT query, check field: task_id, user_id", 415);
        }
        // генерируем текущее время для end_time
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        taskForUpdate.setEnd_time(dateFormat.format(date));
        String end_time = taskForUpdate.getEnd_time();

         /*
        Создаем класс пользователя и проверяем его существавание в db
        * */
        TableUsers userDB = new TableUsers();
        userDB.setUser_id(user_id);
        getUser(userDB);

        /*
        * Проверяем закрыт ли уже трекинг по задаче,
        * А после смотрим существует ли открытый трекинг.
        * Необходима именно такая последовательность, т.к. иначе будет всегда
        * вызываться исключение из checkOpenTask()
        * */
        // проверяем закрыт ли уже трекинг по задаче
        CheckTask.checkCloseTask(taskForUpdate);
        // проверяем существует ли уже такая открытая задача
        CheckTask.checkOpenTask(taskForUpdate);

        Connection conn = connectToDatabase();
        try {
            Statement statmt = conn.createStatement();
            String sqlQuery = String.format("UPDATE tasks SET end_time=Cast('%s' as datetime) WHERE task_id=%d;", end_time, task_id);
            updateTaskLogger.debug(sqlQuery);
            statmt.executeUpdate(sqlQuery);
            updateTaskLogger.info(String.format("Task with task_id=%d has been closed successfully", task_id));
        } catch (SQLException e) {
            updateTaskLogger.error("Cannot execute query `updateTask` to database", e);
            throw new ApplicationException("Cannot execute query `updateTask` to database", e, 500);
        } finally {
            disconnectToDatabase(conn);
        }
    }
}
