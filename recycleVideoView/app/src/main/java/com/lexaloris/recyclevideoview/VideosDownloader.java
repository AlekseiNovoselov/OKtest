package com.lexaloris.recyclevideoview;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class VideosDownloader {

    private static String TAG = "VideosDownloader";

    Context context;
    //FileCache fileCache;
    IVideoDownloadListener iVideoDownloadListener;

    public VideosDownloader(Context context) {
        this.context = context;
        //fileCache = new FileCache(context);
    }

/////////////////////////////////////////////////////////////////
// Start downloading all videos from given urls

    public void startVideosDownloading(final ArrayList<Video> videosList)
    {
        Log.d(TAG, "startVideosDownloading");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run()
            {
                //
                Utils utils = new Utils();
                for(int i=0; i<videosList.size(); i++)
                {
                    final Video video = videosList.get(i);
                    String id = video.getId();
                    String url = video.getUrl();
                    Log.d(TAG, "video id " + id);
                    Log.d(TAG, "video url " + url);

                    String isVideoDownloaded = utils.readPreferences(context, video.getUrl(), "false");
                    Log.d(TAG, "isVideoDownloaded " + isVideoDownloaded);
                    boolean isVideoAvailable = Boolean.valueOf(isVideoDownloaded);
                    if(!isVideoAvailable)
                    {
                        //Download video from url
                        Log.d(TAG, "download video from url");
                        String downloadedPath = downloadVideo(url);
                        Log.i(TAG, "Vides downloaded at: " + downloadedPath);
                        Activity activity = (Activity) context;
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Utils utils = new Utils();
                                utils.savePreferences(context, video.getUrl(), "true");
                                iVideoDownloadListener.onVideoDownloaded(video);
                            }
                        });
                    }

                }
            }
        });
        thread.start();
    }

/////////////////////////////////////////////////////////////////

    private String downloadVideo(String urlStr)
    {
        URL url = null;
        File file = null;
        try
        {
            String filename = urlStr.substring(urlStr.lastIndexOf("/")+1);
            file = new File(context.getFilesDir(), filename);
            //file = fileCache.getFile(urlStr);
            url = new URL(urlStr);
            long startTime = System.currentTimeMillis();
            URLConnection ucon = null;
            ucon = url.openConnection();
            InputStream is = ucon.getInputStream();
            BufferedInputStream inStream = new BufferedInputStream(is, 1024 * 5);
            FileOutputStream outStream = new FileOutputStream(file);
            byte[] buff = new byte[5 * 1024];

            //Read bytes (and store them) until there is nothing more to read(-1)
            int len;
            while ((len = inStream.read(buff)) != -1) {
                outStream.write(buff, 0, len);
            }
            long finishTime = System.currentTimeMillis();
            Log.d(TAG, "download time: " + String.valueOf(finishTime - startTime));

            //clean up
            outStream.flush();
            outStream.close();
            inStream.close();

        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }


    public void setOnVideoDownloadListener(IVideoDownloadListener iVideoDownloadListener) {
        this.iVideoDownloadListener = iVideoDownloadListener;
    }
}
