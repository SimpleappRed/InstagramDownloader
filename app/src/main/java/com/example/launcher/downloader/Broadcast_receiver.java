package com.example.launcher.downloader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class Broadcast_receiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service_intent = new Intent(context, InstaClipBoard.class);
        context.startService(service_intent);
    }
}
