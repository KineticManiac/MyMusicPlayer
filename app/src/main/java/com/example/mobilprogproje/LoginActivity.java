package com.example.mobilprogproje;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    static final int REQ_SIGNUP = 1;
    static final int REQ_MENU = 2;

    private ArrayList<User> userList;
    private int loginCount = 0;

    private int userIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //User List
        if(savedInstanceState != null){
            userList = (ArrayList<User>) savedInstanceState.getSerializable("userList");
        }
        else{
            userList = new ArrayList<>();
        }

        //Buttons
        findViewById(R.id.LoginSignup).setOnClickListener(view -> onSignupClick());
        findViewById(R.id.LoginLogin).setOnClickListener(view -> onLoginClick());

        setTitle("Log in"); //Program isminin "My Music Player" olabilmesi için
    }

    private void login(User user, int index){
        assert(userList.get(index) == user);
        userIndex = index;
        Intent intent = new Intent(this, MenuActivity.class);
        intent.putExtra("user", user);
        startActivityForResult(intent, REQ_MENU);
    }

    private void onLoginFail(){
        loginCount++;
        if(loginCount == 3) {
            Generic.showDialog(this, getString(R.string.invalid_credentials_3));
            onSignupClick();
        }
        else{
            Generic.showDialog(this, getString(R.string.invalid_credentials));
        }
    }

    private void onLoginClick(){
        EditText umail_et = (EditText) findViewById(R.id.LoginUmail);
        EditText password_et = (EditText) findViewById(R.id.LoginPassword);

        String umail = (umail_et).getText().toString();
        String password = (password_et).getText().toString();

        password_et.setText("");

        User.resultOfFindUser res = User.findUser(userList, umail);

        User user = res.getUser();

        if(user == null){
            onLoginFail();
            return;
        }

        if(!user.checkPassword(password)){
            onLoginFail();
            return;
        }

        login(user, res.getIndex());
    }

    private void onSignupClick(){
        Intent intent = new Intent(this, SignupActivity.class);
        intent.putExtra("userList", userList);
        startActivityForResult(intent, REQ_SIGNUP);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        EditText umail_et = (EditText) findViewById(R.id.LoginUmail);
        EditText password_et = (EditText) findViewById(R.id.LoginPassword);

        umail_et.setText("");
        password_et.setText("");

        if(resultCode == RESULT_OK) {
            User user;
            switch (requestCode){
                case REQ_SIGNUP:
                    user = (User) data.getSerializableExtra("user");

                    userList.add(user);
                    login(user, userList.size() - 1);
                    break;
                case REQ_MENU:
                    user = (User) data.getSerializableExtra("user");

                    userList.set(userIndex, user);
                    break;
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putSerializable("userList", userList);
        super.onSaveInstanceState(savedInstanceState);
    }
}