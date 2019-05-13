package com.example.launcher.downloader;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class WarningDialog extends DialogFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.warning_layout,container,false);
        Button button = view.findViewById(R.id.update_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String link = "https://play.google.com/store/apps/details?id=com.downloader6";
                new Availability().execute(link);
            }
        });
        return view;
    }

    @SuppressLint("StaticFieldLeak")
    class Availability extends AsyncTask<String,Void,Void>{
        boolean available = false;
        @Override
        protected void onPostExecute(Void aVoid) {
            Uri uri;
            if (available){
                uri = Uri.parse("https://play.google.com/store/apps/details?id=com.downloader6");

            }else{
                uri = Uri.parse("http://instadownloader.blogfa.com/post/3");
            }
            Intent intent = new Intent(Intent.ACTION_VIEW,uri);
            startActivity(intent);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                URL uri = new URL(strings[0]);
                URLConnection ec = uri.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        ec.getInputStream(), "UTF-8"));
                String inputLine;
                StringBuilder a = new StringBuilder();
                while ((inputLine = in.readLine()) != null)
                    a.append(inputLine);
                if (a.length() > 2000){
                    available = true;
                }
                Log.i("TAGGG", String.valueOf(a.length()));
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
