package com.github.FurianMan.time_tracker;

import com.github.FurianMan.time_tracker.httpserver.HttpServerMain;
import com.github.FurianMan.time_tracker.mysqlUtilities.CheckConnectionToDB;
import com.github.FurianMan.time_tracker.scheduler.Scheduler;


public class Main {

    public static void main(String[] args) {

        HttpServerMain.startServer();
        CheckConnectionToDB.checkConnectionToDB();
        Scheduler.startScheduler();

    }
}

//TODO Сделать GET и DELETE по спецификации
//TODO Сделать несколько логгеров, по структуре пакетов