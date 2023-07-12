package com.example.chatwebrtc.control;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.Method;

/**
 * @describe: 屏幕模式管理类(横竖屏动态切换 、 屏幕常亮设置 、 屏幕宽高比等)
 * @author: yyh
 * @createTime: 2021/4/22 15:26
 * @className: BRScreenManagerUtils
 */
public class BRScreenManagerUtils {

    /**
     * 开启全屏模式
     */
    public static void setFullScreenOn(Window window) {
        WindowManager.LayoutParams attrs = window.getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        window.setAttributes(attrs);
    }

    /**
     * 关闭全屏模式
     */
    public static void setFullScreenOff(Window window) {
        WindowManager.LayoutParams attr = window.getAttributes();
        attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.setAttributes(attr);
        window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    /**
     * 获取屏幕宽度
     */
    public static int getScreenRealWidth(Context context) {
        int vh = 0;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        try {
            @SuppressWarnings("rawtypes")
            Class c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            vh = dm.widthPixels;
        } catch (Exception e) {
            e.printStackTrace();
            return vh;
        }
        return vh;
    }

    /**
     * 获取屏幕高度(不包含底部虚拟返回键高度)
     */
    public static int getScreenRealHeight(Context context) {
        int vh = 0;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//        Display display = windowManager.getDefaultDisplay();
//        DisplayMetrics dm = new DisplayMetrics();
        try {
//            @SuppressWarnings("rawtypes")
//            Class c = Class.forName("android.view.Display");
//            @SuppressWarnings("unchecked")
//            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
//            method.invoke(display, dm);
//            vh = dm.heightPixels;
            vh = windowManager.getDefaultDisplay().getHeight();
        } catch (Exception e) {
            e.printStackTrace();
            return vh;
        }
        return vh;
    }

    /**
     * 获取状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        try {
            int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
            result = context.getResources().getDimensionPixelSize(resourceId);
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
        return result;
    }

    /**
     * 设置横屏模式
     *
     * @param activity
     */
    @SuppressLint("SourceLockedOrientationActivity")
    public static void setLandscape(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    /**
     * 设置竖屏模式
     *
     * @param activity
     */
    @SuppressLint("SourceLockedOrientationActivity")
    public static void setPortrait(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * 设置跟随系统模式
     *
     * @param activity
     */
    public static void setUnspecified(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    /**
     * 设置屏幕常亮
     */
    public static void keepScreenOn(Activity activity) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /**
     * 关闭屏幕常亮
     */
    public static void clearScreenOn(Activity activity) {
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /**
     * 隐藏输入法
     */
    public static void hideSoftKeyboard(Activity activity, View view) {
        if (activity == null) {
            return;
        }
        if (view == null) {
            return;
        }
        try {
            InputMethodManager inputMgr = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
//            if (inputMgr != null) {
//                //如果输入法在窗口上已经显示，则隐藏，反之则显示
//                inputMgr.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
//            }
            if (inputMgr != null) {
                inputMgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示输入法
     */
    public static void showSoftKeyboard(Context context, View view) {
        if (context == null) {
            return;
        }
        try {
            InputMethodManager inputMgr = (InputMethodManager) context.getSystemService(Service.INPUT_METHOD_SERVICE);
            if (inputMgr != null) {
                inputMgr.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Window窗体背景设置
     *
     * @param activity
     * @param bgAlpha  0.5f半透明  1.0f全透明
     */
    public static void setBackgroundAlpha(Activity activity, float bgAlpha) {
        if (activity == null) {
            return;
        }
        Window window = activity.getWindow();
        if (window == null) {
            return;
        }
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = bgAlpha;
        window.setAttributes(lp);
    }
}
