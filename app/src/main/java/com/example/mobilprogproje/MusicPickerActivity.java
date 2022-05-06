package com.example.mobilprogproje;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

public class MusicPickerActivity extends AppCompatActivity {

    private MusicRecycleViewAdapter rvAdapter;
    private RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_archive);

        //Eğer kullanıcı geri tuşuna basarak dönerse
        setResult(RESULT_CANCELED);

        Intent intent = getIntent();
        MusicInfo[] musics = (MusicInfo[]) intent.getSerializableExtra("musics");

        rvAdapter = new MusicRecycleViewAdapter(this, musics);
        rvAdapter.setButtonPrepareDelegate(new MusicRecycleViewAdapter.ButtonPrepareDelegate() {
            @Override
            public MusicRecycleViewAdapter.BUTTON getLeftButton(Recyclable musicInfo) {
                return MusicRecycleViewAdapter.BUTTON.NONE;
            }

            @Override
            public MusicRecycleViewAdapter.BUTTON getRightButton(Recyclable musicInfo) {
                return MusicRecycleViewAdapter.BUTTON.DOWNLOAD;
            }
        });
        rvAdapter.setOnClickListener(((button, musicInfo) -> {
            assert button == MusicRecycleViewAdapter.BUTTON.DOWNLOAD;
            onClick((MusicInfo) musicInfo);
        }));

        rv = findViewById(R.id.MusicArchiveRecyclerView);
        rv.setAdapter(rvAdapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
    }

    private void onClick(MusicInfo musicInfo) {
        Intent intent = new Intent();
        intent.putExtra("music", musicInfo);
        setResult(RESULT_OK, intent);
        finish();
    }
}