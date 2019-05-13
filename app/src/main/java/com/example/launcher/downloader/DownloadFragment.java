package com.example.launcher.downloader;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;

public class DownloadFragment extends Fragment {

    public static String link = "https://www.instagram.com/p/";
    private String[] arr = new String[4];
    public static String link2 = "https://www.instagram.com/tv/";
    public InstaDownloader insta;
    EditText input;
    Boolean aBoolean = false;
    Button download_btn;
    private int counter = 0;
    private InstagramApp mApp;
    private InterstitialAd mInterstitialAd;
    RadioButton with_caption;

    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                    Log.i("TAGI", "Yess");
                } else {
                    Log.d("TAGI", "The interstitial wasn't loaded yet.");
                }
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
//                Toast.makeText(getApplicationContext(), errorCode, Toast.LENGTH_SHORT).show();
                Log.i("TAG", "error code:" + String.valueOf(errorCode));
            }
        });
    }

    public static DownloadFragment newInstance() {
        return new DownloadFragment();
    }

    InstagramApp.OAuthAuthenticationListener listener = new InstagramApp.OAuthAuthenticationListener() {

        @Override
        public void onSuccess() {
        }

        @Override
        public void onFail(String error) {
            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        }
    };
    InstagramSession instagramSession;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        Intent svc = new Intent(getContext(), InstaClipBoard.class);

        mInterstitialAd = new InterstitialAd(getContext());
        new FetchInfo().execute();
        getContext().startService(svc);
        mApp = new InstagramApp(getContext(), ApplicationData.CLIENT_ID,
                ApplicationData.CLIENT_SECRET, ApplicationData.CALLBACK_URL);
        View view = inflater.inflate(R.layout.fragment_downlaod, container, false);
        download_btn = view.findViewById(R.id.download_btn);
        input = view.findViewById(R.id.link_txt);
        mApp.setListener(listener);
        insta = new InstaDownloader(getContext());
        instagramSession = new InstagramSession(getContext());
        RadioGroup radioGroup = view.findViewById(R.id.radioGroup);
        with_caption = view.findViewById(R.id.with_caption);
        RadioButton without_caption = view.findViewById(R.id.without_caption);

        insta.setAccessToken(instagramSession.getAccessToken());
        download_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter++;
                if (counter == 3) {
                    Log.i("TAGI", "in counter 3");
                    if (arr[0] != null) {
                        mInterstitialAd.loadAd(new AdRequest.Builder().build());
                    }
                    showInterstitial();
                    counter = 0;
                }
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


    @SuppressLint("StaticFieldLeak")
    class FetchInfo extends AsyncTask<Void,Void,String[]> {

        @Override
        protected void onPostExecute(String[] arr) {
            if(arr[0] != null) {
                Log.i("TAGII","we're here");
                mInterstitialAd.setAdUnitId(arr[3].trim());
                AdRequest adRequest = new AdRequest.Builder().build();
                mInterstitialAd.loadAd(adRequest);
            }

            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                        Log.i("TAGI", "Yess");
                    } else {
                        Log.d("TAGI", "The interstitial wasn't loaded yet.");
                    }
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
//                super.onAdFailedToLoad(errorCode);
//                    Toast.makeText(getContext(), String.valueOf(errorCode), Toast.LENGTH_SHORT).show();
                    Log.i("TAGI", "error code:" + String.valueOf(errorCode));
                }
            });
        }

        @Override
        protected String[] doInBackground(Void... voids) {

            try {
                URL uri = new URL("http://instadownloader.blogfa.com/post/2");
                URLConnection ec = uri.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        ec.getInputStream(), "UTF-8"));
                String inputLine;
                StringBuilder a = new StringBuilder();
                while ((inputLine = in.readLine()) != null)
                    a.append(inputLine);
                getAns(a);
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return arr;
        }
        private void getAns(StringBuilder stringBuilder) {
            String regex;
            regex = "newUpgrade:(.*?)</p>";
            Pattern pa = Pattern.compile(regex, Pattern.MULTILINE);
            Matcher ma = pa.matcher(stringBuilder);
            String string;
            if (ma.find()) {
                string = ma.group(1);
                arr = string.split(",");
                arr[3] = string.split(",")[3].substring(0,string.split(",")[3].length() -1 );
            }
        }

    }
}
