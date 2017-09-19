package com.ketu.video;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;

import com.ketu.video.Utils.ApiConstants;
import com.ketu.video.Utils.ScreenUtils;
import com.ketu.video.adapter.RecycleViewAdapter;
import com.ketu.video.bean.VideoFeedBean;
import com.ketu.video.Utils.VideoUtil;
import com.ketu.video.videoviews.VideoPlayerView;
import com.ketu.video.views.views.recycleview.ActionBarClickListener;
import com.ketu.video.views.views.recycleview.BaseActivity;
import com.ketu.video.views.views.recycleview.TranslucentRecyclerView;

import java.util.ArrayList;


/**
 * <p/>
 * 功能 :视频列表页
 *
 * @author ketu 时间 2017/9/18
 * @version 1.0
 */


public class VideoFeedActivity extends BaseActivity implements ActionBarClickListener {

    /*全局的视频播放器*/
    public VideoPlayerView videoPlayerView;


    /*列表*/
    public TranslucentRecyclerView recyclerView;
    /*适配器*/
    public RecycleViewAdapter adapter;
    /*数据集*/
    public ArrayList<VideoFeedBean> videoFeedDataList;
    /*布局管理器*/
    public LinearLayoutManager layoutManager;

    /*item亮起的阈值位置*/
    public int threshold;
    /*滑动状态*/
    public boolean scrollState = false;


    /*上一个亮起的item*/
    public RecyclerView.ViewHolder previousHolder;
    /*当前亮起的item*/
    public RecyclerView.ViewHolder currentHolder;
    /*当前亮起的position*/
    public int currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public int getContentLayoutResources() {
        return R.layout.activity_video_feed;
    }

    public void initViews(){

        recyclerView = findSpecificViewById(R.id.recyleview);

    }

    @Override
    protected void initData() {
        /*设置标题栏透明*/
        setNeedTranslucent(true,true);
        /*设置标题栏数据*/
        setTitleData("更多视频",R.drawable.actionbar_back_arrow_white,"",this);

        adapter = new RecycleViewAdapter(this);
        initDataList();
        adapter.setDataList(videoFeedDataList);
        layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        /*屏幕高的1/3*/
        threshold = ScreenUtils.getScreenHeight(this)/3;
        /*初始化全局播放器*/
        videoPlayerView = new VideoPlayerView(this);

    }

