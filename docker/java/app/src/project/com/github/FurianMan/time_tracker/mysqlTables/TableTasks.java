package com.github.FurianMan.time_tracker.mysqlTables;

public class TableTasks {

    private int task_num;

    private String start_time;
    private String end_time;
    private int user_id;

    public void setTask_num(int task_num) {
        this.task_num = task_num;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getTask_num() {
        return task_num;
    }

    public String getStart_time() {
        return start_time;
    }

    public String getEnd_time() {
        return end_time;
    }
}
