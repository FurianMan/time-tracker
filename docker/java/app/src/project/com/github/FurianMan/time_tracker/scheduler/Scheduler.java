package com.github.FurianMan.time_tracker.scheduler;

import com.github.FurianMan.time_tracker.exceptions.ApplicationException;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import static com.github.FurianMan.time_tracker.Constants.scheduledTasksLogger;

public class Scheduler {
    public static void startScheduler() {
        JobDetail job = JobBuilder
                .newJob(CloseTasksExecutor.class)
                .withIdentity("CloseTasksExecutor", "group1")
                .build();

        Trigger trigger = TriggerBuilder
                .newTrigger()
                .withIdentity("CloseTasksExecutorTrigger", "group1")
                .withSchedule(CronScheduleBuilder
                        .cronSchedule("* 59 23 * * ?"))
                .build();


        SchedulerFactory schedFact = new StdSchedulerFactory();
        org.quartz.Scheduler sched = null;
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
}
