package com.lexaloris.recyclevideoview;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public class VideoPlayerController {

    private static String TAG = "VideoPlayerController";

    Context context;
    //FileCache fileCache;
    int currentPositionOfItemToPlay = 0;
    Video currentPlayingVideo;

    private Map<String, VideoPlayer> videos = Collections.synchronizedMap(new WeakHashMap<String, VideoPlayer>());
    private Map<String, ProgressBar> videosSpinner = Collections.synchronizedMap(new WeakHashMap<String, ProgressBar>());

    public VideoPlayerController(Context context) {

        this.context = context;
        //fileCache = new FileCache(context);
    }

    public void loadVideo(Video video, VideoPlayer videoPlayer, ProgressBar progressBar) {

        //Add video to map
        videos.put(video.getIndexPosition(), videoPlayer);
        videosSpinner.put(video.getIndexPosition(), progressBar);

        handlePlayBack(video);
    }

//This method would check two things
//First if video is downloaded or its local path exist
//Second if the videoplayer of this video is currently showing in the list or visible

    public void handlePlayBack(Video video)
    {
        Log.d(TAG, "handlePlayBack");
        //Check if video is available
        if(isVideoDownloaded(video))
        {
            Log.d(TAG, "videoDownloaded");
            // then check if it is currently at a visible or playable position in the listview
            if(isVideoVisible(video))
            {
                Log.d(TAG, "videoVisible");
                //IF yes then playvideo
                playVideo(video);
            }
        }
    }

    private void playVideo(final Video video)
    {
        Log.d(TAG, "playVideo");
        //Before playing it check if this video is already playing

        if(currentPlayingVideo != video)
        {
            //Start playing new url
            if(videos.containsKey(video.getIndexPosition()))
            {
                final VideoPlayer videoPlayer2 = videos.get(video.getIndexPosition());
                //String localPath = fileCache.getFile(video.getUrl()).getAbsolutePath();
                //String localPath = context.getFilesDir() + "lexaloris.mp4";
                String filename = video.getUrl().substring(video.getUrl().lastIndexOf("/")+1);
                String localPath = new File(context.getFilesDir(), filename).getAbsolutePath();
                if(!videoPlayer2.isLoaded)
                {
                    videoPlayer2.loadVideo(localPath, video);
                    videoPlayer2.setOnVideoPreparedListener(new IVideoPreparedListener() {
                        @Override
                        public void onVideoPrepared(Video mVideo) {

                            //Pause current playing video if any
                            if(video.getIndexPosition() == mVideo.getIndexPosition())
                            {
                                if(currentPlayingVideo!=null)
                                {
                                    VideoPlayer videoPlayer1 = videos.get(currentPlayingVideo.getIndexPosition());
                                    videoPlayer1.pausePlay();
                                }
                                videoPlayer2.mp.start();
                                currentPlayingVideo = mVideo;
                            }


                        }
                    });
                }
                else
                {
                    //Pause current playing video if any
                    if(currentPlayingVideo!=null)
                    {
                        VideoPlayer videoPlayer1 = videos.get(currentPlayingVideo.getIndexPosition());
                        videoPlayer1.pausePlay();
                    }

                    boolean isStarted = videoPlayer2.startPlay();
                    {
                        //Log.i(TAG, "Started playing Video Index: " + video.getIndexPosition());
                        //Log.i(TAG, "Started playing Video: " + video.getUrl());
                    }
                    currentPlayingVideo = video;
                }
            }
        }
        else
        {
            //Log.i(TAG, "Already playing Video: " + video.getUrl());
        }

    }

    private boolean isVideoVisible(Video video) {

        Log.d(TAG, "isVideoVisible");
        //To check if the video is visible in the listview or it is currently at a playable position
        //we need the position of this video in listview and current scroll position of the listview
        int positionOfVideo = Integer.valueOf(video.getIndexPosition());
        Log.d(TAG, "positionOfVideo" + String.valueOf(positionOfVideo));
        Log.d(TAG, "currentPositionOfItemToPlay" + String.valueOf(currentPositionOfItemToPlay));

        if(currentPositionOfItemToPlay == positionOfVideo)
            return true;

        return false;
    }

    private boolean isVideoDownloaded(Video video) {

        Utils utils = new Utils();
        String isVideoDownloaded = utils.readPreferences(context, video.getUrl(), "false");
        boolean isVideoAvailable = Boolean.valueOf(isVideoDownloaded);
        if(isVideoAvailable)
        {
            //If video is downloaded then hide its progress
            hideProgressSpinner(video);
            return true;
        }

        showProgressSpinner(video);
        return false;
    }

    private void showProgressSpinner(Video video) {
        ProgressBar progressBar = videosSpinner.get(video.getIndexPosition());
        if(progressBar!=null)
            progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressSpinner(Video video) {

        ProgressBar progressBar = videosSpinner.get(video.getIndexPosition());
        if(progressBar!=null && progressBar.isShown())
        {
            progressBar.setVisibility(View.GONE);
            Log.i(TAG, "ProgressSpinner Hided Index: " + video.getIndexPosition());
        }
    }

    public void setcurrentPositionOfItemToPlay(int mCurrentPositionOfItemToPlay) {
        Log.d(TAG, "setcurrentPositionOfItemToPlay" + String.valueOf(mCurrentPositionOfItemToPlay));
        currentPositionOfItemToPlay = mCurrentPositionOfItemToPlay;
    }
}
