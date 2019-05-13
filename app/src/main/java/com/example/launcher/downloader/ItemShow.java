package com.example.launcher.downloader;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.launcher.downloader.Database.MediaDA;
import com.example.launcher.downloader.Models.Media;


public class ItemShow extends AppCompatActivity {

    ImageButton back_btn;
    public static final String ITEM_SHOW_MESSAGE = "from_item_show";
    ImageView img;
    TextView caption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_show);
        back_btn = findViewById(R.id.back_btn);
        img = findViewById(R.id.img);
        caption = findViewById(R.id.caption);
        Bundle bundle = getIntent().getBundleExtra("info");
        try {
            Media media = (Media) bundle.getSerializable("media");
            caption.setText(media.getCaption());
            back_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Move();
                }
            });
            Fragment fragment;
            if (media.getType().equals("video")) {
                fragment = new Video_fragment();
            } else {
                fragment = new ImageFragment();
            }
            fragment.setArguments(bundle);
            loadFragment(fragment);
        }catch (Exception ignored){}
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.item_show_container,fragment);
        fragmentTransaction.commit();
    }

    public void Move(){
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra(ITEM_SHOW_MESSAGE,ITEM_SHOW_MESSAGE);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Move();
    }
}
