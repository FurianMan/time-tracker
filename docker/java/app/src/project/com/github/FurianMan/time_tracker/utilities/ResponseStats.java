package com.github.FurianMan.time_tracker.utilities;

public abstract class ResponseStats {

    public abstract int getUser_id();

    public abstract void setUser_id(int user_id);

    public abstract void addStats(TimeStatsSum timeStatsSum);
    public abstract void addStats(TimeStatsPeriod timeStatsPeriod);


}
