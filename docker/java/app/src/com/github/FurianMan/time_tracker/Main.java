package com.github.FurianMan.time_tracker;

import java.util.Map;

public class Main {
    public static void main(String[] args) {
        MyHttpServer.startServer();
//        new com.github.FurianMan.time_tracker.MysqlUtilities().connectToDatabase();
        //MysqlTableUsers MysqlData = new MysqlTableUsers("Егор", "Иванович", "QA", "1996-05-08");
        //new MysqlUtilities().InsertInto("INSERT INTO time_tracker.Users (name, surname, position, birthday) VALUES ('Влад', 'Рих', 'QA', '1993-10-01'); ");

    }
    interface TimeTrackerApi {
        
    }
}
