package com.example.webrtcandroid.webrtc;

import org.webrtc.Logging;
import org.webrtc.VideoFrame;
import org.webrtc.VideoSink;

/**
 * * Copyright * 圣通电力
 *
 * @ProjectName: WebrtcAndroid
 * @Package: com.example.webrtcandroid.webrtc
 * @ClassName: ProxyVideoSink
 * @Description:
 * @Author: Raphrodite
 * @CreateDate: 2023/2/15
 */
public class ProxyVideoSink implements VideoSink {

    private static final String TAG = "ProxyVideoSink_zrzr";
    private VideoSink target;

    @Override
    synchronized public void onFrame(VideoFrame frame) {
        if (target == null) {
            Logging.d(TAG, "Dropping frame in proxy because target is null.");
            return;
        }
        target.onFrame(frame);
    }

    synchronized public void setTarget(VideoSink target) {
        this.target = target;
    }
}
