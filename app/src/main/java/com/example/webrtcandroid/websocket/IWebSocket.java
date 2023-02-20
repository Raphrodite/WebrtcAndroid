package com.example.webrtcandroid.websocket;

import org.webrtc.IceCandidate;

/**
 * * Copyright * 圣通电力
 *
 * @ProjectName: WebrtcAndroid
 * @Package: com.example.webrtcandroid.websocket
 * @ClassName: IWebSocket
 * @Description: websocket接口实现
 * @Author: Raphrodite
 * @CreateDate: 2023/2/14
 */
public interface IWebSocket {

    /**
     * WebScoket进行连接
     * @param url 连接地址
     */
    void connect(String url);

    /**
     * 是否连接成功
     */
    boolean isOpen();

    /**
     * 连接关闭
     */
    void close();

    /**
     * 发起预通话配置(分配对应客服)
     */
    void sendPre();

    /**
     * 发起通话
     * @param offerId  安卓客户端ID
     * @param answerId  分配的客服ID（浏览器端登录用户ID，仅当connectStatus为0时有值）
     */
    void sendCall(String offerId, String answerId);

    /**
     * 发送offer信息
     * @param sdp
     */
    void sendOffer(String sdp);

    /**
     * 发送Ice信息
     * @param iceCandidate
     */
    void sendIceCandidate(IceCandidate iceCandidate);

    /**
     * 处理接收到的数据
     * @param message
     */
    void handleMessage(String message);
}
