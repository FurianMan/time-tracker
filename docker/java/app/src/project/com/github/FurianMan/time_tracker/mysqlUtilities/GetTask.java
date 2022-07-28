package com.github.FurianMan.time_tracker.mysqlUtilities;

import com.github.FurianMan.time_tracker.Constants;
import com.github.FurianMan.time_tracker.exceptions.ApplicationException;
import com.github.FurianMan.time_tracker.mysqlTables.TableTasks;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static com.github.FurianMan.time_tracker.mysqlUtilities.ConnectToDB.connectToDatabase;
import static com.github.FurianMan.time_tracker.mysqlUtilities.DisconnectToDB.disconnectToDatabase;

public class GetTask {
    private static final Logger mysqlLogger = Constants.getMysqlLogger();

    /**
     * Ищем task в БД, доступен 1 вариант поиска
     * 1 - по полям user_id, task_num, start_time.
     * Если не нашли, то поднимаем исключение и информируем пользователя
     *
     * @param taskForSearch - передаем объект с данными пользователя
     *
     * Метод используется для поиска task_id, чтобы отдать его пользователю после
     * внесения новой задачи в БД
     */
    public static TableTasks getTask(TableTasks taskForSearch) throws ApplicationException {
        int user_id = taskForSearch.getUser_id();
        int task_num = taskForSearch.getTask_num();
        String start_time = taskForSearch.getStart_time();

        Connection conn;
        TableTasks taskInstance = new TableTasks();
        if (user_id != 0 && task_num != 0) { //TODO на самом деле этим методом пользуется только insertTask и там проверки эти уже есть
            conn = connectToDatabase();
            try {
                Statement statmt = conn.createStatement();
                String sqlQuery = (String.format("SELECT * FROM tasks WHERE user_id=%d AND task_num=%d AND start_time=Cast('%s' as datetime);", user_id, task_num, start_time));
                ResultSet resSet = statmt.executeQuery(sqlQuery);
                mysqlLogger.debug(String.format(sqlQuery));
                if (!resSet.next()) {
                    mysqlLogger.error(String.format("Cannot find in database task: user_id=%d task_num=%d start_time=%s", user_id, task_num, start_time));
                    throw new ApplicationException("Cannot find task in database", 404);
                } else {
                    do { //TODO можно сделать без цикла
                        taskInstance.setTask_id(resSet.getInt("task_id"));
                        taskInstance.setUser_id(resSet.getInt("user_id"));
                        taskInstance.setTask_num(resSet.getInt("task_num"));
                        taskInstance.setStart_time(resSet.getString("start_time"));
                        taskInstance.setEnd_time(resSet.getString("end_time"));
                    } while (resSet.next());
                }
                mysqlLogger.info(String.format("Task has been found successfully: user_id=%d task_num=%d start_time=%s", user_id, task_num, start_time));
            } catch (SQLException e) {
                mysqlLogger.error("Cannot execute query `getTask` to database", e);
                throw new ApplicationException("Cannot execute query `getTask` to database", e, 500);
            } finally {
                disconnectToDatabase(conn);
            }
            return taskInstance;
        } else {
            mysqlLogger.error("Request does not have required fields for method `getTask`");
            mysqlLogger.debug(String.format("Received fields in `getTask`:  user_id=%d, task_num=%d, start_time=%s", user_id, task_num, start_time));
            throw new ApplicationException("Request does not have required fields for searching, please check documentation", 415);
        }
    }
}
