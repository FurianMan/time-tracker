package com.github.FurianMan.time_tracker;

import java.sql.*;
public class MysqlUtilities {
    private final String driverName = "com.mysql.cj.jdbc.Driver";
    private final String connectionString = "jdbc:mysql://100.110.1.1:3308/time_tracker";
    private final String login = "javauser";
    private final String password = "javapassword";
    private static Connection conn;
    private static Statement statmt;
    private static ResultSet resSet;

    public void connectToDatabase() {
        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            System.out.println("Can't get class. No driver found");
            e.printStackTrace();
            return;
        }
        //Connection conn = null;
        try {
            conn = DriverManager.getConnection(connectionString, login, password);
        } catch (SQLException e) {
            System.out.println("Can't get connection. Incorrect URL");
            e.printStackTrace();
            return;
        }
        try {
            conn.close();
        } catch (SQLException e) {
            System.out.println("Can't close connection");
            e.printStackTrace();
            return;
        }
    }
    public void InsertInto(String query) {
        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            System.out.println("Can't get class. No driver found");
            e.printStackTrace();
            return;
        }
        //Connection conn = null;
        try {
            conn = DriverManager.getConnection(connectionString, login, password);
        } catch (SQLException e) {
            System.out.println("Can't get connection. Incorrect URL");
            e.printStackTrace();
            return;
        }
        try {
            statmt = conn.createStatement();
            statmt.executeUpdate("INSERT INTO %s (name, surname, position, birthday) VALUES ('Влад', 'Рих', 'QA', '1993-10-01'); ");

            System.out.println("Таблица заполнена");
        }
        catch (SQLException e) {
            System.out.println("Can't insert query");
            e.printStackTrace();
            return;
        }
        try {
            conn.close();
        } catch (SQLException e) {
            System.out.println("Can't close connection");
            e.printStackTrace();
            return;
        }
    }
}