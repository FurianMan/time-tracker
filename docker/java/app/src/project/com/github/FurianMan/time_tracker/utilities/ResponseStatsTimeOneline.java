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

    /**
     * Метод конвертации минут в формат hh:mm
     * Получаем данные из БД в минутах.
     * Эти минуты из бд - время затраченное на все задачи
     * в течение какого-то периода
     * */
    public void setTimeStatsAll(int timeStatsAll) {
        String newHours;
        String newMinutes;

        if ( timeStatsAll == 0) {
            this.timeStatsAll = "00:00";
            System.out.println(this.timeStatsAll);
            return;
        } else if (timeStatsAll / 60 < 10){
            newHours = '0' + String.valueOf(timeStatsAll/60);
        } else {
            newHours = String.valueOf(timeStatsAll/60);
        }

        if (timeStatsAll%60 % 60 < 10) {
            newMinutes = '0' + String.valueOf(timeStatsAll%60);
        } else {
            newMinutes = String.valueOf(timeStatsAll%60);
        }

        this.timeStatsAll =  newHours + ":" + newMinutes;
    }
}
