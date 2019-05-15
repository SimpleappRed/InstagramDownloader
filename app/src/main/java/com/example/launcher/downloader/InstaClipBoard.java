package com.example.launcher.downloader;

import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.IBinder;

public class InstaClipBoard extends Service {

    private ClipboardManager mClipboardManager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mClipboardManager =
                (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        assert mClipboardManager != null;
        mClipboardManager.addPrimaryClipChangedListener(
                mOnPrimaryClipChangedListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private ClipboardManager.OnPrimaryClipChangedListener mOnPrimaryClipChangedListener =
        new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                ClipData clip = mClipboardManager.getPrimaryClip();
                String paste = clip.getItemAt(0).getText().toString();
                if (paste.matches("https://www.instagram.com/p/(.*)")||paste.matches("https://www.instagram.com/tv/(.*)")) {
                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage(getApplicationContext().getPackageName());
                    if (launchIntent != null) {
                        try {
                            launchIntent.putExtra("paste",paste);
                            startActivity(launchIntent);
                        } catch (ActivityNotFoundException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        };

}