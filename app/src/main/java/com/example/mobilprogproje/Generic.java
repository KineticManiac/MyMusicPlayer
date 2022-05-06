package com.example.mobilprogproje;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.appcompat.app.AlertDialog;

public class Generic {
    public static void showDialog(Context context, String message){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setMessage(message);
        dialogBuilder.create().show();
    }

    public static void showToast(Context context, String message, int length){
        Toast toast = Toast.makeText(context, message, length);
        toast.show();
    }

    public static void sendEmail(Context context, String[] to, String subject, String message){
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, to);
        email.putExtra(Intent.EXTRA_SUBJECT, subject);
        email.putExtra(Intent.EXTRA_TEXT, message);

        //need this to prompts email client only
        email.setType("message/rfc822");

        context.startActivity(Intent.createChooser(email, "Choose an E-mail client :"));
    }

    public static void startLoadingImage(ComponentActivity activity, int request_code){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        activity.startActivityForResult(
                Intent.createChooser(intent, "Choose a picture : "), request_code);
    }

    public static Bitmap finishLoadingImage(ComponentActivity activity, Intent data){
        Uri uri = data.getData();
        try{
            return MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri);
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static void inputString(ComponentActivity ct, String title, Setter<String> stringSetter){
        final LayoutInflater inflater = ct.getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_input_string, null);
        final EditText input = view.findViewById(R.id.DialogInputStringEditText);

        AlertDialog.Builder builder = new AlertDialog.Builder(ct);
        builder.setTitle(title);
        builder.setView(view);

        builder.setPositiveButton("OK", (dialogInterface, i) -> {
            stringSetter.set(input.getText().toString());
        });

        builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
            stringSetter.set(null);
        });

        builder.show();
    }

    public interface Setter<T>{
        void set(T t);
    }
}
