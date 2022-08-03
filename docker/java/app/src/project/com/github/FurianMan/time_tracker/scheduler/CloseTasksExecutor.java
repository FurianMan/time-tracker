package com.github.FurianMan.time_tracker.scheduler;

import com.github.FurianMan.time_tracker.mysqlUtilities.CloseTasksByScheduler;
import org.quartz.*;

import static com.github.FurianMan.time_tracker.Constants.schedulerLogger;

public class CloseTasksExecutor implements Job {

    /**
     * Исполнитель задач по расписанию
    * */
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        CloseTasksByScheduler.closeTaskByScheduler();
        schedulerLogger.info("Job CloseTasksByScheduler has been succeed");
    }
}