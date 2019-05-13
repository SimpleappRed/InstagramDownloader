package com.example.launcher.downloader.Models;



/**
 * @author Ican Bachors
 * @version 1.1
 * Source: https://github.com/bachors/Insta-Downloader
 */

public interface InstaListener {
    void onResponse(Instagram instagram);
    void onFailure(String message);
}