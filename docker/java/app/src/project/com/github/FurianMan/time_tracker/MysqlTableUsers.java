package com.github.FurianMan.time_tracker;

public class MysqlTableUsers {
    private int user_id;
    private String name;
    private String surname;
    private String patronymic = null;
    private String position;
    private String birthday;
    private final String tableDestination = "time_tracker.users";

    MysqlTableUsers (String name, String surname, String patronymic, String position, String birthday) {
        this.name = name;
        this.surname = surname;
        this.patronymic = patronymic;
        this.position = position;
        this.birthday = birthday;
    }
    MysqlTableUsers (String name, String surname, String position, String birthday) {
        this.name = name;
        this.surname = surname;
        this.position = position;
        this.birthday = birthday;
    }
    public String getName() {
        return name;
    }
    public  void setName(String name) {
        this.name = name;
    }
    public String getSurname() {
        return surname;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }
    public String getBirthday() {
        return birthday;
    }
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
    public String getPatronymic() {
        return patronymic;
    }
    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }
    public String getPosition() {
        return position;
    }
    public void setPosition(String position) {
        this.position = position;
    }
    public String getTableDestination() {
        return tableDestination;
    }
}
