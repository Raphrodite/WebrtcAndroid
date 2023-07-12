package com.example.chatwebrtc.webrtc;

import com.example.chatwebrtc.bean.MouseEventBean;

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
     * 匹配客服应答-未匹配到客服需等待
     * @param queueCount 等待人数
     */
    void onWait(int queueCount);

    /**
     * 匹配客服应答-已匹配到客服
     */
    void onMatch();

    /**
     * 发起通话
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
     * web点击接听
     * @param callType 通话类型：AUDIO 音频、VIDEO 视频
     */
    void onCall(String callType);

    /**
     * web挂断
     */
    void onHangUp();

    /**
     * 切换通话方式
     * @param beforeCallType 变更前通话类型 AUDIO、VIDEO
     * @param afterCallType 变更后通话类型 AUDIO、VIDEO
     */
    void onChangeCall(String beforeCallType, String afterCallType);

    /**
     * 切换通话方式取消
     */
    void onChangeCancel();

    /**
     * 自定义消息 摄像头的切换 开启或者关闭涂鸦
     */
    void onAction(String action);

    /**
     * 涂鸦 发送图片
     * @param imageStr
     */
    void onSendImage(String imageStr);

    /**
     * 远程控制 发送坐标
     * @param mouseEventBean
     */
    void onSendPoint(MouseEventBean mouseEventBean);
}
