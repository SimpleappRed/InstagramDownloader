package com.example.launcher.downloader;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import com.example.launcher.downloader.Database.MediaDA;
import com.example.launcher.downloader.Models.Media;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InstaDownloader {

    private static final String TAG = InstaDownloader.class.getSimpleName();

    private Context inContext;
    private ProgressDialog inDialog;
    private String inKey, inInput;
    private Boolean download_caption;
    private ArrayList<Media> media = new ArrayList<>();

    public InstaDownloader(Context inContext) {
        this.inContext = inContext;
    }

    public void setAccessToken(String token) {
        this.inKey = token;
    }

    public void get(String input, Boolean download_caption) {
        inDialog = new ProgressDialog(inContext);
        inDialog.setMessage("Please wait...");
        inDialog.setCancelable(false);
        inDialog.show();
        this.download_caption = download_caption;

        Log.i("access", "without caption," + inKey);
        if (input == null || input.equals("")) {
            return;
        }
        new InstaUrl().execute(input);
    }

    @SuppressLint("StaticFieldLeak")
    private class InstaUrl extends AsyncTask<String, Void, Void> {
        private String response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        String getImageUrl(String content) {
            Log.e("fan", "getImageURL:" + content);
            String regex;
            String imageUrl = "";
            regex = "<meta property=\"og:image\" content=\"(.*?)\"";
            Pattern pa = Pattern.compile(regex, Pattern.MULTILINE);
            Matcher ma = pa.matcher(content);

            if (ma.find()) {
                imageUrl = ma.group(1);
                Log.i("image", "origin_imageUrl=" + imageUrl);
            }
            return imageUrl;
        }

        String getVideoUrl(String content) {
            String regex;
            String videoUrl = null;
            regex = "<meta property=\"og:video\" content=\"(.*?)\" />";
            Pattern pa = Pattern.compile(regex, Pattern.MULTILINE);
            Matcher ma = pa.matcher(content);

            if (ma.find()) {
                videoUrl = ma.group(1);
            }
            return videoUrl;
        }

        @Override
        protected Void doInBackground(String... arg0) {

            try {
                URL uri = new URL(arg0[0]);
                URLConnection ec = uri.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        ec.getInputStream(), "UTF-8"));
                String inputLine;
                StringBuilder a = new StringBuilder();
                while ((inputLine = in.readLine()) != null)
                    a.append(inputLine);
                in.close();
                Media media = new Media();
                String video_url = "";
                String image_url = "";
                media.setType("image");
                if ((video_url = getVideoUrl(a.toString()))!= null){
                    media.setType("video");
                }

                media.setVideo(video_url);
                image_url = getImageUrl(a.toString());
                if (download_caption){
                    media.setCaption(reqId(arg0[0]));
                }
                media.setImage(image_url);
                Download_Store(media);
                inDialog.dismiss();
            } catch (Exception e) {
                inDialog.dismiss();
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            inInput = response;
            Log.i("access", "res:" + response);

        }

        private String reqId(String input) {

            String oId = "";

            String jsonPly = makeServiceCall("https://api.instagram.com/oembed/?url=" + input);
            if (jsonPly != null) {
                try {
                    JSONObject _plyJsonObj = new JSONObject(jsonPly);

                    oId = _plyJsonObj.getString("title");
                    Log.i("access",oId);
                } catch (final JSONException ignored) {

                }
            }
            return oId;
        }

    }

    private void Download_Store(Media sent_media) {
        File root = Environment.getExternalStorageDirectory();
        String folder_main = "instagramDownloader";
        File f = new File(Environment.getExternalStorageDirectory(), folder_main);
        if (!f.exists()) {
            Log.i("access?", "built");
            f.mkdirs();
        }

        DownloadManager downloadmanager = (DownloadManager) inContext.getSystemService(Context.DOWNLOAD_SERVICE);
        String image_path = "";
        String image_link, video_link;
        image_link = sent_media.getImage();
        Random random = new Random();
        Uri uri = Uri.parse(image_link);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setDescription("Downloading");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        image_path = (random.nextInt(99999)) + ".jpg";
        request.setDestinationInExternalPublicDir("/" + folder_main, image_path);
        request.allowScanningByMediaScanner();
        downloadmanager.enqueue(request);
        MediaDA mediaDA = new MediaDA(inContext);
        mediaDA.open();
        Media media1 = new Media();
        media1.setType("image");
        if (sent_media.getType().equals("video")&&
                sent_media.getVideo() != null && !sent_media.getVideo().equals("")) {
            video_link = sent_media.getVideo().trim();
            Uri uri2 = Uri.parse(video_link);
            DownloadManager.Request request2 = new DownloadManager.Request(uri2);
            request2.setDescription("Downloading");
            request2.setTitle("Download media");
            request2.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            String video_path = (random.nextInt(99999)) + ".mp4";
            request2.setDestinationInExternalPublicDir("/"
                    + folder_main, video_path);
            request2.allowScanningByMediaScanner();
            downloadmanager.enqueue(request2);
            media1.setVideo(root.getAbsolutePath() + "/" + folder_main + "/" + video_path);
            media1.setType("video");
        }
        media1.setImage(root.getAbsolutePath() + "/" + folder_main + "/" + image_path);
        media1.setCaption(sent_media.getCaption());
        mediaDA.addMedia(media1);
        Log.i("access?", image_link);
        mediaDA.close();

    }

    private String makeServiceCall(String reqUrl) {
        String response = null;
        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(20000);
            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = convertStreamToString(in);
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        } catch (ProtocolException e) {
            Log.e(TAG, "ProtocolException: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        return response;
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

}
