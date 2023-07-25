package com.example.chatwebrtc.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.chatwebrtc.view.RobotWindow;

/**
 * * Copyright * 圣通电力
 *
 * @ProjectName: WebrtcAndroid
 * @Package: com.example.webrtcandroid
 * @ClassName:
 * @Description:
 * @Author: Raphrodite
 * @CreateDate: 2023/5/24
 */
public class CallChatService extends Service {

    /**
     * Log TAG
     */
    public final static String TAG = "ChatService_zrzr";

    private CallChatService instance;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "ChatService onCreate");
        instance = this;
        //展示客服悬浮窗
        RobotWindow.getInstance(this).show(0, 0, null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "ChatService onDestroy");
        instance = null;
        //隐藏客服悬浮窗
        RobotWindow.getInstance(this).hide(null);
    }
}
