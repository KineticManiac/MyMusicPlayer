package com.example.mobilprogproje;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

public class User implements Serializable {
    private final String username, email, password;
    private final List<MusicInfo> musics;
    private final List<Playlist> playlists;
    private BitmapCacher profilePicture;

    public User(String username, String email, String password){
        this.username = username;
        this.email = email;
        this.password = password;
        this.musics = new ArrayList<>();
        this.playlists = new ArrayList<>();
    }

    public Drawable getProfilePicture(Context ct) {
        if(profilePicture == null)
            return ct.getResources().getDrawable(R.drawable.default_profile_picture);
        else
            return new BitmapDrawable(ct.getResources(), profilePicture.loadBitmap(ct));
    }

    public void setProfilePicture(Context ct, Bitmap profilePicture) {
        if(this.profilePicture == null)
            this.profilePicture = new BitmapCacher(ct, profilePicture);
        else
            this.profilePicture.saveBitmap(profilePicture);
    }

    public String getUsername(){
        return username;
    }

    public String getEmail(){
        return email;
    }

    public boolean checkPassword(String password){
        return password.equals(this.password);
    }

    public void addMusic(MusicInfo music){
        musics.add(music);
    }

    public boolean removeMusic(MusicInfo music){
        return musics.remove(music);
    }

    public boolean hasMusic(MusicInfo music){
        return musics.contains(music);
    }

    //Otomatik alfabetik sıralama
    public MusicInfo[] getMusics(){
        musics.sort((m1, m2) -> m1.getTitle().compareToIgnoreCase(m2.getTitle()));
        return musics.toArray(new MusicInfo[0]);
    }

    public void addPlaylist(Playlist playlist) { playlists.add(playlist); }

    public boolean removePlaylist(Playlist playlist) { return playlists.remove(playlist); }

    public void replacePlaylist(Playlist oldPlaylist, Playlist newPlaylist){
        removePlaylist(oldPlaylist);
        addPlaylist(newPlaylist);
    }

    //Silinmiş müzikleri oynatma listelerinden kaldır
    private void refreshPlaylist(Playlist playlist){
        Playlist.Cursor cursor = playlist.cursor();
        while(cursor.isValid()){
            MusicInfo musicInfo = cursor.current();
            if(musics.contains(musicInfo)){
                cursor.moveNext();
            }
            else{
                playlist.removeMusic(cursor.getPosition());
            }
        }
    }

    public void refreshPlaylists(){
        for (Playlist playlist : playlists) {
            refreshPlaylist(playlist);
        }
    }

    //Otomatik alfabetik sıralama
    public Playlist[] getPlaylists(){
        playlists.sort((pl1, pl2) -> pl1.getTitle().compareToIgnoreCase(pl2.getTitle()));
        return playlists.toArray(new Playlist[0]);
    }

    static public resultOfFindUser findUser(List<User> userList, String umail){
        return findUser(userList, umail, umail);
    }

    static public resultOfFindUser findUser(List<User> userList, String username, String email){
        ListIterator<User> i = userList.listIterator();
        User user = null;
        int index = -1;
        while(user == null && i.hasNext()){
            User u = i.next();
            index++;
            if(u.getUsername().equals(username) || u.getEmail().equals(email))
                user = u;
        }
        return new resultOfFindUser(user, index);
    }

    //findUser metodunun döndüreceği tür
    static public class resultOfFindUser{
        private final User user;
        private final int index;

        public resultOfFindUser(User user, int index){
            this.user = user;
            this.index = index;
        }

        public User getUser() {
            return user;
        }

        public int getIndex() {
            return index;
        }
    }
}
