package com.github.FurianMan.time_tracker.scheduler;

import com.github.FurianMan.time_tracker.mysqlUtilities.ClearDatabaseByScheduler;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import static com.github.FurianMan.time_tracker.Constants.schedulerLogger;

public class ClearDatabaseExecutor implements Job {

    /**
     * Исполнитель задач по расписанию
     * */
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        ClearDatabaseByScheduler.clearDatabase();
        schedulerLogger.info("Job ClearDatabaseByScheduler has been succeed");
    }
}
