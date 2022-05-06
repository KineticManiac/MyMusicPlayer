package com.example.mobilprogproje;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;

public class SignupActivity extends AppCompatActivity {

    ArrayList<User> userList;
    static final int REQ_PICTURE = 1;
    Bitmap bmp;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Intent intent = getIntent();
        userList = (ArrayList<User>) intent.getSerializableExtra("userList");
        //Signup Button
        findViewById(R.id.SignupSignup).setOnClickListener(view -> signup());
        //Profile Picture
        imageView = findViewById(R.id.SignupProfilePicture);
        imageView.setOnClickListener(view -> onClickProfilePic());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == REQ_PICTURE){
                bmp = Generic.finishLoadingImage(this, data);
                imageView.setImageBitmap(bmp);
            }
        }
    }

    private void onClickProfilePic(){
        Generic.startLoadingImage(this, REQ_PICTURE);
    }

    private void signup(){
        //Get all data

        String username = ((EditText) findViewById(R.id.SignupUsername)).getText().toString();
        if(!username.matches("[a-zA-Z][a-zA-Z0-9]+")){
            Generic.showDialog(this, getString(R.string.bad_username));
            return;
        }

        String email = ((EditText) findViewById(R.id.SignupEmail)).getText().toString();
        if(!(!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches())){
            Generic.showDialog(this, getString(R.string.bad_email));
            return;
        }
        if(!email.equals(((EditText) findViewById(R.id.SignupEmail2)).getText().toString())){
            Generic.showDialog(this, getString(R.string.email_mismatch));
            return;
        }

        String password = ((EditText) findViewById(R.id.SignupPassword)).getText().toString();
        if(!password.matches("[a-zA-Z0-9]+")){
            Generic.showDialog(this, getString(R.string.bad_password));
            return;
        }
        if(!password.equals(((EditText) findViewById(R.id.SignupPassword2)).getText().toString())){
            Generic.showDialog(this, getString(R.string.password_mismatch));
            return;
        }

        //Create user
        User user = User.findUser(userList, username, email).getUser();
        if(user != null){
            if(user.getUsername().equals(username)){
                Generic.showDialog(this, getString(R.string.username_already_exists));
            }
            else{
                Generic.showDialog(this, getString(R.string.email_is_used));
            }
            return;
        }
        user = new User(username, email, password);

        if(bmp != null)
            user.setProfilePicture(this, bmp);

        //Send email
        Generic.sendEmail(this, new String[]{email}, getString(R.string.email_subject),
                getString(R.string.email_message, username));

        //Return back message
        Intent resultIntent = new Intent();
        resultIntent.putExtra("user", user);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

}