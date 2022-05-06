package com.example.mobilprogproje;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.media.MediaPlayer;

import java.io.Serializable;

public class MusicInfo extends Recyclable implements Serializable {
    private final String title, author;
    private final int fileId, duration, bgId;

    public MusicInfo(String title, String author, int fileId, int bgId, Resources res){
        this.title = title;
        this.author = author;
        this.fileId = fileId;
        this.bgId = bgId;

        MediaPlayer mediaPlayer = new MediaPlayer();
        AssetFileDescriptor afd = res.openRawResourceFd(fileId);
        try {
            mediaPlayer.setDataSource(afd);
            mediaPlayer.prepare();
        }
        catch (Exception e) {throw new RuntimeException(e);}
        duration = mediaPlayer.getDuration();
        mediaPlayer.release();

    }

    @Override
    public boolean equals(Object x){
        if(x instanceof MusicInfo && this.fileId == ((MusicInfo) x).fileId)
            return true;
        else
            return false;
    }

    //Kullanmayacağım ama anladığım kadarıyla,
    // equals metodu override ediliyorsa, hashCode metodunun da override edilmesi uygundur.
    @Override
    public int hashCode(){
        return fileId;
    }

    public String getTitle() {
        return title;
    }

    public int getFileId() {
        return fileId;
    }

    public String getDescription() {
        return author;
    }

    public int getDuration() { return duration; }

    //İlk başta her müziğin ayrı bir ikonu olacak şekilde yapmıştım.
    public int getIconId() { return R.drawable.music_icon; }

    public int getBgId() {
        return bgId;
    }
}
