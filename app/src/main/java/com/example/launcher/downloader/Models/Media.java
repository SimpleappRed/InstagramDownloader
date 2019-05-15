package com.example.launcher.downloader.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * @author Amri Muhammad Karimi
 */

public class Media implements Serializable{

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("video")
    @Expose
    private String video;

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getCaption() {

        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    @SerializedName("caption")
    @Expose
    private String caption;

    public String getType() {
        return type;
    }

    public String getImage() {
        return image;
    }

    public String getVideo() {
        return video;
    }

}