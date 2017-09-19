package com.ketu.video.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ketu.video.R;
import com.ketu.video.Utils.ApiConstants;
import com.ketu.video.Utils.FileUtil;
import com.ketu.video.Utils.VideoUtil;
import com.ketu.video.bean.VideoFeedBean;
import com.ketu.video.views.views.roundimageview.CircleImageView;

import java.util.ArrayList;

/**
 * <p/>
 * 功能 :RecycleView适配器
 *
 * @author ketu 时间 2017/9/12
 * @version 1.0
 */

public class RecycleViewAdapter extends RecycleViewBaseAdapter<VideoFeedBean> {



    public RecycleViewAdapter(Context context) {
        super(context);
        dataList = new ArrayList<>();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType){
            case VideoFeedHolder.VIEW_TYPE:
                return new VideoFeedHolder(LayoutInflater.from(context).inflate(R.layout.layout_recycleview_video_item,parent,false));

        }
        return new VideoFeedHolder(LayoutInflater.from(context).inflate(R.layout.layout_recycleview_video_item,parent,false));
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        /*刷新数据*/
        holder.onRefreshView(dataList.get(position),position);
    }

    @Override
    public int getItemViewType(int position) {
        int type = dataList.get(position).type;

        switch (type){
            case ApiConstants.TYPE_VIDEO_FEED:
                return VideoFeedHolder.VIEW_TYPE;
        }

        return VideoFeedHolder.VIEW_TYPE;
    }

    public class VideoFeedHolder extends BaseViewHolder<VideoFeedBean> implements View.OnClickListener{

        private static final int VIEW_TYPE = 1;

        /*蒙层*/
        public View  viewMask;
        public View  viewBottomMask;


        /*第一帧布局*/
        public RelativeLayout firstFrameLayout;
        /*第一帧图像，播放按钮*/
        public ImageView firstFrameImage, btnPlay;


        /*标题内容*/
        public TextView tvTitle;
        /*头像和用户名*/
        public CircleImageView avatar;
        public TextView userName;

        /*评论*/
        public RelativeLayout commentsLayout;
        public TextView tvComments;
        /*分享*/
        public ImageView ivShare;
        /*赞*/
        public RelativeLayout zanLayout;
        public TextView tvZan;


        public VideoFeedHolder(View itemView) {
           super(itemView);

            viewMask=findViewById(R.id.v_video_mask);
            viewBottomMask = findViewById(R.id.v_video_bottom_mask);

            firstFrameLayout = findViewById(R.id.fl_first_frame_layout);
            firstFrameImage = findViewById(R.id.iv_first_frame_image);
            btnPlay = findViewById(R.id.btn_play);

            tvTitle = findViewById(R.id.tv_title);

            avatar = findViewById(R.id.iv_user_avatar);
            userName = findViewById(R.id.tv_user_name);

            commentsLayout = findViewById(R.id.rl_comments_layout);
            tvComments = findViewById(R.id.tv_comments);
            ivShare = findViewById(R.id.iv_share);
            zanLayout = findViewById(R.id.rl_zan_layout);
            tvZan = findViewById(R.id.tv_zan_counts);


            tvComments.setOnClickListener(this);
            btnPlay.setOnClickListener(this);
            btnPlay.setClickable(false);
            btnPlay.setEnabled(false);

        }

       @Override
       public void onRefreshView(VideoFeedBean videoFeedBean, int position) {

           /*刷新数据*/
           Log.e("onRefreshView","----------position--"+position+"--------："+ viewMask.getVisibility());

          /* if (videoFeedBean.video_thumb_pic == null){
               Log.e("frame","video路径："+videoFeedBean.videoUrl);
               Bitmap bitmap = VideoUtil.getFirstVideoFrame(videoFeedBean.videoUrl);
               String thumbPic =FileUtil.getVideoThumbPicPath(context,videoFeedBean.videoUrl);
               Log.e("frame","videothumb路径："+thumbPic);
               VideoUtil.saveBitmapToFile(bitmap,thumbPic);
               videoFeedBean.video_thumb_pic = thumbPic;
           }*/

           Glide.with(context)
                   .load(videoFeedBean.video_thumb_pic)
                   .diskCacheStrategy(DiskCacheStrategy.ALL)
                   .centerCrop()
                   .into(firstFrameImage);


       }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {


            }

        }

    }


    public interface OnHolderVideoFeedListener {
        void onStartPlayVideoNoWifi();

        void onStartPlayVideo();
    }
    private OnHolderVideoFeedListener listener;
    /**
     * 注册监听
     *
     * @param listener
     */
    public void registerVideoPlayerListener(OnHolderVideoFeedListener listener) {
        this.listener = listener;
    }

    /**
     * 解除监听
     */
    public void unRegisterVideoPlayerListener() {
        if (listener != null) {
            listener = null;
        }
    }



}
