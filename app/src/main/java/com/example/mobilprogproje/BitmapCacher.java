package com.example.mobilprogproje;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;

public class BitmapCacher implements Serializable {
    private static int cacheCount = 0;
    private final String filename;

    public BitmapCacher(Context ct, Bitmap bmp){
        File file = new File(ct.getCacheDir(), "bmp" + cacheCount);
        filename = file.getPath();
        cacheCount++;
        saveBitmap(bmp);
    }

    public void saveBitmap(Bitmap bmp){
        try(FileOutputStream fos = new FileOutputStream(filename)){
            bmp.compress(Bitmap.CompressFormat.PNG, 0, fos);
        }
        catch (Exception e) {throw new RuntimeException(e);}
    }

    public Bitmap loadBitmap(Context ct){
        try{
            return MediaStore.Images.Media.getBitmap(
                    ct.getContentResolver(), Uri.fromFile(new File(filename)));
        }
        catch (Exception e) {throw new RuntimeException(e);}
    }
}
