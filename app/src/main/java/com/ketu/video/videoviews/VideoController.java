package com.ketu.video.videoviews;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.AttrRes;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ketu.video.R;
import com.pili.pldroid.player.IMediaController;

import java.text.DecimalFormat;
import java.util.Locale;

/**
 * <p/>
 * 功能 :视频播放控制器
 *
 * @author ketu 时间 2017/9/13
 * @version 1.0
 */
public class VideoController extends FrameLayout implements IMediaController,View.OnClickListener,SeekBar.OnSeekBarChangeListener {

    /*实时更新界面UI进度*/
    public static final int REALTIME_UPDATE_UI_PROGRESS = 1;
    /*更新控制器不可见*/
    public static final int UPDATE_CONTROLLER_INVISIABLE =2;



    /*上下文环境*/
    public Context context;

    /*Bottom*/

    /*播放暂停按钮*/
    public ImageView videoPlayButton;
    public LinearLayout controllerBottomLayout;

    public TextView videoCurrenTime;
    public SeekBar  videoSeekBar;
    public TextView videoTotalTime;


    /*全屏*/
    public ImageView videoFullScreen;


    /*视频实际控制对象,播放器*/
    private MediaPlayerControl mediaPlayer;

    /*公共数据*/
    public long duration;//ms
    public AudioManager audioManager;


    /*是否实时更新Video进度*/
    boolean realTimeUpdateVideo = true;
    boolean isSeekBarDragging ;

    /*实时更新任务*/
    Runnable realTimeUpdateVideoRunnable;


    public VideoController( Context context) {
        this(context,null);
    }

    public VideoController( Context context,  AttributeSet attrs) {
        this(context, attrs,0);

    }

    public VideoController( Context context,  AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;

        initViews();
        initDatas();
        initListeners();
    }

    /**
     * 初始化Views
     */
    public void initViews(){
        View rootView = LayoutInflater.from(context).inflate(R.layout.layout_video_controller,null);

        videoPlayButton = (ImageView) rootView.findViewById(R.id.iv_video_play_btn);

        controllerBottomLayout       = (LinearLayout) rootView.findViewById(R.id.ll_controller_bottom_layout);
        videoCurrenTime = (TextView) rootView.findViewById(R.id.tv_video_current_time);
        videoSeekBar    = (SeekBar) rootView.findViewById(R.id.sb_video_seekbar);
        videoTotalTime  = (TextView) rootView.findViewById(R.id.tv_video_total_time);

        videoFullScreen = (ImageView) rootView.findViewById(R.id.iv_video_full_screen);

        addView(rootView);
    }

    /**
     * 初始化数据
     */
    public void initDatas(){
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        videoSeekBar.setThumbOffset(1);
        videoSeekBar.setMax(1000);

    }

