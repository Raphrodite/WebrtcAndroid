package com.example.chatwebrtc.webrtc;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.chatwebrtc.IConnectEvent;
import com.example.chatwebrtc.bean.LoginUseBean;
import com.example.chatwebrtc.bean.MouseEventBean;
import com.example.chatwebrtc.utils.CallConfigs;
import com.example.chatwebrtc.view.CallChatWindow;

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
     * Log tag
     */
    private static final String TAG = "WebRtcUtil_zrzr";

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
        Log.e(TAG, "token = " + TOKEN + ", address = " + bean.getStunConfig().getAddress() + ", username = " +  bean.getStunConfig().getUsername() +
                ", password = " + bean.getStunConfig().getPassword());
        //初始化
        WebRtcManager.getInstance().init(webSocketUrl, iceServers, new IConnectEvent() {
            @Override
            public void onSuccess() {
                //连接成功 开始匹配客服
                CallChatWindow.getInstance(activity).showCallStatus(CallConfigs.CALL_STATUS_ING);
                //发起预通话配置
                CallChatWindow.getInstance(activity).showTopRight(null, captureIntent);
            }

            @Override
            public void onFailed(String msg) {
                //连接失败
                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onWait(int queueCount) {
                //匹配客服应答-未匹配到客服需等待
                CallChatWindow.getInstance(activity).showCallStatusQueue(CallConfigs.CALL_STATUS_WAIT, queueCount);
            }

            @Override
            public void onMatch() {
                //匹配客服应答-已匹配到客服
                CallChatWindow.getInstance(activity).showCallStatus(CallConfigs.CALL_STATUS_MATCH);
            }

            @Override
            public void onCall(String callType) {
                //web点击接听 展示即将接通状态页面
                CallChatWindow.getInstance(activity).showCallStatusCallType(CallConfigs.CALL_STATUS_SOON, callType);
            }

            @Override
            public void onHangUp() {
                //web 挂断
                CallChatWindow.getInstance(activity).showCallStatus(CallConfigs.CALL_STATUS_HANG_UP);
            }

            @Override
            public void onChangeCall(String beforeCallType, String afterCallType) {
                //切换通话方式
                CallChatWindow.getInstance(activity).showChangeCallType(beforeCallType, afterCallType);
            }

            @Override
            public void onChangeCancel() {
                //切换通话方式取消
                CallChatWindow.getInstance(activity).showChangeCallTypeCancal();
            }

            @Override
            public void onAction(String action) {
                //自定义消息
                CallChatWindow.getInstance(activity).showAction(action);
            }

            @Override
            public void onSendImage(String imageStr) {
                //发送图片展示
                CallChatWindow.getInstance(activity).showImage(imageStr);
            }

            @Override
            public void onSendPoint(MouseEventBean mouseEventBean) {
                //远程控制 发送坐标
                CallChatWindow.getInstance(activity).showPoint(mouseEventBean);
            }
        });
        //建立连接
        WebRtcManager.getInstance().connect(videoEnable ? MediaType.TYPE_VIDEO : MediaType.TYPE_AUDIO);
    }

}
