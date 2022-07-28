package com.github.FurianMan.time_tracker.utilities;

public class ResponseStatsTimeOneline extends ResponseStats {
    private int user_id;
    private String timeStatsOneline;

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

    public String getTimeStatsOneline() {
        return timeStatsOneline;
    }

    /**
     * Метод конвертации минут в формат hh:mm
     * Получаем данные из БД в минутах.
     * Эти минуты из бд - время затраченное на все задачи
     * в течение какого-то периода
     * */
    public void setTimeStatsOneline(int timeStatsOneline) {
        String newHours;
        String newMinutes;

        if ( timeStatsOneline == 0) {
            this.timeStatsOneline = "00:00";
            System.out.println(this.timeStatsOneline);
            return;
        } else if (timeStatsOneline / 60 < 10){
            newHours = '0' + String.valueOf(timeStatsOneline /60);
        } else {
            newHours = String.valueOf(timeStatsOneline /60);
        }

        if (timeStatsOneline %60 % 60 < 10) {
            newMinutes = '0' + String.valueOf(timeStatsOneline %60);
        } else {
            newMinutes = String.valueOf(timeStatsOneline %60);
        }

        this.timeStatsOneline =  newHours + ":" + newMinutes;
    }
}
