package com.github.FurianMan.time_tracker.utilities;

import java.util.LinkedList;
import java.util.List;

public class ResponseStatsTimeSum extends ResponseStats {
    private int user_id;
    private List<TimeStats> timeStatsSum = new LinkedList<>();

    @Override
    public int getUser_id() {
        return user_id;
    }
    @Override
    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
    @Override
    public void addStats(TimeStats timeStats) {
        this.timeStatsSum.add(timeStats);
    }
}
