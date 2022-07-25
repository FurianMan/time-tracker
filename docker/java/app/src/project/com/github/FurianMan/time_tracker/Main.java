package com.github.FurianMan.time_tracker;

import com.github.FurianMan.time_tracker.mysqlUtilities.CheckConnectionToDB;

public class Main {

    public static void main(String[] args) {

        MyHttpServer.startServer();
        CheckConnectionToDB.checkConnectionToDB();
    }
}

//TODO исправить все ответы по http, почему-то вижу их в utf-16
