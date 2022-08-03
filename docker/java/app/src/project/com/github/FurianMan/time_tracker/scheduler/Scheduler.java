package com.github.FurianMan.time_tracker.scheduler;

import com.github.FurianMan.time_tracker.exceptions.ApplicationException;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import static com.github.FurianMan.time_tracker.Constants.schedulerLogger;

public class Scheduler {

    /**
     * Метод используется для инициализации scheduler, jobs, triggers.
     * Таким образом можно создавать задачи по-расписанию.
     * Этот метод настраивает поведение и добавляет jobs в расписание
    * */
    public static void startScheduler() {
        JobDetail job = JobBuilder
                .newJob(CloseTasksExecutor.class)
                .withIdentity("CloseTasksExecutor", "group1")
                .build();

        JobDetail job2 = JobBuilder
                .newJob(ClearDatabaseExecutor.class)
                .withIdentity("ClearDatabase", "group2")
                .build();

        Trigger trigger = TriggerBuilder
                .newTrigger()
                .withIdentity("CloseTasksExecutorTrigger", "group1")
                .withSchedule(CronScheduleBuilder
                        .cronSchedule("* 59 23 * * ?"))
                .build();

        Trigger trigger2 = TriggerBuilder
                .newTrigger()
                .withIdentity("ClearDatabaseTrigger", "group2")
                .withSchedule(CronScheduleBuilder
                        .cronSchedule("* 50 23 * * ?"))
                .build();


        SchedulerFactory schedFact = new StdSchedulerFactory();
        org.quartz.Scheduler sched = null;
        try {
            sched = schedFact.getScheduler();
            sched.start();
            sched.scheduleJob(job,trigger);
            sched.scheduleJob(job2,trigger2);
            schedulerLogger.info("Scheduler has been started successfully");
        } catch (SchedulerException e) {
            schedulerLogger.error("Failed start scheduler: \n" + e);
        } catch (ApplicationException e) {
            schedulerLogger.error("Failed execute job: \n" + e);
        }
    }
}
