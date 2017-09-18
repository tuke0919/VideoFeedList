package com.ketu.video.views.views.recycleview;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.ketu.video.R;


/**
 * 功能：自定义标题栏的基类
 */

public abstract class BaseActivity extends Activity {
    public String TAG = "BaseActivity";

    /*自定义actionbar*/
    public  TranslucentActionBar translucentActionBar;


    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置状态栏透明,不透明(4.4以下)，透明(4.4-5.0)，半透明(5.0以上)):
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        setContentView(getContentLayoutResources());
        translucentActionBar=  findSpecificViewById(R.id.actionbar);
        /*设置状态栏高度*/
        setStatusBarHeight();


        initViews();
        initData();
        initListeners();
    }
    public abstract int getContentLayoutResources();

    public final <T> T findSpecificViewById(int resId){
        return (T)this.findViewById(resId);
    }
    protected abstract void initViews();
    protected abstract void initData();
    protected abstract void initListeners();

    /**
     * 设置标题栏高度
     */
    public void setStatusBarHeight(){
        translucentActionBar.setStatusBarHeight(getStatusBarHeight(this));
    }

    /**
     *设置是否需要渐变,是否隐藏标题
     * @param translucent
     * @param titleInitVisibile
     */
    public void setNeedTranslucent(boolean translucent, boolean titleInitVisibile ){
        translucentActionBar.setNeedTranslucent(translucent,titleInitVisibile);
    }

    /**
     * 设置标题数据
     * @param title  标题
     * @param leftIcon  左图标
     * @param rightText 右文字
     * @param listener  监听器
     */
    public void setTitleData(String title, int leftIcon, String rightText, ActionBarClickListener listener){
        translucentActionBar.setData(title,leftIcon,rightText,listener);

    }
    /**
     * 获取状态栏高度
     *
     * @return
     */
    public int getStatusBarHeight(Context context) {
        //获取status_bar_height资源的ID
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            return context.getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    /**
     * 设置背景颜色
     * @param resId
     */
    public void setLayRootBackgroundColor(int resId){
        translucentActionBar.setLayRootBackgroundColor(getResources().getColor(resId));
    }
    /**
     * 设置状态栏颜色
     * @param resId
     */
    public  void setStatusBarBackgroundColor(int resId){
        translucentActionBar.setStatusBarBackgroundColor(getResources().getColor(resId));
    }
    /**
     * 关闭输入法
     */
    protected void closeInputView() {
        try {

            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 弹出输入法
     * @param editText
     */
    protected void showInputView(EditText editText){
        if (editText==null){
            return;
        }
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        editText.requestFocusFromTouch();
        try {
            InputMethodManager inputMethodManager= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(editText,0);
            //inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}
