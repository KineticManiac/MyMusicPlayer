package com.example.mobilprogproje;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuActivity extends AppCompatActivity {

    private User user;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");

        imageView = findViewById(R.id.MenuProfilePicture);
        imageView.setImageDrawable(user.getProfilePicture(this));

        TextView textView = findViewById(R.id.MenuUsername);
        textView.setText(user.getUsername());

        Button musicArchive = findViewById(R.id.MenuMusicArchive);
        musicArchive.setOnClickListener(view -> onClickMusicArchive());

        Button myPlaylists = findViewById(R.id.MenuMyPlaylists);
        myPlaylists.setOnClickListener(view -> onClickMyPlaylists());

        Button logout = findViewById(R.id.MenuLogout);
        logout.setOnClickListener(view -> onClickLogout());
    }

    private void onClickMusicArchive() {
        Intent intent = new Intent(this, MusicArchiveActivity.class);
        intent.putExtra("user", user);
        startActivityForResult(intent, 0);
    }

    private void onClickMyPlaylists() {
        Intent intent = new Intent(this, MyPlaylistsActivity.class); //TO-DO: Null yerine doğru activity'yi ekle
        intent.putExtra("user", user);
        startActivityForResult(intent, 0);
    }

    private void onClickLogout() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("user", user);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            user = (User) data.getSerializableExtra("user");
            user.refreshPlaylists(); //Eğer herhangi bir müzik silinmişse
        }
    }
}