package com.example.chatwebrtc.webrtc;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.chatwebrtc.IConnectEvent;
import com.example.chatwebrtc.bean.LoginUseBean;
import com.example.chatwebrtc.utils.CallConfigs;
import com.example.chatwebrtc.view.CallNewWindow;

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

    public static String TOKEN = "" ;

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
     * @param captureIntent
     */
    public static void callNewWindow(Activity activity, String webSocketUrl, boolean videoEnable, Intent captureIntent, LoginUseBean bean) {
        TOKEN = bean.getToken();
        iceServers = new MyIceServer[] {
                // 添加其他IceServer对象
                new MyIceServer(bean.getStunConfig().getAddress(), bean.getStunConfig().getUsername(), bean.getStunConfig().getPassword())
        };
        Log.e("zrzr", "token = " + TOKEN + ", address = " + bean.getStunConfig().getAddress() + ", username = " +  bean.getStunConfig().getUsername() +
                ", password = " + bean.getStunConfig().getPassword());
        //初始化
        WebRtcManager.getInstance().init(webSocketUrl, iceServers, new IConnectEvent() {
            @Override
            public void onSuccess() {
                //连接成功 开始匹配客服
                CallNewWindow.getInstance(activity).showCallStatus(CallConfigs.CALL_STATUS_ING);
                //发起预通话配置
                CallNewWindow.getInstance(activity).showTopRight(null, captureIntent);
            }

            @Override
            public void onFailed(String msg) {
                //连接失败
                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onWait() {
                //匹配客服应答-未匹配到客服需等待
                CallNewWindow.getInstance(activity).showCallStatus(CallConfigs.CALL_STATUS_WAIT);
            }

            @Override
            public void onMatch() {
                //匹配客服应答-已匹配到客服
                CallNewWindow.getInstance(activity).showCallStatus(CallConfigs.CALL_STATUS_MATCH);
            }

            @Override
            public void onQueue() {
                //排队中
                Toast.makeText(activity, "当前正在排队中", Toast.LENGTH_SHORT).show();
                //展示排队状态页面
                CallNewWindow.getInstance(activity).showCallStatus(CallConfigs.CALL_STATUS_QUEUE);
            }

            @Override
            public void onHangUp() {
                //web点击挂断
                CallNewWindow.getInstance(activity).showCallStatus(CallConfigs.CALL_STATUS_HANG_UP);
                Toast.makeText(activity, "通话已被挂断", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCall() {
                //web点击接听 展示即将接通状态页面
                CallNewWindow.getInstance(activity).showCallStatus(CallConfigs.CALL_STATUS_SOON);
            }
        });
        //建立连接
        WebRtcManager.getInstance().connect(videoEnable ? MediaType.TYPE_VIDEO : MediaType.TYPE_AUDIO);
    }

}
