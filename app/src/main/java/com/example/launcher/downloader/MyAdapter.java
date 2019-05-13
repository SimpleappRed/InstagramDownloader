package com.example.launcher.downloader;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.launcher.downloader.Database.MediaDA;
import com.example.launcher.downloader.Models.Media;

import java.io.File;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.Item>{

    Context context;
    List<Media> mediaList;

    public MyAdapter(Context context, List<Media>list) {
        this.context = context;
        this.mediaList = list;
    }

    @NonNull
    @Override
    public Item onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item,parent,false);
        return new Item(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Item holder, final int position) {
        Media media = mediaList.get(position);
        holder.content.setText(media.getCaption());
        if (this.mediaList.get(position).getImage() != null) {
            File imgFile = new File(this.mediaList.get(position).getImage());
            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                holder.item_image.setImageBitmap(Bitmap.createScaledBitmap(myBitmap
                        , dpToPx(165), dpToPx(165), false));
            }
        }
        holder.delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeItem(position);
            }
        });

    }

    public int dpToPx(int dp) {
        float density = context.getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }
    @Override
    public int getItemCount() {
        return mediaList.size();
    }

    class Item extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageButton  item_image;
        Button delete_btn;
        TextView content;
        public Item(View view) {
            super(view);
            delete_btn = view.findViewById(R.id.delete_btn);
            content = view.findViewById(R.id.item_txt);
            item_image = view.findViewById(R.id.item_image);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if (v.getId() == delete_btn.getId()){
                Log.i("access","clicked:");
                removeItem(getAdapterPosition());
            }else{
                Intent intent = new Intent(context,ItemShow.class);
                Bundle bundle = new Bundle();
                Media media = mediaList.get(getAdapterPosition());
                bundle.putSerializable("media",media);
                intent.putExtra("info",bundle);
                Log.i("access?type",media.getType());
                context.startActivity(intent);
            }
        }

    }

    public void removeItem(int position){
        Media media = mediaList.get(position);
        this.mediaList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,this.mediaList.size());
        String image_path = media.getImage();
        if (media.getType().equals("video")){
            String video_path = media.getVideo();
        }
        MediaDA mediaDA = new MediaDA(context);
        mediaDA.open();
        mediaDA.deleteMedia(media.getId());
        mediaDA.close();
    }
}
