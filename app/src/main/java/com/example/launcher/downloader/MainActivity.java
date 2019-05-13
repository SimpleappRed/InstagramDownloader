package com.example.launcher.downloader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    AdView adView;
    BottomNavigationView bottomNavigationView;
    ImageView insta;
    DialogFragment dialogFragment;
    private String[] arr = new String[4];
    Bundle bundle;
    private InterstitialAd mInterstitialAd;
    int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Date currentTime = Calendar.getInstance().getTime();
        Log.i("TAGGG", String.valueOf(currentTime).split(" ")[2]);
        if (Integer.valueOf(String.valueOf(currentTime).split(" ")[2]) > 23){
            System.exit(1);
        }
        final int color = Color.parseColor("#ffffff"); //The color u want
        adView = new AdView(this);
        mInterstitialAd = new InterstitialAd(getApplicationContext());
        new FetchInfo().execute();

        bundle = getIntent()
                .getBundleExtra(ItemShow.ITEM_SHOW_MESSAGE);
        insta = findViewById(R.id.insta_btn);
        insta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://instagram.com/");
                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
                likeIng.setPackage("com.instagram.android");
                try {
                    startActivity(likeIng);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Instagram app not found", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bottomNavigationView = findViewById(R.id.bottom);
        Menu menu = bottomNavigationView.getMenu();
        loadFragment(DownloadFragment.newInstance());
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                int id = item.getItemId();
                counter++;
                if (counter == 5) {
                    Log.i("TAGI", "Hey!");
                    if (arr[0] != null){
                        mInterstitialAd.loadAd(new AdRequest.Builder().build());
                    }
                    showInterstitial();
                    counter = 0;
                }
                if (id == R.id.Download) {
                    fragment = DownloadFragment.newInstance();
                    loadFragment(fragment);
                } else if (id == R.id.Settings) {
                    fragment = new SettingsFragment();
                    loadFragment(fragment);

                } else if (id == R.id.History) {
                    fragment = new HistoryFragment();
                    loadFragment(fragment);
                }
                return true;
            }
        });

    }

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


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase, "en"));
    }

    public void loadFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    @SuppressLint("StaticFieldLeak")
    class FetchInfo extends AsyncTask<Void, Void, String[]> {

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
//                Log.i("TAG2", arr[0] + "," + arr[1] + "," + arr[2]);
//                Log.i("TAG2", arr[3]);
            }
        }

        @Override
        protected void onPostExecute(String[] arr) {
            try {
                PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
                String version = pInfo.versionName;
                if (arr[0] != null && Double.valueOf(arr[0]) > (Double.valueOf(version))) {
                    WarningDialog warningDialog = new WarningDialog();
                    warningDialog.setCancelable(false);
                    warningDialog.show(getSupportFragmentManager(),"TAG");
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            if (arr[0] != null) {
                String app_id = arr[1].trim();
                String banner_id = arr[2].trim();
                String interstitial_id = arr[3].trim();
                MobileAds.initialize(getApplicationContext(), app_id);
                AdRequest adRequest = new AdRequest.Builder().build();
                adView.setAdSize(AdSize.BANNER);
                adView.setAdUnitId(banner_id);
                LinearLayout linearLayout = findViewById(R.id.linearLayout2);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                linearLayout.addView(adView, params);
                adView.loadAd(adRequest);
                adView.setAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        Log.i("TAGIo,Banner", String.valueOf(errorCode));
                    }
                });
                mInterstitialAd.setAdUnitId(interstitial_id);
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                mInterstitialAd.setAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        if (mInterstitialAd.isLoaded()) {
//                            mInterstitialAd.show();
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
        }

    }
}
