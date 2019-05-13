package com.example.launcher.downloader;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

public class SettingsFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings,container,false);
        TextView location_txt = view.findViewById(R.id.location_txt);
        Button persian_btn = view.findViewById(R.id.perisan_btn);
        Button arabic_btn = view.findViewById(R.id.arabic_btn);
        Button english_btn = view.findViewById(R.id.english_btn);
        location_txt.setText(Environment.getExternalStorageDirectory().getAbsolutePath()+"/InstagramDownloader");
        persian_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocale("fa");
            }
        });

        english_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocale("en");
            }
        });

        arabic_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocale("ar");
            }
        });


        return view;

    }


    public void setLocale(String lang) {
        switch (lang) {
            case "fa":
                String fa_LanguageCode = "fa";
                LocaleHelper.setLocale(getContext(), fa_LanguageCode);
                break;
            case "ar":
                String ar_LanguageCode = "ar";
                LocaleHelper.setLocale(getContext(), ar_LanguageCode);
                break;
            default:
                String en_LanguageCode = "en";
                LocaleHelper.setLocale(getContext(), en_LanguageCode);
                break;
        }
        getActivity().recreate();
    }

}
