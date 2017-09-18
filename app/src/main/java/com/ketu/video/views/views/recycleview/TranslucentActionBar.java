package com.ketu.video.views.views.recycleview;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ketu.video.R;


/**
 * 支持渐变的 actionBar
 */

public final class TranslucentActionBar extends LinearLayout {

    public View layRoot;
    public View vStatusBar;
    public View layLeft;
    public View layRight;

    public TextView tvTitle;
    public TextView tvRight;
    public View iconLeft;


    public TranslucentActionBar(Context context) {
        this(context, null);
    }

    public TranslucentActionBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TranslucentActionBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {

        setOrientation(HORIZONTAL);
        View contentView = inflate(getContext(), R.layout.actionbar_trans, this);
        vStatusBar = contentView.findViewById(R.id.tv_statusbar);

        layRoot = contentView.findViewById(R.id.lay_transroot);
        iconLeft = contentView.findViewById(R.id.iv_actionbar_left);
        tvTitle = (TextView) contentView.findViewById(R.id.tv_actionbar_title);
        tvRight = (TextView) contentView.findViewById(R.id.tv_actionbar_right);

    }

    /**
     * 设置右按钮文本
     * @param text
     */
    public void setRightBtnText(String text){
        tvRight.setText(text);
    }

    /**
     * 返回右按钮文本
     * @return
     */
    public String getRightBtnText(){
        return tvRight.getText().toString();
    }

    /**
     * 设置背景颜色
     * @param resId
     */
    public void setLayRootBackgroundColor(int resId){

        layRoot.setBackgroundColor(resId);
    }
    /**
     * 设置状态栏高度
     *
     * @param statusBarHeight
     */
    public void setStatusBarHeight(int statusBarHeight) {
        ViewGroup.LayoutParams params = vStatusBar.getLayoutParams();
        params.height = statusBarHeight;
        vStatusBar.setLayoutParams(params);
    }

    /**
     * 设置状态栏颜色
     * @param resId
     */
    public  void setStatusBarBackgroundColor(int resId){

        vStatusBar.setBackgroundColor(resId);
    }
    /**
     * 设置是否需要渐变
     */
    public void setNeedTranslucent() {
        setNeedTranslucent(true, false);
    }

    /**
     * 设置是否需要渐变,并且隐藏标题
     *
     * @param translucent
     */

    public void setNeedTranslucent(boolean translucent, boolean titleInitVisibile) {
        if (translucent) {
            layRoot.setBackground(getResources().getDrawable(R.drawable.translucentbar_background));

        }else {
            layRoot.setBackgroundColor(getResources().getColor(R.color.actionbar_background));
        }
        if (titleInitVisibile) {
            tvTitle.setVisibility(View.GONE);
        }
    }

    /**
     * 设置标题
     *
     * @param strTitle
     */
    public void setTitle(String strTitle) {
        if (!TextUtils.isEmpty(strTitle)) {
            tvTitle.setText(strTitle);
        } else {
            tvTitle.setVisibility(View.GONE);
        }
    }

    /**
     * 设置右按钮不可点击
     * @param enable
     */
    public void setRightBtnEnable(boolean enable){
        layRight.setClickable(enable);

    }


    /**
     * 设置数据
     *
     * @param strTitle
     * @param resIdLeft
     * @param strRight
     * @param listener
     */
    public void setData(String strTitle, int resIdLeft, String strRight,  final  ActionBarClickListener listener) {
        if (!TextUtils.isEmpty(strTitle)) {
            tvTitle.setText(strTitle);
            tvTitle.setVisibility(View.VISIBLE);
        } else {
            tvTitle.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(strRight)) {
            tvRight.setText(strRight);
            tvRight.setVisibility(View.VISIBLE);
        } else {
            tvRight.setVisibility(View.GONE);
        }

        if (resIdLeft == 0) {
            iconLeft.setVisibility(View.GONE);
        } else {
            iconLeft.setBackgroundResource(resIdLeft);
            iconLeft.setVisibility(View.VISIBLE);
        }


        if (listener != null) {
            layLeft = findViewById(R.id.lay_actionbar_left);
            layRight = findViewById(R.id.lay_actionbar_right);
            layLeft.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onLeftClick();
                }
            });
            layRight.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onRightClick();
                }
            });
        }
    }

}
