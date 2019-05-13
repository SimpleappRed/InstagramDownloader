package com.example.launcher.downloader;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.launcher.downloader.Models.Media;

import java.io.File;

public class Video_fragment extends Fragment {

    int i = 0;
    VideoView videoView;
    Media media;
    Uri video;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_item_video, container, false);
        videoView = view.findViewById(R.id.videoView);
        MediaController mediaController = new
                MediaController(getContext());
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        Bundle bundle = getArguments();
        media = (Media) bundle.getSerializable("media");
        video = Uri.fromFile(new File(media.getVideo()));
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        stop();
    }


    @Override
    public void onResume() {
        super.onResume();
        videoView.setVideoURI(video);
        videoView.start();
        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoView.isPlaying()) {
                    videoView.pause();
                } else {
                    resume();
                }
            }
        });
    }

    public void start() {
        videoView.start();
    }

    public void stop() {
        videoView.stopPlayback();
    }

    public void resume() {
        videoView.resume();
    }
}
