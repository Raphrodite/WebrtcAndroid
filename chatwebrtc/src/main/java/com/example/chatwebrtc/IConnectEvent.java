package com.example.chatwebrtc;

/**
 * * Copyright * 圣通电力
 *
 * @ProjectName: WebrtcAndroid
 * @Package: com.example.webrtcandroid
 * @ClassName: IConnectEvent
 * @Description: 连接状态事件
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
     */
    void onWait();

    /**
     * 匹配客服应答-已匹配到客服
     */
    void onMatch();

    /**
     * 排队中
     */
    void onQueue();

    /**
     * web点击挂断
     */
    void onHangUp();

    /**
     * web点击接听
     */
    void onCall();
}
