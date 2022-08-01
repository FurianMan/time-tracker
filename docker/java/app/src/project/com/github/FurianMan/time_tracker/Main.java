package com.github.FurianMan.time_tracker;

import com.github.FurianMan.time_tracker.httpserver.HttpServerMain;
import com.github.FurianMan.time_tracker.mysqlUtilities.CheckConnectionToDB;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.scheduling.annotation.EnableScheduling;

//@SpringBootApplication
//@EnableScheduling
public class Main {

    public static void main(String[] args) {

        HttpServerMain.startServer();
        CheckConnectionToDB.checkConnectionToDB();
//        SpringApplication.run(Main.class);
    }
}

//TODO обернуть все в неизвестные ошибки. Экспериментально сделал обработку парсинга
//TODO получить id всех незавершенных задач по пользователю
//TODO проверить сумму по одной задаче, но если затрекано дважды
//TODO сделать тесты