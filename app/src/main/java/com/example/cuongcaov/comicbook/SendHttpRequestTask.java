package com.example.cuongcaov.comicbook;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Copyright © 2017 Asian Tech Co., Ltd.
 * Created by cuongcaov on 27/10/2017
 */

public class SendHttpRequestTask extends AsyncTask<String, Void, Bitmap> {

    private Context mContext;

    public SendHttpRequestTask(Context context) {
        this.mContext = context;
    }

    @Override
    protected Bitmap doInBackground(String... params) {

        try {
            URL url = new URL(params[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            File folder = new File(mContext.getFilesDir(), "content");
            folder.mkdir();
            File file = new File(folder, "btnv.nv");
            if (file.exists()) file.delete();
            FileOutputStream out = new FileOutputStream(file);
            myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            Log.i("tag11", file.getPath());
            return myBitmap;
        } catch (Exception e) {
            Log.d("tag11", e.getMessage());
        }

        return null;
    }

}
