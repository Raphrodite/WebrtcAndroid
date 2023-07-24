package com.example.chatwebrtc.webrtc;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.chatwebrtc.IConnectEvent;
import com.example.chatwebrtc.IViewCallback;
import com.example.chatwebrtc.bean.MouseEventBean;
import com.example.chatwebrtc.peer.PeerConnectionHelper;
import com.example.chatwebrtc.websocket.IWebSocket;
import com.example.chatwebrtc.websocket.WebSocketManager;

import org.webrtc.EglBase;
import org.webrtc.IceCandidate;

import java.util.ArrayList;

/**
 * * Copyright * 圣通电力
 *
 * @ProjectName: WebrtcAndroid
 * @Package: com.example.webrtcandroid.webrtc
 * @ClassName: WebrtcManager
 * @Description: websocket和peer通道
 * @Author: Raphrodite
 * @CreateDate: 2023/2/14
 */
public class WebRtcManager implements IWebRtcEvents {

    /**
     * Log tag
     */
    private final String TAG = "WebRtcManager_zrzr";

    /**
     * 单例对象
     */
    private static WebRtcManager webrtcManager;

    /**
     * handler
     */
    private Handler handler = new Handler(Looper.getMainLooper());

    /**
     * WebSocket
     */
    private IWebSocket webSocket;

    /**
     * Peer
     */
    private PeerConnectionHelper peerHelper;

    /**
     * 初始化参数
     */
    private String webSocketUrl;
    private MyIceServer[] iceServers;
    private IConnectEvent connectEvent;

    /**
     * connect传递参数
     */
    private int mediaType;
    private boolean videoEnable;

    /**
     * 单例实现
     * @return
     */
    public static WebRtcManager getInstance() {
        if (webrtcManager == null) {
            synchronized (WebSocketManager.class) {
                if (webrtcManager == null) {
                    webrtcManager = new WebRtcManager();
                }
            }
        }
        return webrtcManager;
    }

    /**
     * 初始化
     * @param webSocketUrl
     * @param iceServers
     * @param event
     */
    public void init(String webSocketUrl, MyIceServer[] iceServers, IConnectEvent event) {
        this.webSocketUrl = webSocketUrl;
        this.iceServers = iceServers;
        connectEvent = event;
    }

    /**
     * 建立连接 WebSocket Peer通道
     * @param mediaType
     */
    public void connect(int mediaType) {
        if (webSocket == null) {
            this.mediaType = mediaType;
            videoEnable = mediaType != MediaType.TYPE_AUDIO;
            webSocket = new WebSocketManager(this);
            webSocket.connect(webSocketUrl);
            peerHelper = new PeerConnectionHelper(webSocket, iceServers);
        } else {
            // 正在通话中
            webSocket.close();
            webSocket = null;
            peerHelper = null;
        }
    }

    /**
     * 设置界面回调
     * @param callback
     */
    public void setCallback(IViewCallback callback) {
        if (peerHelper != null) {
            peerHelper.setViewCallback(callback);
        }
    }

    //===================================控制功能 activity start==============================================

    /**
     * 匹配客服
     * @param context
     * @param eglBase
     * @param captureIntent
     */
    public void sendQueue(Context context, EglBase eglBase, Intent captureIntent) {
        if (peerHelper != null) {
            peerHelper.initContext(context, eglBase, captureIntent);
        }
        if (webSocket != null) {
            webSocket.sendQueue("0");
        }
    }

    /**
     * 切换通话方式应答
     * @param ack 应答状态 AGREE同意、REFUSE拒绝
     */
    public void sendChangeCallTypeAck(String ack) {
        if (webSocket != null) {
            webSocket.sendChangeCallTypeAck(ack);
        }
    }

    /**
     * 发送action 涂鸦是否开启 远程控制是否开启
     * @param action
     */
    public void sendCustomAction(String action) {
        if (webSocket != null) {
            webSocket.sendCustomAction(action);
        }
    }

    /**
     * 翻转摄像头
     */
    public void switchCamera() {
        if (peerHelper != null) {
            peerHelper.switchCamera();
        }
    }

    /**
     * 设置是否静音
     * @param enable
     */
    public void toggleMute(boolean enable) {
        if (peerHelper != null) {
            peerHelper.toggleMute(enable);
        }
    }

