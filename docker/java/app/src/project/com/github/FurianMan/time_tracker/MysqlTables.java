package com.github.FurianMan.time_tracker;

import com.google.gson.annotations.Expose;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface JsonRequired {
}

class TableUsers {
    @Expose
    private int user_id;
    private String patronymic;
    @JsonRequired
    private String name;
    @JsonRequired
    private String surname;
    @JsonRequired
    private String position;
    @JsonRequired
    private String birthday;
    private String newPatronymic;
    private String newName;
    private String newSurname;
    private String newPosition;
    private String newBirthday;
    @Expose
    private final String tableDestination = "time_tracker.users";

    TableUsers(String name, String surname, String patronymic, String position, String birthday) {
        this.name = name;
        this.surname = surname;
        this.patronymic = patronymic;
        this.position = position;
        this.birthday = birthday;
    }

    TableUsers(String name, String surname, String position, String birthday) {
        this.name = name;
        this.surname = surname;
        this.position = position;
        this.birthday = birthday;
    }

    TableUsers() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
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

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getTableDestination() {
        return tableDestination;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    public String getNewBirthday() {
        return newBirthday;
    }

    public void setNewBirthday(String newBirthday) {
        this.newBirthday = newBirthday;
    }

    public String getNewPatronymic() {
        return newPatronymic;
    }

    public void setNewPatronymic(String newPatronymic) {
        this.newPatronymic = newPatronymic;
    }

    public String getNewPosition() {
        return newPosition;
    }

    public String getNewSurname() {
        return newSurname;
    }

    public void setNewPosition(String newPosition) {
        this.newPosition = newPosition;
    }

    public void setNewSurname(String newSurname) {
        this.newSurname = newSurname;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return " Surname: " + this.surname + "Name: " + this.name + " Patronymic: " + this.patronymic + " Position: " + this.position + " Birthday: " + this.birthday;
    }
    public ArrayList<String> getValues () {
        ArrayList<String> values = new ArrayList<>();
        values.add(getName());
        values.add(getSurname());
        values.add(getPatronymic());
        values.add(getPosition());
        values.add(getNewName());
        values.add(getNewSurname());
        values.add(getNewPatronymic());
        values.add(getNewPosition());
        return values;
    }

}

class TableTasks {
    @JsonRequired
    private String task_num;
    @JsonRequired
    private String start_time;
    private String end_time;

    TableTasks(String task_num, String start_time, String end_time) {
        this.task_num = task_num;
        this.start_time = start_time;
        this.end_time = end_time;
    }

    public String getTask_num() {
        return task_num;
    }

    public String getStart_time() {
        return start_time;
    }

    public String getEnd_time() {
        return end_time;
    }
}

class ResponseUserId {
    private int user_id;

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}
