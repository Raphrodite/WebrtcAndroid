package com.example.webrtcandroid;

import org.webrtc.MediaStream;

/**
 * * Copyright * 圣通电力
 *
 * @ProjectName: WebrtcAndroid
 * @Package: com.example.webrtcandroid
 * @ClassName: IViewCallback
 * @Description: 界面回调 添加本地流 添加远程流 关闭
 * @Author: Raphrodite
 * @CreateDate: 2023/2/14
 */
public interface IViewCallback {

    /**
     * 设置本地流
     * @param stream
     */
    void onSetLocalStream(MediaStream stream);

    /**
     * 设置本地屏幕共享的流
     * @param stream
     */
    void onSetScreenStream(MediaStream stream);

    /**
     * 添加远程流
     * @param stream
     */
    void onAddRemoteStream(MediaStream stream);

    /**
     * 关闭
     */
    void onClose();
}
