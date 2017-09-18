package com.ketu.video.views.views.recycleview;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.ketu.video.R;
import com.ketu.video.Utils.ScreenUtils;


/**
 * <p/>
 * 功能 :滑动，变换标题栏透明度
 *
 * @author ketu 时间 2017/7/31
 * @version 1.0
 */

public class TranslucentRecyclerView extends RecyclerView {

    private static String TAG=TranslucentRecyclerView.class.getSimpleName();

    /*设置渐变视图*/
    public View transView;
    /*渐变开始的位置*/
    public int transStartY=50;
    /*渐变结束的位置*/
    public int transEndY=300;
    /*渐变颜色*/
    public int transColor= Color.WHITE;


    public TranslucentRecyclerView(Context context) {
        super(context);
    }

    public TranslucentRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TranslucentRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /*透明状态改变监听器*/
    public interface TranslucentChangedListener {
         void onTranslucentChanged(int alpha);
    }

    public TranslucentRecyclerView.TranslucentChangedListener listener;

    public void setTranslucentChangedListener(TranslucentRecyclerView.TranslucentChangedListener listener){
        this.listener=listener;
    }


    /**
     * 设置渐变View
     * @param transView
     * @param transColor
     * @param transStartY
     * @param transEndY
     */
    public void setTransView(View transView, int transColor, int transStartY, int transEndY){
        this.transView=transView;
        this.transColor=transColor;
        this.transStartY=transStartY;
        this.transEndY=transEndY;

        //初始视图-透明,,0是透明，255不透明
        this.transView.setBackgroundColor(ColorUtils.setAlphaComponent(transColor, 0));
        if (transStartY > transEndY) {
            throw new IllegalArgumentException("transStartY 不得大于 transEndY .. ");
        }
    }


    /**
     * 设置渐变View
     * @param transView
     */
    public void setTransView(View transView){

        setTransView(transView,getResources().getColor(R.color.actionbar_background), ScreenUtils.dp2px(getContext(),transStartY),ScreenUtils.dp2px(getContext(),transEndY));
    }
    /**
     * 根据滑动的距离计算alpha
     * @return
     */
    public int getTransAlpha(){

        int totalScrollY=getScrollYDistance();

        if (totalScrollY>0 && totalScrollY<transEndY){
            return (int) ((totalScrollY - transStartY)* 255 / (transEndY - transStartY) );
        }else if (totalScrollY>=transEndY){
            return 255;
        }
        return 0;

    }

    public View getTransView() {
        return transView;
    }

    public int getTransColor() {
        return transColor;
    }

    public void setTransColor(int transColor) {
        this.transColor = transColor;
    }
     @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        int alpha=getTransAlpha();

        /* 设置渐变view背景色*/
        if (transView!=null){
            transView.setBackgroundColor(ColorUtils.setAlphaComponent(transColor,alpha));
           /* 调用监听接口*/
            listener.onTranslucentChanged(alpha);
        }
    }

    /**
     * 获得第一项最大距离
     * @return 获得向上滑动的距离
     */
    public int getScrollYDistance(){
        LinearLayoutManager manager= (LinearLayoutManager) this.getLayoutManager();

        int position=manager.findFirstVisibleItemPosition();

        if(position!=0){
            return transEndY;
        }

        View firstVisiableChildView=manager.findViewByPosition(position);

        int itemHeight = firstVisiableChildView.getHeight();

        return 0-firstVisiableChildView.getTop();
    }

}
