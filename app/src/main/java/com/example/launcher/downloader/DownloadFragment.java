package com.example.launcher.downloader;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;

public class DownloadFragment extends Fragment {

    public static String link = "https://www.instagram.com/p/";
    private String[] arr = new String[4];
    public static String link2 = "https://www.instagram.com/tv/";
    public InstaDownloader insta;
    EditText input;
    Boolean aBoolean = false;
    Button download_btn;
    RadioButton with_caption;

    public static DownloadFragment newInstance() {
        return new DownloadFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        Intent svc = new Intent(getContext(), InstaClipBoard.class);
        getContext().startService(svc);
        View view = inflater.inflate(R.layout.fragment_downlaod, container, false);
        download_btn = view.findViewById(R.id.download_btn);
        input = view.findViewById(R.id.link_txt);
        insta = new InstaDownloader(getContext());
        with_caption = view.findViewById(R.id.with_caption);

        download_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                link = input.getText().toString().trim();
                if (link.length() > 0) {
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                        if (PackageManager.PERMISSION_GRANTED !=
                                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            ActivityCompat.requestPermissions((Activity) getContext(), new
                                    String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        } else {
                            Click();
                        }
                    } else {
                        Click();
                    }
                }
            }
        });

        input.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    String url = input.getText().toString().trim();
                    String id = url.replace(link, "");
                    if (!id.isEmpty())
                        insta.get(url, false);
                    else
                        Toast.makeText(getContext(), "URL not valid.", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });


        return view;
    }

    public void Click() {
        if (with_caption.isChecked()) {
            insta.get(link, true);
        } else {
            Log.i("access", input.getText().toString());
            insta.get(link, false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // clipboard listener
        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        assert clipboard != null;
        if ((clipboard.hasPrimaryClip())) {
            if ((clipboard.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_PLAIN))) {
                final ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
                String paste = item.getText().toString();
                if (paste.matches(link + "(.*)") || paste.matches(link2 + "(.*)")) {
                    input.setText(item.getText().toString());
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                aBoolean = true;
                Click();
                // permission was granted, yay! Do the
                // contacts-related task you need to do.
            } else {

                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                Toast.makeText(getContext(), "You must accept this to download files", Toast.LENGTH_SHORT).show();
            }
            return;
        }
    }

}
