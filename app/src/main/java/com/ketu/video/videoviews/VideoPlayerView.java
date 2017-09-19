package com.ketu.video.videoviews;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ketu.video.R;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoTextureView;
import com.pili.pldroid.player.widget.PLVideoView;

/**
 * <p/>
 * 功能 :播放器View
 *
 * @author ketu 时间 2017/9/14
 * @version 1.0
 */

public class VideoPlayerView extends FrameLayout implements
        PLMediaPlayer.OnPreparedListener,
        PLMediaPlayer.OnInfoListener,
        PLMediaPlayer.OnCompletionListener,
        PLMediaPlayer.OnErrorListener,
        VideoController.OnVideoControllerListener ,
        View.OnClickListener{


    /*上下文环境*/
    Context context;
    /*Views*/
    public TextView videoErrorText;
    public LinearLayout videoErrorLayout;
    public LinearLayout videoLoading;
    public LinearLayout videoPlayButtonLayout;
    public FrameLayout videoPlayerLayout;
    public ProgressBar videoBottomProgressBar;

    /*视频控制器*/
    public VideoController videoController;
    /*视频播放器*/
    public PLVideoTextureView videoPlayer;

    /*是否已经启动过Videoplayer*/
    public boolean hasStartedVideoPlayer = false;
    /*播放路径*/
    public String videoPath;


    public VideoPlayerView( Context context) {
        this(context,null);


    }

    public VideoPlayerView( Context context,  AttributeSet attrs) {
        this(context, attrs,0);
    }

    public VideoPlayerView( Context context,  AttributeSet attrs,  int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initViews();
        initDatas();

        setListeners();

    }

    public void initViews(){

        View view=View.inflate(context, R.layout.layout_video_view, this);
        videoPlayerLayout = (FrameLayout) view.findViewById(R.id.fl_video_player_container);
        videoLoading = (LinearLayout) view.findViewById(R.id.ll_video_loading);
        videoBottomProgressBar = (ProgressBar) view.findViewById(R.id.pb_video_bottom_progress);
        //videoPlayButtonLayout = (LinearLayout) view.findViewById(R.id.ll_video_view_play_layout);
        videoErrorLayout = (LinearLayout) view.findViewById(R.id.ll_video_error_layout);
        videoErrorText = (TextView) view.findViewById(R.id.tv_video_error_text);
    }

    /**
     * 初始化数据
     */
    public void initDatas(){
        videoBottomProgressBar.setVisibility(GONE);
        videoBottomProgressBar.setMax(1000);
        /*计算播放按钮*/
        getVideoPlayButtonLayout();

        /*初始化播放器*/
        initVideoPlayer();

    }

    /**
     * 初始化播放器和控制器
     */
    public void initVideoPlayer(){

        videoPlayer = new PLVideoTextureView(context);
        videoController = new VideoController(context);

        videoPlayer.setBackgroundColor(Color.BLACK);
        AVOptions options = new AVOptions();
        // 默认的缓存大小2000
        options.setInteger(AVOptions.KEY_CACHE_BUFFER_DURATION, 2000);
        // 最大的缓存大小4000
        options.setInteger(AVOptions.KEY_MAX_CACHE_BUFFER_DURATION, 4000);
        // 不启用自动播放
        options.setInteger(AVOptions.KEY_FAST_OPEN, 0);
        videoPlayer.setAVOptions(options);


        /*设置播放器*/
        setVideoPlayer();
        /*添加播放器和控制器*/
        addVideoPlayerAndController();

    }

    /**
     * 设置播放器
     */
    public void setVideoPlayer() {
        /*更新的加载条*/
        videoPlayer.setBufferingIndicator(videoLoading);
        /*绑定控制器*/
        videoPlayer.setMediaController(videoController);
        videoPlayer.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_PAVED_PARENT);
        /*设置监听器*/
        videoPlayer.setOnPreparedListener(this);
        videoPlayer.setOnInfoListener(this);
        videoPlayer.setOnCompletionListener(this);
        videoPlayer.setOnErrorListener(this);
        videoPlayer.setLooping(false);
    }

    /**
     * 添加播放器和控制器
     */
    public void addVideoPlayerAndController(){
        if (videoPlayer.getParent() != null) {
            ((ViewGroup) (videoPlayer.getParent())).removeView(videoPlayer);
        }
        /*添加真正的播放器*/
        videoPlayerLayout.addView(videoPlayer);
        /*添加播放按钮*/
        videoPlayerLayout.addView(videoPlayButtonLayout);
        /*添加控制器*/
        videoPlayerLayout.addView(videoController,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));


        videoPlayerLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                videoPlayerLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                Log.e("videoController","显示器宽:"+videoPlayer.getWidth()+ "显示器宽:"+videoPlayer.getHeight());
                Log.e("videoController","控制器宽:"+videoController.getWidth()+ "控制器高:"+videoController.getHeight());
            }
        });


    }

    /**
     * 计算播放按钮的layout
     * @return
     */
    public LinearLayout getVideoPlayButtonLayout(){
        videoPlayButtonLayout = new LinearLayout(context);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        videoPlayButtonLayout.setLayoutParams(lp);

        videoPlayButtonLayout.setGravity(Gravity.CENTER);
        videoPlayButtonLayout.setOrientation(LinearLayout.VERTICAL);
        videoPlayButtonLayout.setVisibility(INVISIBLE);

        ImageView videoPlayButton = new ImageView(context);
        ViewGroup.LayoutParams lp1 = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        videoPlayButton.setLayoutParams(lp1);
        videoPlayButton.setImageResource(R.drawable.video_view_button_play_icon);
        videoPlayButton.setScaleType(ImageView.ScaleType.CENTER_CROP);

        videoPlayButtonLayout.addView(videoPlayButton);


        return videoPlayButtonLayout;

    }

    public void setListeners(){
        /*控制器的监听器*/
        videoController.setOnVideoControllerListener(this);

        videoPlayButtonLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (videoController.getVisibility() == INVISIBLE){
                    videoController.show();
                }else {
                    videoController.startPlayVideo();
                }
            }
        });

    }

    /**
     * 开始播放Video
     */
    public void startPlayVideo(String path){
         if (videoPlayer == null){
             return;
         }
         /*如果启动已经启动过。不在释放资源，设置路径*/
        if (!hasStartedVideoPlayer) {
            videoPlayer.stopPlayback();
            this.videoPath = path;
            videoPlayer.setVideoPath(videoPath);
        }
         /*底部条消失*/
        if (videoBottomProgressBar != null) {
            videoBottomProgressBar.setVisibility(GONE);
        }

       /* if (!isPlayingOrPause()) { // 暂停之后再播放，走这一段代码
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    videoController.show(5000);
                }
            }, 300);
        }*/

        try {
            Log.e("onPrepared","开始播放");
            videoPlayer.start();

         } catch (Exception e) { //若遇到异常，重新开始播放

            videoPlayer.stopPlayback();
            videoPlayer.setVideoPath(videoPath);
            videoPlayer.start();
         }
    }

    /**
     * 自动播放，跳转到具体的位置，1，正真的视频跳转，2，控制器底部条跳转
     * @param position
     */
    public void seekToPositionOnAutoPlay(long position){

        videoController.seekToOnAutoPlay(position);
    }



    /**
     * 手动暂停
     */
   public void onManualPause(){

       if (videoPlayer != null && hasStartedVideoPlayer) {

           videoPlayer.pause();
           videoPlayer.setVolume(0f, 0f);
       }
   }

    /**
     * true 正在播放，false 暂停
     * @return
     */
    public boolean isPlayingOrPause(){

        return videoPlayer.isPlaying();

    }

    /**
     * 准备完毕回调
     * @param plMediaPlayer
     * @param i
     */
    @Override
    public void onPrepared(PLMediaPlayer plMediaPlayer, int i) {

        Log.e("onPrepared", "onPrepared: 准备完毕播放");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                videoController.show();
            }
        }, 500);

        //videoController.setVisibility(View.VISIBLE);
    }


    /**
     * 播放状态发生变化回调
     * @param plMediaPlayer
     * @param i
     * @param i1
     * @return
     */
    @Override
    public boolean onInfo(PLMediaPlayer plMediaPlayer, int i, int i1) {
       String msg ="";
        switch (i) {
            case 701:
                // 开始缓冲
                msg = "开始缓冲";
                videoLoading.setVisibility(VISIBLE);
                break;
            case 702:
                // 停止缓冲
                msg = "停止缓冲";
                videoLoading.setVisibility(GONE);
                break;
            case 3://第一帧画像已经播放
                if (videoLoading.getVisibility() == VISIBLE) {
                    videoLoading.setVisibility(GONE);
                }
                break;
        }
        Log.e("onPrepared", "onInfo: "+ msg);
        return false;
    }

    /**
     * 播放完成回调
     * @param plMediaPlayer
     */
    @Override
    public void onCompletion(PLMediaPlayer plMediaPlayer) {

        if (videoBottomProgressBar != null) {
            videoBottomProgressBar.setVisibility(GONE);
        }
        Log.e("onPrepared", "onCompletion: 播放完成");
        videoPlayer.stopPlayback();

        hasStartedVideoPlayer = false;

        videoErrorLayout.setVisibility(VISIBLE);
        videoErrorText.setText("重播");
    }

    /**
     * 播放错误回调
     * @param plMediaPlayer
     * @param i
     * @return
     */
    @Override
    public boolean onError(PLMediaPlayer plMediaPlayer, int i) {
        String msg;
        switch (i) {
            case -5: msg = "网络异常";       break;
            case -11:msg = "与服务器连接断开";break;
            case -110: msg = "连接超时";     break;
            default: msg = "播放状态异常";    break;
        }

       if (msg.equals("播放状态异常")){
           videoPlayer.stopPlayback();
           videoPlayer.setVideoPath(videoPath);
           videoPlayer.start();
       }else {
           videoErrorLayout.setVisibility(VISIBLE);
           videoErrorText.setText(msg);
       }


        return true;
    }

    @Override
    public void onSeekBarStopTracking(long currentPosition) {

    }

    /**
     * 控制器显示后隐藏底部进度条
     */
    @Override
    public void onVideoControllerShow() {

        if (videoController == null) {
            return;
        }
        if (videoBottomProgressBar != null) {
            videoBottomProgressBar.setVisibility(GONE);
        }
    }

    /**
     * 控制器隐藏后显示底部进度条
     *
     */
    @Override
    public void onVideoControllerHide() {

        if (videoController == null) {
            return;
        }
        if (videoBottomProgressBar != null) {
            videoBottomProgressBar.setVisibility(VISIBLE);
        }

    }

    /**
     * 更新底部进度条
     * @param position  播放当前位置
     * @param duration  总的视频长度，实际是字节长度
     * @param percent   预加载百分比
     */
    @Override
    public void onVideoPlayButtonClickUpdateBottomProgress(long position, long duration, int percent) {
        if (videoController == null) {
            return;
        }
        if (videoBottomProgressBar != null){
            if (duration > 0) {
                long pos = 1000L * position / duration;
                videoBottomProgressBar.setProgress((int) pos);
            }
            videoBottomProgressBar.setSecondaryProgress(percent * 10);
        }

    }

    /**
     * 控制器的播放按钮，显示view的播放按钮
     * @param b
     */
    @Override
    public void onShowViewPlayViewButton(boolean b) {
        if(b) {
            videoPlayButtonLayout.setVisibility(View.VISIBLE);
        }else {
            videoPlayButtonLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.ll_video_error_layout:break;
        }
    }
}
