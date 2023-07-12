package com.example.chatwebrtc;

import com.example.chatwebrtc.bean.MouseEventBean;

/**
 * * Copyright * 圣通电力
 *
 * @ProjectName: WebrtcAndroid
 * @Package: com.example.webrtcandroid
 * @ClassName: IConnectEvent
 * @Description: 连接状态事件-WebSocket
 * @Author: Raphrodite
 * @CreateDate: 2023/2/15
 */
public interface IConnectEvent {

    /**
     * 连接成功
     */
    void onSuccess();

    /**
     * 连接失败
     * @param msg
     */
    void onFailed(String msg);

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
     * web点击接听
     * @param callType 通话类型：AUDIO 音频、VIDEO 视频
     */
    void onCall(String callType);

    /**
     * web 挂断
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
     * 自定义消息 摄像头的切换
     * @param action
     */
    void onAction(String action);

    /**
     * 发送图片
     * @param imageStr
     */
    void onSendImage(String imageStr);

    /**
     * 远程控制 发送坐标
     * @param mouseEventBean
     */
    void onSendPoint(MouseEventBean mouseEventBean);
}
