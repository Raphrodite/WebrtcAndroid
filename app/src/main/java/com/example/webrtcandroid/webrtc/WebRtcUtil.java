package com.example.webrtcandroid.webrtc;

import static com.example.webrtcandroid.webrtc.WebRtcManager.IS_SCREEN;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.example.webrtcandroid.CallVideoActivity;
import com.example.webrtcandroid.IConnectEvent;
import com.example.webrtcandroid.MainActivity;
import com.example.webrtcandroid.view.CallNewWindow;

/**
 * * Copyright * 圣通电力
 *
 * @ProjectName: WebrtcAndroid
 * @Package: com.example.webrtcandroid.webrtc
 * @ClassName: WebrtcUtil
 * @Description: 进行管理activity跳转，判断websocket是否连接成功
 * @Author: Raphrodite
 * @CreateDate: 2023/2/14
 */
public class WebRtcUtil {

    /**
     * 信令服务器
     */
    private static MyIceServer[] iceServers = {
            new MyIceServer("turn:rpc.stdlnj.cn:3478",
                    "asd",
                    "123456")
    };

    /**
     * 发起通话
     * @param activity
     * @param webSocketUrl
     * @param videoEnable
     * @param isScreen
     */
    public static void callVideo(Activity activity, String webSocketUrl, boolean videoEnable, boolean isScreen) {
        if(isScreen) {
            IS_SCREEN = 1;
        }else{
            IS_SCREEN = 0;
        }
        //初始化
        WebRtcManager.getInstance().init(webSocketUrl, iceServers, new IConnectEvent() {
            @Override
            public void onSuccess() {
                //连接成功 跳转通话界面
                CallVideoActivity.openActivity(activity, videoEnable);
            }

            @Override
            public void onFailed(String msg) {

            }

            @Override
            public void onQueue() {
                //排队中
                Toast.makeText(activity, "当前正在排队中", Toast.LENGTH_SHORT).show();
            }
        });
        //建立连接
        WebRtcManager.getInstance().connect(videoEnable ? MediaType.TYPE_VIDEO : MediaType.TYPE_AUDIO);
    }

    /**
     * 发起通话
     * @param activity
     * @param webSocketUrl
     * @param videoEnable
     * @param isScreen
     */
    public static void callNewWindow(Activity activity, String webSocketUrl, boolean videoEnable, boolean isScreen, Intent captureIntent) {
        if(isScreen) {
            IS_SCREEN = 1;
        }else{
            IS_SCREEN = 0;
        }
        //初始化
        WebRtcManager.getInstance().init(webSocketUrl, iceServers, new IConnectEvent() {
            @Override
            public void onSuccess() {
                //连接成功 跳转通话界面
                CallNewWindow.getInstance(activity).showTopRight(null, captureIntent);
            }

            @Override
            public void onFailed(String msg) {

            }

            @Override
            public void onQueue() {
                //排队中
                Toast.makeText(activity, "当前正在排队中", Toast.LENGTH_SHORT).show();
            }
        });
        //建立连接
        WebRtcManager.getInstance().connect(videoEnable ? MediaType.TYPE_VIDEO : MediaType.TYPE_AUDIO);
    }

}
