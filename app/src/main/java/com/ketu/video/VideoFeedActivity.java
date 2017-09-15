package com.ketu.video;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.ketu.video.views.VideoController;
import com.ketu.video.views.VideoPlayerView;

public class VideoFeedActivity extends AppCompatActivity {

    public FrameLayout videoFrameLaout;
    public VideoPlayerView videoPlayerView;

    public String videoPath = "http://rmrbtest-image.peopleapp.com/upload/video/201707/1499914158feea8c512f348b4a.mp4";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_feed);

        initViews();
        initDatas();

    }
    public void initViews(){
        videoFrameLaout = (FrameLayout) findViewById(R.id.fl_video_player);

    }
    public void initDatas(){
        videoPlayerView = new VideoPlayerView(this);

        videoFrameLaout.addView(videoPlayerView);
        videoPlayerView.startPlayVideo(videoPath);

    }
}