    /**
     *
     * 初始化数据
     */
    public void initDataList(){
        videoFeedDataList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            VideoFeedBean videoFeedItem = new VideoFeedBean();
            videoFeedItem.type = ApiConstants.TYPE_VIDEO_FEED;
            videoFeedItem.videoUrl = VideoUtil.videoUrls[i];
            videoFeedItem.video_thumb_pic = VideoUtil.videoThumbPics[i];
            videoFeedDataList.add(videoFeedItem);
        }

    }

    @Override
    protected void initListeners() {
      /*设置滑动监听器*/
      recyclerView.setOnScrollListener(new RecyclerViewScrollListener());


    }

    @Override
    public void onLeftClick() {

    }

    @Override
    public void onRightClick() {

    }


    /**
     * RecyclerView滑动监听器
     */
    public class RecyclerViewScrollListener extends OnScrollListener{

        public RecyclerViewScrollListener() {

        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {


            switch (newState) {
                case RecyclerView.SCROLL_STATE_IDLE://停止滑动
                    Log.e("scroll","----onScrollStateChanged-----SCROLL_STATE_IDLE");

                    scrollState = false;
                    //滑动停止和松开手指时，调用此方法 进行播放
                    addVideoPlayerAndPlayVideo();
                    break;
                case RecyclerView.SCROLL_STATE_DRAGGING://用户用手指滚动

                    Log.e("scroll","----onScrollStateChanged-----SCROLL_STATE_DRAGGING");
                    scrollState = true;
                    break;
                case RecyclerView.SCROLL_STATE_SETTLING://自动滚动

                    Log.e("scroll","----onScrollStateChanged-----SCROLL_STATE_SETTLING");
                    scrollState = true;
                    break;
            }

        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            Log.e("scroll","--------------onScrolled--------------");


            if (layoutManager instanceof LinearLayoutManager) {

                LinearLayoutManager linearManager =  layoutManager;
                //获取最后一个可见view的位置
                int lastItemPosition = linearManager.findLastVisibleItemPosition();
                //获取第一个可见view的位置
                int firstItemPosition = linearManager.findFirstVisibleItemPosition();
                //获取可见view的总数
                int visibleItemCount = linearManager.getChildCount();
                if (scrollState){

                    videoFeedLight(recyclerView, firstItemPosition, lastItemPosition, visibleItemCount);

                }else {
                    videoFeedLight(recyclerView, firstItemPosition, lastItemPosition, visibleItemCount);
                    addVideoPlayerAndPlayVideo();

                }


            }
        }
    }


    /**
     * 动态添加播放器，并播放视频
     */
    public void addVideoPlayerAndPlayVideo(){

          if (currentHolder == null || videoPlayerView ==null){
               return;
          }
          if (currentHolder instanceof RecycleViewAdapter.VideoFeedHolder){
              RecycleViewAdapter.VideoFeedHolder currentVideoHolder = (RecycleViewAdapter.VideoFeedHolder) currentHolder;
              FrameLayout videoPlayerContainer = currentVideoHolder.videoPlayerContainer;
              videoPlayerContainer.removeAllViews();

              /*上一个Videoview的父容器*/
              ViewGroup previousViewGroup = (ViewGroup) videoPlayerView.getParent();
              if (previousViewGroup != null && previousViewGroup.getChildCount() > 0){
                  previousViewGroup.removeAllViews();
              }

              /*添加播放器*/
              videoPlayerContainer.addView(videoPlayerView);

              /*第一帧缩略图隐藏*/
              currentVideoHolder.firstFrameLayout.setVisibility(View.GONE);

              // 获取播放进度
              VideoFeedBean videoFeedBean = videoFeedDataList.get(currentPosition);
              long videoProgress = videoFeedBean.videoProgress;
              long duration = videoFeedBean.duration;

              if (videoProgress != 0 && videoProgress != duration) { // 跳转到之前的进度，继续播放

                  videoPlayerView.startPlayVideo(videoFeedBean.videoUrl);
                  videoPlayerView.seekToPositionOnAutoPlay(videoProgress);

              } else {//从头播放
                  videoPlayerView.startPlayVideo(videoFeedBean.videoUrl);
              }


          }


    }

    /**
     *
     *使符合条件的item亮起
     * @param recyclerView
     * @param firstItemPosition
     * @param lastItemPosition
     * @param visibleItemCount
     */
    public void videoFeedLight(RecyclerView recyclerView, int firstItemPosition, int lastItemPosition, int visibleItemCount){

        if (recyclerView == null){
            return;
        }
        Rect lightVideoFeedRect = new Rect();

        for (int i = 0 ; i< visibleItemCount ;i++){
            View childView = recyclerView.getChildAt(i);
            RecyclerView.ViewHolder holder = recyclerView.getChildViewHolder(childView);
            childView.getGlobalVisibleRect(lightVideoFeedRect);

            if (lightVideoFeedRect.top < threshold && threshold < lightVideoFeedRect.bottom){

                Log.e("videoFeedLight","当前亮起的Item： "+ (i+firstItemPosition));
                currentPosition = i+firstItemPosition;
                currentHolder =  holder;
                break;
            }
        }

        if (currentHolder == null){
            return;
        }
        /*蒙版显示*/
        if (previousHolder != null){
            if (previousHolder != currentHolder && previousHolder instanceof RecycleViewAdapter.VideoFeedHolder){
                showHolderMask(((RecycleViewAdapter.VideoFeedHolder)previousHolder).viewMask);
            }
        }

        /*蒙版消失*/
        if (((RecycleViewAdapter.VideoFeedHolder)currentHolder).viewMask.getVisibility() == View.VISIBLE){

            hideHolderMask(((RecycleViewAdapter.VideoFeedHolder)currentHolder).viewMask);
            previousHolder = currentHolder;
        }

    }


    /**
     * 显示蒙版
     * @param view
     */
    public void showHolderMask(final View view){

        if (view == null){
            return;
        }
        if (view.getAnimation() !=null){
            Log.e("alphaAnimation","显示动画");
            view.getAnimation().cancel();
            view.setAnimation(null);
        }

        AlphaAnimation alphaAnimation =new AlphaAnimation(0.0f,1.0f);
        alphaAnimation.setDuration(100);
        alphaAnimation.setFillAfter(false);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Log.e("alphaAnimation","显示动画Visibility" );
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        /**/
        view.startAnimation(alphaAnimation);

    }

    /**
     * 隐藏蒙版
     * @param view
     */
    public void hideHolderMask(final View view){

        if (view == null){
            return;
        }
        if (view.getAnimation() !=null){
            Log.e("alphaAnimation","隐藏动画");

            view.getAnimation().cancel();
            view.setAnimation(null);
        }
        AlphaAnimation alphaAnimation =new AlphaAnimation(1.0f,0.0f);
        alphaAnimation.setDuration(100);
        alphaAnimation.setFillAfter(false);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.e("alphaAnimation","隐藏动画Gone");
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(alphaAnimation);

    }


}
