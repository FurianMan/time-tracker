package com.github.FurianMan.time_tracker.utilities;

import java.util.LinkedList;
import java.util.List;

public class ResponseStatsTimePeriod extends ResponseStats {
    private int user_id;
    private List<TimeStatsPeriod> timeStatsPeriods = new LinkedList<TimeStatsPeriod>();

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
    public void addStats(TimeStatsPeriod timeStatsPeriod) {
        this.timeStatsPeriods.add(timeStatsPeriod);
    }
}
