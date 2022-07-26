package com.github.FurianMan.time_tracker.utilities;

import java.util.LinkedList;
import java.util.List;

public class ResponseStatsTime {
    private int user_id;
    private List<TimeStats> allTimeStats = new LinkedList<>();

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void addStats(TimeStats timeStats) {
        this.allTimeStats.add(timeStats);
    }
}
