package com.example.webrtcandroid.websocket;

import org.webrtc.CalledByNative;

/**
 * * Copyright * 圣通电力
 *
 * @ProjectName: WebrtcAndroid
 * @Package: com.example.webrtcandroid.websocket
 * @ClassName: IceCandidate
 * @Description:
 * @Author: Raphrodite
 * @CreateDate: 2023/2/15
 */
public class IceCandidate {
    public final String sdpMid;
    public final int sdpMLineIndex;
    public final String sdp;
    public final String serverUrl;

    public IceCandidate(String sdpMid, int sdpMLineIndex, String sdp) {
        this.sdpMid = sdpMid;
        this.sdpMLineIndex = sdpMLineIndex;
        this.sdp = sdp;
        this.serverUrl = "";
    }

    @CalledByNative
    IceCandidate(String sdpMid, int sdpMLineIndex, String sdp, String serverUrl) {
        this.sdpMid = sdpMid;
        this.sdpMLineIndex = sdpMLineIndex;
        this.sdp = sdp;
        this.serverUrl = serverUrl;
    }

    @Override
    public String toString() {
        return sdpMid + ":" + sdpMLineIndex + ":" + sdp + ":" + serverUrl;
    }

    @CalledByNative
    String getSdpMid() {
        return sdpMid;
    }

    @CalledByNative
    String getSdp() {
        return sdp;
    }
}
