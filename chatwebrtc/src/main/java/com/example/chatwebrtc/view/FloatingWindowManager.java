package com.example.chatwebrtc.view;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;

/**
 * * Copyright * 圣通电力
 *
 * @ProjectName: WebrtcAndroid
 * @Package: com.example.webrtcandroid.view
 * @ClassName: FloatingWindowManager
 * @Description: 悬浮窗 工具类
 * @Author: Raphrodite
 * @CreateDate: 2023/4/6
 */
public class FloatingWindowManager {

    private WindowManager mWindowManager;
    private static FloatingWindowManager mInstance;
    private Context mContext;

    public static FloatingWindowManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new FloatingWindowManager(context.getApplicationContext());
        }
        return mInstance;
    }

    private FloatingWindowManager(Context context) {
        mContext = context;
        //获得WindowManager对象
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
    }

    /**
     * 添加悬浮窗
     *
     * @param view
     * @param params
     * @return
     */
    public boolean addView(View view, WindowManager.LayoutParams params) {
        try {
            mWindowManager.addView(view, params);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 移除悬浮窗
     *
     * @param view
     * @return
     */
    public boolean removeView(View view) {
        try {
            mWindowManager.removeView(view);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 更新悬浮窗参数
     *
     * @param view
     * @param params
     * @return
     */
    public boolean updateView(View view, WindowManager.LayoutParams params) {
        try {
            mWindowManager.updateViewLayout(view, params);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