    /**
     * 扬声器
     * @param enable
     */
    public void toggleSpeaker(boolean enable) {
        if (peerHelper != null) {
            peerHelper.toggleSpeaker(enable);
        }
    }

    /**
     * 退出聊天
     */
    public void exitCall() {
        if (peerHelper != null) {
            webSocket = null;
            peerHelper.exitCall();
        }
    }

    //===================================控制功能 activity end==============================================

    // ==================================信令回调 start===============================================

    @Override
    public void onWebSocketOpen() {
        //webSocket连接成功
        handler.post(() -> {
            if (connectEvent != null) {
                connectEvent.onSuccess();
            }
        });
    }

    @Override
    public void onWebSocketFailed(String msg) {
        //webSocket连接失败
        handler.post(() -> {
            if (webSocket != null && !webSocket.isOpen()) {
                connectEvent.onFailed(msg);
            } else {
                if (peerHelper != null) {
                    peerHelper.closeChatWindow();
                    peerHelper.exitCall();
                }
            }
        });
    }

    @Override
    public void onWait(int queueCount) {
        //匹配客服应答-未匹配到客服需等待
        handler.post(() -> {
            if (connectEvent != null) {
                connectEvent.onWait(queueCount);
            }
        });
    }

    @Override
    public void onMatch() {
        //匹配客服应答-已匹配到客服
        handler.post(() -> {
            if (connectEvent != null) {
                connectEvent.onMatch();
            }
        });
    }

    @Override
    public void onSendCall(ArrayList<String> connections) {
        //发起通话
        handler.post(() -> {
            if (peerHelper != null) {
                peerHelper.onSendCall(connections, videoEnable);
                if (mediaType == MediaType.TYPE_VIDEO) {
                    toggleSpeaker(true);
                }
            }
        });
    }

    @Override
    public void onReceiveAnswer(String id, String sdp) {
        //发送offer接收到answer
        handler.post(() -> {
            if (peerHelper != null) {
                Log.e(TAG, "onReceiverAnswer");
                peerHelper.onReceiverAnswer(id, sdp);
            }
        });
    }

    @Override
    public void onRemoteIceCandidate(String id, IceCandidate iceCandidate) {
        //接收到ice回调
        handler.post(() -> {
            if (peerHelper != null) {
                Log.e(TAG, "onRemoteIceCandidate");
                peerHelper.onRemoteIceCandidate(id, iceCandidate);
            }
        });
    }

    @Override
    public void onCall(String callType) {
        //即将接通
        handler.post(() -> {
            if (connectEvent != null) {
                Log.e(TAG, "onCall");
                connectEvent.onCall(callType);
            }
        });
    }

    @Override
    public void onHangUp() {
        //web挂断
        handler.post(() -> {
            if (connectEvent != null) {
                Log.e(TAG, "onHangUp");
                connectEvent.onHangUp();
            }
        });
    }

    @Override
    public void onChangeCall(String beforeCallType, String afterCallType) {
        //切换通话方式
        handler.post(() -> {
            if (connectEvent != null) {
                Log.e(TAG, "onChangeCall");
                connectEvent.onChangeCall(beforeCallType, afterCallType);
            }
        });
    }

    @Override
    public void onChangeCancel() {
        //切换通话方式取消
        handler.post(() -> {
            if (connectEvent != null) {
                Log.e(TAG, "onChangeCancel");
                connectEvent.onChangeCancel();
            }
        });
    }

    @Override
    public void onAction(String action) {
        //自定义消息
        handler.post(() -> {
            if (connectEvent != null) {
                Log.e(TAG, "onAction");
                connectEvent.onAction(action);
            }
        });
    }

    @Override
    public void onSendImage(String imageStr) {
        //发送图片
        handler.post(() -> {
            if (connectEvent != null) {
                Log.e(TAG, "onSendImage");
                connectEvent.onSendImage(imageStr);
            }
        });
    }

    @Override
    public void onSendPoint(MouseEventBean mouseEventBean) {
        //远程控制 发送坐标
        handler.post(() -> {
            if (connectEvent != null) {
                Log.e(TAG, "onSendPoint");
                connectEvent.onSendPoint(mouseEventBean);
            }
        });
    }

    // ==================================信令回调 end===============================================
}
