package com.github.FurianMan.time_tracker.mysqlUtilities;

import com.github.FurianMan.time_tracker.Constants;
import com.github.FurianMan.time_tracker.utilities.ResponseTaskId;
import com.github.FurianMan.time_tracker.exceptions.ApplicationException;
import com.github.FurianMan.time_tracker.mysqlTables.TableTasks;
import com.github.FurianMan.time_tracker.mysqlTables.TableUsers;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.github.FurianMan.time_tracker.mysqlUtilities.ConnectToDB.connectToDatabase;
import static com.github.FurianMan.time_tracker.mysqlUtilities.DisconnectToDB.disconnectToDatabase;
import static com.github.FurianMan.time_tracker.mysqlUtilities.GetTask.getTask;
import static com.github.FurianMan.time_tracker.mysqlUtilities.GetUser.getUser;

public class InsertTask {//TODO сделать проверку, что нет уже трекающей задачи. Надо искать задачу, у которой нет end_time, но мы не знаем время
    private static final Logger mysqlLogger = Constants.getMysqlLogger();

    public static ResponseTaskId insertTask(TableTasks newTask) throws ApplicationException {//TODO посмотреть, что будет, если внести task_id
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        newTask.setStart_time(dateFormat.format(date));

        int task_num = newTask.getTask_num();
        int user_id = newTask.getUser_id();
        String start_time = newTask.getStart_time();

        if (user_id == 0 && task_num == 0) {
            mysqlLogger.error("Request does not have required fields for 'insertTask', please check documentation");
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
            mysqlLogger.debug(sqlQuery);
            statmt.executeUpdate(sqlQuery);
            mysqlLogger.info(String.format("Отчет успешно начат по задаче: task_num=%d, user_id=%d", task_num, user_id));

        } catch (SQLException e) {
            mysqlLogger.error("Can't execute query 'insertTask' in database", e);
            throw new ApplicationException("Can't execute query 'insertTask' in database", e, 500);
        } finally {
            disconnectToDatabase(conn);
        }
        ResponseTaskId respTaskId = new ResponseTaskId();
        respTaskId.setTask_id(getTask(newTask).getTask_id());
        return respTaskId;
    }
}
