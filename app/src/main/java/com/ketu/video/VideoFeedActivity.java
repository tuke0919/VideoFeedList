package com.ketu.video;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import com.ketu.video.Utils.ApiConstants;
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


public class VideoFeedActivity extends BaseActivity implements ActionBarClickListener{

    public VideoPlayerView videoPlayerView;
    /*列表*/
    public TranslucentRecyclerView recyclerView;
    /*适配器*/
    public RecycleViewAdapter adapter;
    /*数据集*/
    public ArrayList<VideoFeedBean> videoFeedDataList;
    /*布局管理器*/
    public LinearLayoutManager layoutManager;

    //public String videoPath = "http://rmrbtest-image.peopleapp.com/upload/video/201707/1499914158feea8c512f348b4a.mp4";




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

    }

    @Override
    public void onLeftClick() {

    }

    @Override
    public void onRightClick() {

    }
}
