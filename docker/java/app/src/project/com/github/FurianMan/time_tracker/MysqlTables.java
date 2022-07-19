package com.github.FurianMan.time_tracker;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface JsonRequired {}
class TableUsers {
        private int user_id;
        private String patronymic = null;
        @JsonRequired private String name;
        @JsonRequired private String surname;
        @JsonRequired private String position;
        @JsonRequired private String birthday;
        private final String tableDestination = "time_tracker.users";
          TableUsers (String name, String surname, String patronymic, String position, String birthday) {
            this.name = name;
            this.surname = surname;
            this.patronymic = patronymic;
            this.position = position;
            this.birthday = birthday;
        }
         TableUsers (String name, String surname, String position, String birthday) {
            this.name = name;
            this.surname = surname;
            this.position = position;
            this.birthday = birthday;
        }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public String getPosition() {
        return position;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getTableDestination() {
        return tableDestination;
    }
    @Override
    public String toString () {
              return  " Surname: " + this.surname + "Name: " + this.name  + " Patronymic: " + this.patronymic + " Position: " + this.position + " Birthday: " + this.birthday;
    }
}
class TableTasks {
    @JsonRequired private String task_num;
    @JsonRequired private String start_time;
    private String end_time;
    TableTasks (String task_num, String start_time, String end_time) {
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
