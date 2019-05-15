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
    private String[] arr = new String[4];
    Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final int color = Color.parseColor("#ffffff"); //The color u want
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
}
