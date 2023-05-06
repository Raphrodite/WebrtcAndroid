package com.example.chatwebrtc.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * * Copyright * 圣通电力
 *
 * @ProjectName: WebrtcAndroid
 * @Package: com.example.webrtcandroid.utils
 * @ClassName: ScreenUtil
 * @Description: 屏幕 工具类
 * @Author: Raphrodite
 * @CreateDate: 2023/4/6
 */
public class ScreenUtil {

    public ScreenUtil() {
    }

    public static int dip2px(float paramFloat) {
        return (int)(paramFloat * Resources.getSystem().getDisplayMetrics().density + 0.5F);
    }

    /** @deprecated */
    @Deprecated
    public static int dip2px(Context paramContext, float paramFloat) {
        return dip2px(paramFloat);
    }

    public static DisplayMetrics getDisplayMetrics(Activity paramActivity) {
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        paramActivity.getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        return localDisplayMetrics;
    }

    public static int getScreenHeight(Activity paramActivity) {
        return getDisplayMetrics(paramActivity).heightPixels;
    }

    public static int getScreenHeights(Activity paramActivity) {
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        paramActivity.getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        int i = 0;
        int j = paramActivity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (j > 0) {
            i = paramActivity.getResources().getDimensionPixelSize(j);
        }

        return localDisplayMetrics.heightPixels - i;
    }

    public static int getScreenWidth(Activity paramActivity) {
        return getDisplayMetrics(paramActivity).widthPixels;
    }

    public static int px2dip(float paramFloat) {
        return (int)(paramFloat / Resources.getSystem().getDisplayMetrics().density + 0.5F);
    }

    public static int px2sp(float paramFloat) {
        return (int)(paramFloat / Resources.getSystem().getDisplayMetrics().scaledDensity + 0.5F);
    }

    public static int sp2px(float paramFloat) {
        return (int)(paramFloat * Resources.getSystem().getDisplayMetrics().scaledDensity + 0.5F);
    }

    public static int[] getScreenWH(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return new int[]{displayMetrics.widthPixels, displayMetrics.heightPixels};
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

}
