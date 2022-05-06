package com.example.mobilprogproje;

import android.content.Context;

//Bu çok kötü bir fikir, ama MusicRecycleViewAdapter'ı tekrar yazmak istemiyorum
public abstract class Recyclable {

    abstract int getDuration();
    abstract int getIconId();
    abstract String getTitle();
    abstract String getDescription();

    public String getDurationString(Context ct) {
        return getDurationString(ct, getDuration());
    }

    static public String getDurationString(Context ct, int duration){
        int ms = duration % 1000;
        duration /= 1000;
        int sec = duration % 60;
        duration /= 60;
        int min = duration % 60;
        duration /= 60;
        if(duration == 0){
            return ct.getString(R.string.duration_style_short, min, sec, ms);
        }
        else{
            return ct.getString(R.string.duration_style_long, duration, min, sec, ms);
        }
    }
}
