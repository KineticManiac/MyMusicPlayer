package com.example.mobilprogproje;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;

import java.util.ArrayList;

public class MusicArchiveActivity extends AppCompatActivity {

    private User user;
    private Intent resultIntent;

    private RecyclerView rv;
    private MusicRecycleViewAdapter rvAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_archive);

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");

        resultIntent = new Intent();
        resultIntent.putExtra("user", user);
        setResult(RESULT_OK, resultIntent);

        Resources res = getResources();
        String titles[] = res.getStringArray(R.array.music_titles);
        String authors[] = res.getStringArray(R.array.music_authors);
        TypedArray files = res.obtainTypedArray(R.array.music_files);
        TypedArray bgs = res.obtainTypedArray(R.array.music_backgrounds);

        ArrayList<MusicInfo> musics = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            int fileId = files.getResourceId(i, -1);
            int bgId = bgs.getResourceId(i, -1);

            musics.add(new MusicInfo(titles[i], authors[i],
                    fileId, bgId, res));
        }

        musics.sort((m1, m2) -> m1.getTitle().compareToIgnoreCase(m2.getTitle()));

        rvAdapter = new MusicRecycleViewAdapter(this, musics.toArray(new MusicInfo[0]));
        rvAdapter.setOnClickListener(
                (button, musicInfo) -> {
            switch (button){
                case DOWNLOAD:
                    onMusicDownload((MusicInfo) musicInfo);
                    break;
                case DELETE:
                    onMusicDelete((MusicInfo) musicInfo);
                    break;
                default:
                    throw new RuntimeException("Impossible button.");
            }
        });

        rvAdapter.setButtonPrepareDelegate(new MusicRecycleViewAdapter.ButtonPrepareDelegate() {
            @Override
            public MusicRecycleViewAdapter.BUTTON getLeftButton(Recyclable musicInfo) {
                return MusicRecycleViewAdapter.BUTTON.NONE;
            }

            @Override
            public MusicRecycleViewAdapter.BUTTON getRightButton(Recyclable musicInfo) {
                if(user.hasMusic((MusicInfo) musicInfo))
                    return MusicRecycleViewAdapter.BUTTON.DELETE;
                else
                    return MusicRecycleViewAdapter.BUTTON.DOWNLOAD;
            }
        });

        rv = findViewById(R.id.MusicArchiveRecyclerView);
        rv.setAdapter(rvAdapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
    }

    private void onMusicDelete(MusicInfo musicInfo) {
        if(!user.removeMusic(musicInfo))
            throw new RuntimeException("User doesn\'t have music.");
        updateResult();
        rvAdapter.setItemRightButton(musicInfo, MusicRecycleViewAdapter.BUTTON.DOWNLOAD);
    }

    private void onMusicDownload(MusicInfo musicInfo) {
        assert !user.hasMusic(musicInfo);

        user.addMusic(musicInfo);
        updateResult();
        rvAdapter.setItemRightButton(musicInfo, MusicRecycleViewAdapter.BUTTON.DELETE);
    }

    private void updateResult(){
        resultIntent.putExtra("user", user);
    }
}