package com.example.mobilprogproje;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;

public class MusicPlayerActivity extends AppCompatActivity {

    MediaPlayer mediaPlayer;
    Playlist playlist;
    MusicInfo musicInfo;
    Resources resources;
    ImageView playButton, pauseButton, background;
    SeekBar seekBar;
    Playlist.Cursor cursor;
    PlayPauseBroadcastParser broadcastParser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        Intent intent = getIntent();
        playlist = (Playlist) intent.getSerializableExtra("playlist");
        musicInfo = (MusicInfo) intent.getSerializableExtra("start");
        cursor = playlist.cursor(musicInfo);

        resources = getResources();

        playButton = findViewById(R.id.MusicPlayerPlayButton);
        pauseButton = findViewById(R.id.MusicPlayerPauseButton);

        playButton.setOnClickListener(view -> play());
        pauseButton.setOnClickListener(view -> pause());

        broadcastParser = new PlayPauseBroadcastParser(this);
        broadcastParser.setOnBroadcastReceivedListener(play -> {
            if (play) {
                play();
            } else {
                pause();
            }
        });

        seekBar = findViewById(R.id.MusicPlayerSeekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            boolean autopause = false;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b)
                    mediaPlayer.seekTo(percentToMiliseconds(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if(mediaPlayer.isPlaying()){
                    autopause = true;
                    quickPause();
                }
                else{
                    autopause = false;
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(autopause){
                    quickPlay();
                }
            }
        });

        ImageView forwardsButton = findViewById(R.id.MusicPlayerForwardsButton);
        forwardsButton.setOnClickListener(view -> next());

        ImageView backwardsButton = findViewById(R.id.MusicPlayerBackwardsButton);
        backwardsButton.setOnClickListener(view -> previous());

        background = findViewById(R.id.MusicPlayerBackground);

        load(cursor.current());
        mediaPlayer.setOnCompletionListener(mediaPlayer -> next());
        play();
    }

    @Override
    protected void onDestroy(){
        mediaPlayer.release();
        super.onDestroy();
    }

    private void playlistFinished(){
        cursor.setPosition(0);
        pause();
        load(cursor.current());
    }

    private void next(){
        if(cursor.hasNext()) {
            load(cursor.next());
            play();
        }
        else
            playlistFinished();
    }

    private void previous(){
        if(cursor.hasPrevious()) {
            load(cursor.previous());
            play();
        }
        else
            playlistFinished();
    }

    private void load(MusicInfo musicInfo){
        if(mediaPlayer == null)
            mediaPlayer = new MediaPlayer();
        else {
            if (mediaPlayer.isPlaying())
                mediaPlayer.stop(); //Bu gerekli mi emin değilim
            mediaPlayer.reset();
        }

        background.setImageDrawable(resources.getDrawable(musicInfo.getBgId()));

        AssetFileDescriptor afd = resources.openRawResourceFd(musicInfo.getFileId());

        try {
            mediaPlayer.setDataSource(afd);
            mediaPlayer.prepare();
        }
        catch (Exception e) { throw new RuntimeException(e); }

        this.musicInfo = musicInfo;
        setTitle(musicInfo.getTitle() + " - " + musicInfo.getDescription());

        seekBar.setProgress(0);
    }


    private int percentToMiliseconds(float f){
        return (int) ((f / 100f) * musicInfo.getDuration());
    }

    private void quickPlay(){
        mediaPlayer.start();

        ProgressBarAnimation animation = new ProgressBarAnimation(
                seekBar, seekBar.getProgress(), 100);
        animation.setDuration(musicInfo.getDuration() - mediaPlayer.getCurrentPosition());
        seekBar.startAnimation(animation);
    }

    private void play(){
        quickPlay();

        playButton.setVisibility(View.INVISIBLE);
        pauseButton.setVisibility(View.VISIBLE);
    }

    private void quickPause(){
        mediaPlayer.pause();
        seekBar.clearAnimation();
    }

    private void pause(){
        quickPause();

        playButton.setVisibility(View.VISIBLE);
        pauseButton.setVisibility(View.INVISIBLE);
    }

    private class ProgressBarAnimation extends Animation{
        private ProgressBar progressBar;
        private float from;
        private float  to;

        public ProgressBarAnimation(ProgressBar progressBar, float from, float to) {
            super();
            this.progressBar = progressBar;
            this.from = from;
            this.to = to;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            float value = from + (to - from) * interpolatedTime;
            progressBar.setProgress((int) value);
        }
    }
}