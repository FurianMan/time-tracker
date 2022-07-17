package com.github.FurianMan.time_tracker;

public class MysqlTables {

     class Users {
        private int user_id;
        private String name;
        private String surname;
        private String patronymic = null;
        private String position;
        private String birthday;
        private final String tableDestination = "time_tracker.users";
        Users (String name, String surname, String patronymic, String position, String birthday) {
            this.name = name;
            this.surname = surname;
            this.patronymic = patronymic;
            this.position = position;
            this.birthday = birthday;
        }
        Users (String name, String surname, String position, String birthday) {
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
        public void display () {
            System.out.println(name +" "+ surname +" "+  position +" "+ birthday);
        }
    }
    class Tasks {
        private String task_num;
        private String start_time;
        private String end_time;
        Tasks (String task_num, String start_time, String end_time) {
            this.task_num = task_num;
            this.start_time = start_time;
            this.end_time = end_time;
        }

        public String getTask_num() {
            return task_num;
        }

        public void setTask_num(String task_num) {
            this.task_num = task_num;
        }

        public void setEnd_time(String end_time) {
            this.end_time = end_time;
        }

        public String getEnd_time() {
            return end_time;
        }

        public String getStart_time() {
            return start_time;
        }

        public void setStart_time(String start_time) {
            this.start_time = start_time;
        }
    }
}