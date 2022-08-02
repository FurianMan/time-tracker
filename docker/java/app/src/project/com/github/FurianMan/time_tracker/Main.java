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

//TODO обернуть все в неизвестные ошибки. Экспериментально сделал обработку парсинга
//TODO получить id всех незавершенных задач по пользователю
//TODO проверить сумму по одной задаче, но если затрекано дважды
//TODO сделать тесты