    /**
     * 初始化监听器
     */
    public void initListeners(){
        videoPlayButton.setOnClickListener(this);
        videoSeekBar.setOnSeekBarChangeListener(this);
        videoFullScreen.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.iv_video_play_btn:
               if (mediaPlayer == null){
                   return;
               }

               if (mediaPlayer.isPlaying()){
                   mediaPlayer.pause();
                   handler.removeMessages(REALTIME_UPDATE_UI_PROGRESS);

               }else {
                   mediaPlayer.start();
                   handler.sendEmptyMessageDelayed(REALTIME_UPDATE_UI_PROGRESS,1000);
               }
                /*更新播放按钮的背景*/
                updateVideoPlayBackground();

                break;

            case R.id.iv_video_full_screen:
                if (mediaPlayer ==null){

                    return;
                }

                Activity activity = (Activity)context;
                /*方向切换*/
                int screenOrientation = activity.getRequestedOrientation();
                switch (screenOrientation){
                    case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        break;
                    case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        break;
                }
                /*更新全屏视图*/
                updateFullScreenViews();
                break;
        }

    }


    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            long position;
            switch (msg.what){
                case REALTIME_UPDATE_UI_PROGRESS:
                   /* 关联了seekBar的与当前播放时间的关键点,不停的发送handler实现实时查询*/

                    /*实时更新UI进度条*/
                    position = setRealtimeUpdateUiProgress();

                    if (!isSeekBarDragging ){//&& getVisibility() == View.VISIBLE

                        msg = obtainMessage(REALTIME_UPDATE_UI_PROGRESS);
                        sendMessageDelayed(msg,1000 - (position % 1000));
                        updateVideoPlayBackground();
                    }

                    break;
                case UPDATE_CONTROLLER_INVISIABLE:

                    hide();
                    break;


            }

        }
    };

    /**
     * 切换全屏视图
     * @param
     */
    public void updateFullScreenViews(){
        Activity activity = (Activity)context;

        // 控制方向切换
        int screenOrientation = activity.getRequestedOrientation();
        switch (screenOrientation) {
            case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:

                videoFullScreen.setImageResource(R.drawable.controller_video_shrink_screen_icon);
                break;
            case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:

                videoFullScreen.setImageResource(R.drawable.controller_video_full_screen_icon);
                break;
        }
    }

    /**
     * VideoView的播放按钮使控制器开始
     */
   public void  startPlayVideo(){

       if (mediaPlayer.isPlaying()){
           return;
       }
       mediaPlayer.start();

       updateVideoPlayBackground();


   }


    /**
     *自动继续播放，跳转到具体的位置，1，正真的视频跳转，2，控制器底部条跳转
     * @param position
     */
   public void seekToOnAutoPlay(long position){
        if (mediaPlayer == null){
            return;
        }
       mediaPlayer.seekTo(position);

       float percent = (float) ((double) position / (double) duration);
       DecimalFormat fnum = new DecimalFormat("##0.0");
       float c_percent = 0;
       c_percent = Float.parseFloat(fnum.format(percent));
       videoSeekBar.setProgress((int) (c_percent * 100));

   }


    /**
     * 更新播放按钮的背景
     */
    public  void updateVideoPlayBackground(){

        if (mediaPlayer == null){
            return;
        }
        if (mediaPlayer.isPlaying()){
            videoPlayButton.setImageResource(R.drawable.controller_bottom_pause_icon1);
            listener.onShowViewPlayViewButton(false);
        }else {
            videoPlayButton.setImageResource(R.drawable.controller_bottom_play_icon1);
            /*VideoPlayerView要显示播放按钮*/
            listener.onShowViewPlayViewButton(true);

        }
    }


    /**
     * 设置播放进度给时间显示textView与seekBar
     * @return
     */
    public long setRealtimeUpdateUiProgress(){
        if (mediaPlayer == null||isSeekBarDragging){
            return 0;
        }


        Log.e("updateui","更新进度条，时间");
        long position = mediaPlayer.getCurrentPosition();
        long duration = mediaPlayer.getDuration();
        int percent = mediaPlayer.getBufferPercentage();

        /*更新seekbar*/
        if (videoSeekBar != null){
            if (duration > 0) {
                long pos = 1000L * position / duration;
                videoSeekBar.setProgress((int) pos);
            }
            videoSeekBar.setSecondaryProgress(percent * 10);
        }

        this.duration = duration;

        if (this.duration < position) {
            this.duration = position;
        }
        /*更新总时间*/
        if (videoTotalTime != null) {
            videoTotalTime.setText(getVideoCurrentTime(this.duration));
        }
        /*更新当前时间*/
        if (videoCurrenTime != null) {
            videoCurrenTime.setText(getVideoCurrentTime(position));
        }

        /*更新VideoPlayerView的bottomProgressBar*/
        listener.onVideoPlayButtonClickUpdateBottomProgress(position,duration,percent);

        return position;
    }




    /*seekBar的三个回调*/


    /**
     * 拖动中，用户触发fromUser=true，编程触发fromUser=false
     * @param seekBar
     * @param progress
     * @param fromUser
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
           // 防止调用seekBar.setProgress时回调
           if (!fromUser){
               return;
           }
           long arg = Long.parseLong(String.valueOf(progress));
           final long newposition = (duration *arg)/1000L;

           /*实时更新Video*/
           if (realTimeUpdateVideo){
               handler.removeCallbacks(realTimeUpdateVideoRunnable);
               realTimeUpdateVideoRunnable = new Runnable() {
                   @Override
                   public void run() {
                       mediaPlayer.seekTo(newposition);

                       long position = mediaPlayer.getCurrentPosition();
                       long duration = mediaPlayer.getDuration();
                       int percent = mediaPlayer.getBufferPercentage();

                      /*更新VideoPlayerView的bottomProgressBar*/
                       listener.onVideoPlayButtonClickUpdateBottomProgress(position,duration,percent);

                   }
               };
               handler.postDelayed(realTimeUpdateVideoRunnable, 200);
           }

           /*设置video的Currenttime*/
           String currentTime = getVideoCurrentTime(newposition);
           if (videoCurrenTime != null){
               videoCurrenTime.setText(currentTime);
           }


    }

    /**
     * 开始拖动
     * @param seekBar
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isSeekBarDragging = true;

        show(3600000);

        /*开始拖动取消实时更新进度*/
        handler.removeMessages(REALTIME_UPDATE_UI_PROGRESS);

        if (realTimeUpdateVideo){
            /*实时更新video时，静音*/
            audioManager.setStreamMute(AudioManager.STREAM_MUSIC,true);
        }


    }

    /**
     * 拖动结束
     * @param seekBar
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

        long all =  duration * seekBar.getProgress();
        long currentPosition = all / 1000L;

        isSeekBarDragging = false;

        if (! realTimeUpdateVideo) {
            mediaPlayer.seekTo(currentPosition);
        }
        /*如果是暂停就播放*/
        if(!mediaPlayer.isPlaying()){
            mediaPlayer.start();
        }
        /*滑到最末尾*/
        if (currentPosition >= duration){
            if (listener != null){
                listener.onSeekBarStopTracking(currentPosition);
                handler.removeCallbacks(realTimeUpdateVideoRunnable);
                hide();
            }
        }else {


            /*显示3s*/
            show(3000);

            /*1s后实时更新进度*/
            handler.removeMessages(REALTIME_UPDATE_UI_PROGRESS);
            handler.sendEmptyMessageDelayed(REALTIME_UPDATE_UI_PROGRESS,1000);

        }
        /*取消静音*/
        audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
    }

    /**
     * 根据进度计算Video播放的当前时间
     * @param newposition
     * @return
     */
    public  String  getVideoCurrentTime(long newposition){

        int totalSeconds = (int) (newposition / 1000);
        int seconds = totalSeconds%60;
        int minutes = (totalSeconds/60)%60;
        int hours = totalSeconds /3600;
        if (hours > 0){
            return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds).toString();
        }else {
            return String.format(Locale.US, "%02d:%02d", minutes, seconds).toString();
        }
    }


    /*控制器监听器*/

    public interface OnVideoControllerListener {
        /*seekbar拖动回调*/
        void onSeekBarStopTracking(long currentPosition);

        /*控制器显示和隐藏*/
        void onVideoControllerShow();
        void onVideoControllerHide();

        /*更新videoPlayerView底部进度条*/
        void onVideoPlayButtonClickUpdateBottomProgress(long position, long duration, int percent);


        /*控制器播放暂停更新VideoPlayerView的播放按钮*/
        void onShowViewPlayViewButton(boolean b);

    }
    public OnVideoControllerListener listener;

    public void setOnVideoControllerListener(OnVideoControllerListener listener){
        this.listener = listener;
    }


    /**
     * 设置媒体播放器
     * @param mediaPlayerControl
     */
    @Override
    public void setMediaPlayer(MediaPlayerControl mediaPlayerControl) {
           this.mediaPlayer = mediaPlayerControl;
    }

    /*显示控件, 开启时可以自动调用，消失后点击也调用*/
    @Override
    public void show() {
        show(5000);
        if (listener != null) {
            listener.onVideoControllerShow();
        }

    }

    /*显示控件 超时i后自动隐藏*/
    @Override
    public void show(int i) {
        Log.e("onPrepared","显示控制器 timeout= "+ i);
          if (getVisibility() == View.INVISIBLE){
              setVisibility(View.VISIBLE);
          }
          updateVideoPlayBackground();
         // updateFullScreenViews();
          /*更新进度*/
          handler.sendEmptyMessage(REALTIME_UPDATE_UI_PROGRESS);
          /*延迟消失*/
          if (i != 0) {
             handler.removeMessages(UPDATE_CONTROLLER_INVISIABLE);
             handler.sendMessageDelayed(handler.obtainMessage(UPDATE_CONTROLLER_INVISIABLE), i);
          }

    }

    /*隐藏控件，隐藏时不再实时更新进度*/
    @Override
    public void hide() {
        Log.e("onPrepared","隐藏控制器");
        if (getVisibility() == View.VISIBLE){
            //handler.removeMessages(REALTIME_UPDATE_UI_PROGRESS);
            setVisibility(View.INVISIBLE);
            if (listener != null){
                listener.onVideoControllerHide();
            }
        }

    }

    /*控件是否在显示*/
    @Override
    public boolean isShowing() {
        return (getVisibility()==View.VISIBLE) ? true :false;
    }

    @Override
    public void setAnchorView(View view) {

    }

}
