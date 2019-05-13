package com.example.launcher.downloader;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.launcher.downloader.Database.MediaDA;
import com.example.launcher.downloader.Models.Media;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.Collections;

import it.gmariotti.recyclerview.adapter.AlphaAnimatorAdapter;

public class HistoryFragment extends Fragment {

    MyAdapter myAdapter;
    ArrayList<Media> arrayList = new ArrayList<>();
    MediaDA mediaDA ;

    ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback
            (ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT |
                    ItemTouchHelper.DOWN | ItemTouchHelper.UP, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                              RecyclerView.ViewHolder target) {
            int from = viewHolder.getAdapterPosition();
            int to = target.getAdapterPosition();
            Collections.swap(arrayList, from, to);
            myAdapter.notifyItemMoved(from, to);
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            arrayList.remove(viewHolder.getAdapterPosition());
            myAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
        }
    });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        touchHelper.attachToRecyclerView(recyclerView);
        mediaDA = new MediaDA(getContext());
        mediaDA.open();

        arrayList = mediaDA.getALLMedias();
        Log.i("access", String.valueOf(arrayList.size()));
        myAdapter = new MyAdapter(getContext(), arrayList);
        AlphaAnimatorAdapter animatorAdapter = new AlphaAnimatorAdapter(myAdapter, recyclerView);
        recyclerView.setAdapter(animatorAdapter);
        return view;
    }

}
