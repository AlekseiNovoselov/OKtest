package com.lexaloris.recyclevideoview;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity implements IVideoDownloadListener {

    private static String TAG = "MainActivity";

    private Context context;
    private RecyclerView mRecyclerView;
    private ProgressBar progressBar;
    private VideosAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Video> urls;
    VideosDownloader videosDownloader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = MainActivity.this;
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        urls = new ArrayList<>();
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new VideosAdapter(MainActivity.this, urls);
        mRecyclerView.setAdapter(mAdapter);

        videosDownloader = new VideosDownloader(context);
        videosDownloader.setOnVideoDownloadListener(this);

        // будем считать, что соединение есть всегда
//        if(Utils.hasConnection(context))
//        {
            getVideoUrls();

            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    Log.d(TAG, "onScrolled");
                    super.onScrolled(recyclerView, dx, dy);
                }

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    Log.d(TAG, "onScrollStateChanged");
                    super.onScrollStateChanged(recyclerView, newState);

                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {

                        LinearLayoutManager layoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
                        int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
                        int findFirstCompletelyVisibleItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition();
                        Log.d(TAG, "firstVisiblePosition" + String.valueOf(firstVisiblePosition));
                        Log.d(TAG, "findFirstCompletelyVisibleItemPosition" + String.valueOf(findFirstCompletelyVisibleItemPosition));

                        Video video;
                        if (urls != null && urls.size() > 0)
                        {
                            if (findFirstCompletelyVisibleItemPosition >= 0) {
                                Log.d(TAG, "firstVisiblePosition >= 0");
                                video = urls.get(findFirstCompletelyVisibleItemPosition);
                                mAdapter.videoPlayerController.setcurrentPositionOfItemToPlay(findFirstCompletelyVisibleItemPosition);
                                mAdapter.videoPlayerController.handlePlayBack(video);
                            }
                            else
                            {
                                Log.d(TAG, "firstVisiblePosition < 0");
                                video = urls.get(firstVisiblePosition);
                                mAdapter.videoPlayerController.setcurrentPositionOfItemToPlay(firstVisiblePosition);
                                mAdapter.videoPlayerController.handlePlayBack(video);
                            }
                        }
                    }
                }
            });
        }
//        else
//            Toast.makeText(context, "No internet available", Toast.LENGTH_LONG).show();
//    }

    @Override
    public void onVideoDownloaded(Video video) {
        Log.d(TAG, "onVideoDownloaded");
        mAdapter.videoPlayerController.handlePlayBack(video);
    }

    private void getVideoUrls()
    {
        Log.d(TAG, "getVideoUrls");
        Video video1 = new Video("0", "0", "http://techslides.com/demos/sample-videos/small.mp4");
        urls.add(video1);
        Video video2 = new Video("1", "1", "http://www.quirksmode.org/html5/videos/big_buck_bunny.mp4");
        urls.add(video2);
        Video video3 = new Video("2", "2", "http://sample-videos.com/video/mp4/720/big_buck_bunny_720p_1mb.mp4");
        urls.add(video3);
        Video video4 = new Video("3", "3", "http://dev.exiv2.org/attachments/341/video-2012-07-05-02-29-27.mp4");
        urls.add(video4);
//        Video video5 = new Video("4", "4", "http://techslides.com/demos/sample-videos/small.mp4");
//        urls.add(video5);
//        Video video6 = new Video("5", "5", "http://www.quirksmode.org/html5/videos/big_buck_bunny.mp4");
//        urls.add(video6);
//        Video video7 = new Video("6", "6", "http://sample-videos.com/video/mp4/720/big_buck_bunny_720p_1mb.mp4");
//        urls.add(video7);

        mAdapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
        videosDownloader.startVideosDownloading(urls);
    }

}
