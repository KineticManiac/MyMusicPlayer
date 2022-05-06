package com.example.mobilprogproje;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Arrays;

public class MyPlaylistsActivity extends AppCompatActivity {

    private static final int REQ_CHANGE = 2;
    private static final int REQ_NEW = 1;

    private User user;
    private Intent resultIntent;

    private RecyclerView rv;
    private MusicRecycleViewAdapter rvAdapter;

    private Playlist selectedPlaylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");

        resultIntent = new Intent();
        resultIntent.putExtra("user", user);
        setResult(RESULT_OK, resultIntent);

        rvAdapter = new MusicRecycleViewAdapter(this, user.getPlaylists());
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
                    onClickPlay((Playlist) musicInfo);
                    break;
                case DELETE:
                    onClickDelete((Playlist) musicInfo);
                    break;
                default:
                    throw new RuntimeException("Impossible button.");
            }
        });

        rv = findViewById(R.id.PlaylistRecyclerView);
        rv.setAdapter(rvAdapter);
        rv.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton actionButton = findViewById(R.id.PlaylistNewButton);
        actionButton.setOnClickListener((view -> onClickNew()));
    }

    private void onClickNew() {
        Generic.inputString(this, "Enter title : ", title -> {
            if(title == null)
                return;

            if(title.length() == 0){
                Generic.showDialog(this, getString(R.string.bad_playlist_title));
                return;
            }

            if(Arrays.stream(user.getPlaylists()).anyMatch(playlist -> title.equals(playlist.getTitle()))){
                Generic.showDialog(this, getString(R.string.double_playlist_title));
                return;
            }

            Playlist playlist = new Playlist(title);

            Intent intent = new Intent(this, PlaylistActivity.class);
            intent.putExtra("allMusics", user.getMusics());
            intent.putExtra("playlist", playlist);
            startActivityForResult(intent, REQ_NEW);
        });
    }

    private void onClickDelete(Playlist pl){
        user.removePlaylist(pl);
        rvAdapter.removeItem(pl);
        updateResult();
    }

    private void onClickPlay(Playlist pl){
        selectedPlaylist = pl;

        Intent intent = new Intent(this, PlaylistActivity.class);
        intent.putExtra("allMusics", user.getMusics());
        intent.putExtra("playlist", pl);
        startActivityForResult(intent, REQ_CHANGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            Playlist playlist = (Playlist) data.getSerializableExtra("playlist");
            if(requestCode == REQ_NEW) {
                user.addPlaylist(playlist);
                rvAdapter.insertItem(playlist);
            }
            else if(requestCode == REQ_CHANGE){
                user.replacePlaylist(selectedPlaylist, playlist);
                rvAdapter.changeItem(selectedPlaylist, playlist);
            }
            else{
                throw new RuntimeException("Impossible request code.");
            }
            updateResult();
        }
    }

    private void updateResult(){
        resultIntent.putExtra("user", user);
    }
}