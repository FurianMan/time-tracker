package com.github.FurianMan.time_tracker;

import com.github.FurianMan.time_tracker.mysqlUtilities.CheckConnectionToDB;

public class Main {

    public static void main(String[] args) {

        MyHttpServer.startServer();
        CheckConnectionToDB.checkConnectionToDB();
    }
}

//TODO исправить все ответы по http, возможно убрать кавычки
//TODO обернуть все в неизвестные ошибки