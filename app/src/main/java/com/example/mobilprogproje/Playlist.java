package com.example.mobilprogproje;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Playlist extends Recyclable implements Serializable {
    private String title;
    private final List<MusicInfo> musicInfoList;

    public Playlist(String title){
        this.title = title;
        musicInfoList = new ArrayList<>();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public MusicInfo[] getMusics(){
        return musicInfoList.toArray(new MusicInfo[0]);
    }

    public void addMusic(MusicInfo musicInfo){
        musicInfoList.add(musicInfo);
    }

    public void addMusic(int i, MusicInfo musicInfo){
        musicInfoList.add(i, musicInfo);
    }

    public void removeMusic(int i){
        musicInfoList.remove(i);
    }

    public void removeMusic(MusicInfo m){
        musicInfoList.remove(m);
    }

    @NonNull
    public Cursor cursor() {
        return new Cursor();
    }

    @NonNull
    public Cursor cursor(int i){
        return new Cursor(i);
    }

    @NonNull
    public Cursor cursor(MusicInfo musicInfo){
        return new Cursor(musicInfo);
    }

    @Override
    int getDuration() {
        int total = 0;
        for(MusicInfo musicInfo : musicInfoList){
            total += musicInfo.getDuration();
        }
        return total;
    }

    @Override
    int getIconId() {
        return R.drawable.music_icon;
    }

    @Override
    String getDescription() {
        return musicInfoList.size() + " songs";
    }

    public class Cursor {
        private int position;

        public Cursor(){
            this(0);
        }

        public Cursor(int position) {
            setPosition(position);
        }

        public Cursor(MusicInfo musicInfo){
            setPosition(musicInfo);
        }

        public void setPosition(MusicInfo musicInfo){
            position = -1;
            Iterator<MusicInfo> i = musicInfoList.iterator();
            MusicInfo m = null;
            while(!musicInfo.equals(m) && i.hasNext()){
                m = i.next();
                position++;
            }
            assert musicInfo.equals(m);
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public int getPosition() {
            return position;
        }

        public MusicInfo next(){
            assert hasNext();
            moveNext();
            return current();
        }

        public void moveNext(){
            position++;
        }

        public boolean isValid(){
            return position >= 0 && position < musicInfoList.size();
        }

        public boolean hasNext(){
            return position < musicInfoList.size() - 1;
        }

        public void movePrevious(){
            position--;
        }

        public MusicInfo previous(){
            assert hasPrevious();
            movePrevious();
            return current();
        }

        public boolean hasPrevious(){
            return position > 0;
        }

        public MusicInfo current(){
            return musicInfoList.get(position);
        }
    }
}
