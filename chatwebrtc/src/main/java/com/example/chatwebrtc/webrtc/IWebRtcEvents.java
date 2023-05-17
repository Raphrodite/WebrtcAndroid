package com.example.chatwebrtc.webrtc;

import org.webrtc.IceCandidate;

import java.util.ArrayList;

/**
 * * Copyright * 圣通电力
 *
 * @ProjectName: WebrtcAndroid
 * @Package: com.example.webrtcandroid.webrtc
 * @ClassName: IWebRTCEvents
 * @Description: 监听WebSocket中的消息回调
 * @Author: Raphrodite
 * @CreateDate: 2023/2/14
 */
public interface IWebRtcEvents {

    /**
     * WebSocket连接成功
     */
    void onWebSocketOpen();

    /**
     * WebSocket连接失败
     * @param msg
     */
    void onWebSocketFailed(String msg);

    /**
     * 预通话配置-返回排队中
     */
    void onQueue();

    /**
     * 发起正式通话
     * @param connections
     */
    void onSendCall(ArrayList<String> connections);

    /**
     * 发送offer 接收到answer
     * @param id
     * @param sdp
     */
    void onReceiveAnswer(String id, String sdp);

    /**
     * 接收到Ice
     * @param id
     * @param iceCandidate
     */
    void onRemoteIceCandidate(String id, IceCandidate iceCandidate);

    /**
     * web点击挂断
     */
    void onHangUp();

    /**
     * web点击接听
     */
    void onCall();
}
