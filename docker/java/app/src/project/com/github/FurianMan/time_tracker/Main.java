package com.github.FurianMan.time_tracker;

import com.github.FurianMan.time_tracker.mysqlUtilities.CheckConnectionToDB;

public class Main {

    public static void main(String[] args) {

        MyHttpServer.startServer();
        CheckConnectionToDB.checkConnectionToDB();
    }
}

//TODO обернуть все в неизвестные ошибки. Экспериментально сделал обработку парсинга
//TODO получить id всех незавершенных задач по пользователю
//TODO проверить сумму по одной задаче, но если затрекано дважды
//TODO вынести в dockerfile конфигурацию логера
//TODO разобраться почему происходит дублирование только при некорректном json.