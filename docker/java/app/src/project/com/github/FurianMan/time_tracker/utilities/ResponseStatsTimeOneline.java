package com.github.FurianMan.time_tracker.utilities;

public class ResponseStatsTimeOneline extends ResponseStats {
    private int user_id;
    private String timeStatsAll;

    @Override
    public int getUser_id() {
        return user_id;
    }
    @Override
    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
    @Override
    // Этот метод тут не нужен...
    public void addStats(TimeStatsSum timeStatsSum) {return;}

    @Override
    // Этот метод тут не нужен...
    public void addStats(TimeStatsPeriod timeStatsPeriod) {return;}

    public String getTimeStatsAll() {
        return timeStatsAll;
    }

    public void setTimeStatsAll(String timeStatsAll) {
        this.timeStatsAll = timeStatsAll;
    }
}
