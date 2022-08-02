package com.github.FurianMan.time_tracker;

import java.text.SimpleDateFormat;

import com.github.FurianMan.time_tracker.exceptions.ApplicationException;
import com.github.FurianMan.time_tracker.mysqlUtilities.CloseTasksByScheduler;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import static com.github.FurianMan.time_tracker.Constants.scheduledTasksLogger;

public class ScheduledTasks implements Job {
    public static void startScheduler() {
        JobDetail job = JobBuilder
                .newJob(ScheduledTasks.class)
                .withIdentity("ScheduledTasks", "group1")
                .build();

        Trigger trigger = TriggerBuilder
                .newTrigger()
                .withIdentity("ScheduledTasksTrigger", "group1")
                .withSchedule(CronScheduleBuilder
                        .cronSchedule("3 * * * * ?"))
                .build();


        SchedulerFactory schedFact = new StdSchedulerFactory();
        Scheduler sched = null;
        try {
            sched = schedFact.getScheduler();
            sched.start();
            sched.scheduleJob(job,trigger);
            scheduledTasksLogger.info("Scheduler has been started successfully");
        } catch (SchedulerException e) {
            scheduledTasksLogger.error("Failed start scheduler: \n" + e);
        } catch (ApplicationException e) {
            scheduledTasksLogger.error("Failed execute job: \n" + e);
        }
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        CloseTasksByScheduler.closeTaskByScheduler();
        scheduledTasksLogger.info("Job CloseTasksByScheduler has been succeed");
    }
}