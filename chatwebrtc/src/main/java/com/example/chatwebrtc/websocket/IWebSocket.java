package com.example.chatwebrtc.websocket;

import org.webrtc.IceCandidate;

import java.util.List;

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
     * 匹配客服
     * @param againQueue 重新排队时传1 （客服长时间未接听、客服拒接）
     */
    void sendQueue(String againQueue);

    /**
     * 发起通话
     * @param serverId 配对到的客服id
     */
    void sendCall(String serverId);

    /**
     * 发送offer信息
     * @param sdp
     * @param mids
     */
    void sendOffer(String sdp, List<String> mids);

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
