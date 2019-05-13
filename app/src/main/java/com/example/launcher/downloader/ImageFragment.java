package com.example.launcher.downloader;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.launcher.downloader.Models.Media;
import com.squareup.picasso.Picasso;

import java.io.File;

public class ImageFragment extends Fragment {

    ImageView image;
    Bitmap bitmap;
    Media media;
    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_image, container, false);
        image = view.findViewById(R.id.img);
        Bundle bundle = getArguments();
        media = (Media) bundle.getSerializable("media");

        image.setImageBitmap(bitmap);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (media.getImage() != null) {
                    File file = new File(media.getImage());
                    Intent intent = new Intent(Intent.ACTION_VIEW)//
                            .setDataAndType(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ?
                                            android.support.v4.content.FileProvider.getUriForFile
                                                    (getContext(), getContext().getPackageName()
                                                            + ".provider", file) : Uri.fromFile(file),
                                    "image/*").addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (media.getImage() != null) {
            File imgFile = new File(media.getImage());
            Log.i("access?file", "inja!!");
            if (imgFile.exists()) {
//                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//                image.setImageBitmap(Bitmap.createScaledBitmap(myBitmap
//                        , dpToPx(375), dpToPx(215), false));
//                image.setImageURI(Uri.fromFile(imgFile));
                Picasso.with(getContext()).load(imgFile).resize(dpToPx(375), dpToPx(259)).into(image);
                Log.i("access?provide", imgFile.getAbsolutePath());
            }
        }

    }


    public int dpToPx(int dp) {
        float density = getContext().getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }
}
