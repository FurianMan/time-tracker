package com.github.FurianMan.time_tracker.scheduler;

import com.github.FurianMan.time_tracker.mysqlUtilities.CloseTasksByScheduler;
import org.quartz.*;

import static com.github.FurianMan.time_tracker.Constants.scheduledTasksLogger;

public class CloseTasksExecutor implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        CloseTasksByScheduler.closeTaskByScheduler();
        scheduledTasksLogger.info("Job CloseTasksByScheduler has been succeed");
    }
}