package com.example.mobilprogproje;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;

public class PlaylistActivity extends AppCompatActivity {

    private static final int REQ_NEW = 1;

    private MusicInfo[] allMusics;
    private Playlist playlist;
    private Intent resultIntent;

    private MusicRecycleViewAdapter rvAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        Intent intent = getIntent();
        allMusics = (MusicInfo[]) intent.getSerializableExtra("allMusics");
        playlist = (Playlist) intent.getSerializableExtra("playlist");

        setTitle(playlist.getTitle());

        resultIntent = new Intent();
        updateResult();
        setResult(RESULT_OK, resultIntent);

        rvAdapter = new MusicRecycleViewAdapter(this, playlist.getMusics());
        rvAdapter.setButtonPrepareDelegate(new MusicRecycleViewAdapter.ButtonPrepareDelegate() {
            @Override
            public MusicRecycleViewAdapter.BUTTON getLeftButton(Recyclable musicInfo) {
                return MusicRecycleViewAdapter.BUTTON.PLAY;
            }

            @Override
            public MusicRecycleViewAdapter.BUTTON getRightButton(Recyclable musicInfo) {
                return MusicRecycleViewAdapter.BUTTON.DELETE;
            }
        });

        rvAdapter.setOnClickListener((button, musicInfo) -> {
            switch (button){
                case PLAY:
                    onClickPlay((MusicInfo) musicInfo);
                    break;
                case DELETE:
                    onClickDelete((MusicInfo) musicInfo);
                    break;
                default:
                    throw new RuntimeException("Impossible button.");
            }
        });

        RecyclerView rv = findViewById(R.id.PlaylistRecyclerView);
        rv.setAdapter(rvAdapter);
        rv.setLayoutManager(new LinearLayoutManager(this));

        rvAdapter.makeDraggable((recyclable, position) -> {
            MusicInfo musicInfo = (MusicInfo) recyclable;
            playlist.removeMusic(musicInfo);
            playlist.addMusic(position, musicInfo);
            return true;
        });

        FloatingActionButton actionButton = findViewById(R.id.PlaylistNewButton);
        actionButton.setOnClickListener((view -> onClickNew()));
    }

    private void onClickNew() {
        Intent intent = new Intent(this, MusicPickerActivity.class);
        ArrayList<MusicInfo> musics = new ArrayList<>(Arrays.asList(allMusics));
        for(MusicInfo musicInfo : playlist.getMusics()){
            musics.remove(musicInfo);
        }
        intent.putExtra("musics", musics.toArray(new MusicInfo[0]));
        startActivityForResult(intent, REQ_NEW);
    }

    private void onClickDelete(MusicInfo m){
        playlist.removeMusic(m);
        rvAdapter.removeItem(m);
        updateResult();
    }

    private void onClickPlay(MusicInfo m){
        Intent intent = new Intent(this, MusicPlayerActivity.class);
        intent.putExtra("playlist", playlist);
        intent.putExtra("start", m);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == REQ_NEW){
                MusicInfo music = (MusicInfo) data.getSerializableExtra("music");
                playlist.addMusic(music);
                rvAdapter.insertItem(music);
                updateResult();
            }
        }
    }

    private void updateResult(){
        resultIntent.putExtra("playlist", playlist);
    }
}