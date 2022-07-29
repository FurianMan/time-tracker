package com.github.FurianMan.time_tracker;

import com.github.FurianMan.time_tracker.httpserver.*;
import com.github.FurianMan.time_tracker.mysqlUtilities.*;
import com.github.FurianMan.time_tracker.utilities.Utilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants {
    public static final String driverName = "com.mysql.cj.jdbc.Driver";
    public static final String connectionString = "jdbc:mysql://db:3306/time_tracker";
    public static final String loginDB = "javauser";
    public static final String passwordDB = "javapassword";
    public static final String appVersion = "Time Tracker Version 0.1\n";
    public static final String contentType = "Content-Type";
    public static final String jsonFormat = "application/json;charset=utf-8";
    public static final String encoding = "utf-8";
    public static final int serverPort = Integer.parseInt(Utilities.getConstants("APP_PORT"));

    // http loggers
    public static final Logger httpServerMainLogger = LoggerFactory.getLogger(HttpServerMain.class);
    public static final Logger handleReqUserLogger = LoggerFactory.getLogger(HandleReqUser.class);
    public static final Logger handleReqVersionLogger = LoggerFactory.getLogger(HandleReqVersion.class);
    public static final Logger handleReqWorkLogger = LoggerFactory.getLogger(HandleReqWork.class);
    public static final Logger handleReqWorkStatsLogger = LoggerFactory.getLogger(HandleReqWorkStats.class);

    // utilities loggers
    public static final Logger utilitieslLogger = LoggerFactory.getLogger(Utilities.class);

    // MysqlUtilities loggers
    public static final Logger checkConnectionToDBLogger = LoggerFactory.getLogger(CheckConnectionToDB.class);
    public static final Logger checkTaskLogger = LoggerFactory.getLogger(CheckTask.class);
    public static final Logger clearStatsLogger = LoggerFactory.getLogger(ClearStats.class);
    public static final Logger connectToDBLogger = LoggerFactory.getLogger(ConnectToDB.class);
    public static final Logger deleteUserLogger = LoggerFactory.getLogger(DeleteUser.class);
    public static final Logger disconnectToDBLogger = LoggerFactory.getLogger(DisconnectToDB.class);
    public static final Logger getTaskLogger = LoggerFactory.getLogger(GetTask.class);
    public static final Logger getUserLogger = LoggerFactory.getLogger(GetUser.class);
    public static final Logger getWorkStatsOnelineLogger = LoggerFactory.getLogger(GetWorkStatsOneline.class);
    public static final Logger getWorkStatsPeriodLogger = LoggerFactory.getLogger(GetWorkStatsPeriod.class);
    public static final Logger getWorkStatsSumLogger = LoggerFactory.getLogger(GetWorkStatsSum.class);
    public static final Logger insertTaskLogger = LoggerFactory.getLogger(InsertTask.class);
    public static final Logger insertUserLogger = LoggerFactory.getLogger(InsertUser.class);
    public static final Logger updateTaskLogger = LoggerFactory.getLogger(UpdateTask.class);
    public static final Logger updateUserkLogger = LoggerFactory.getLogger(UpdateUser.class);
}
