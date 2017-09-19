package com.ketu.video.bean;


/**
 * <p/>
 * 功能 :VideoFeedBean
 *
 * @author ketu 时间 2017/9/12
 * @version 1.0
 */

public class VideoFeedBean extends BaseBean{

    /*id*/
    public String id;
    /*video的链接*/
    public String videoUrl;
    /*缩略图*/
    public int video_thumb_pic;

    /*Video文字描述*/
    public String title;
    public String avatarUrl;
    public String userName;

    /*评论，赞数据*/
    public String commentCounts;
    public String zansCounts;

    /*Video长按*/
    public long videoProgress = 0;
    /*播放时间*/
    public long duration = 0;
}
