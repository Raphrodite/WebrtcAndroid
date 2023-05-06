package com.example.chatwebrtc.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.example.chatwebrtc.IViewCallback;
import com.example.chatwebrtc.R;
import com.example.chatwebrtc.utils.webrtc.ProxyVideoSink;
import com.example.chatwebrtc.utils.webrtc.WebRtcManager;

import org.webrtc.EglBase;
import org.webrtc.MediaStream;
import org.webrtc.RendererCommon;
import org.webrtc.SurfaceViewRenderer;

/**
 * * Copyright * 圣通电力
 *
 * @ProjectName: WebrtcAndroid
 * @Package: com.example.webrtcandroid.view
 * @ClassName: CallNewWindow
 * @Description: 呼叫弹窗
 * @Author: Raphrodite
 * @CreateDate: 2023/4/6
 */
public class CallNewWindow extends BaseFloatingWindow {

    /**
     * Log TAG
     */
    public final static String TAG = "CallNewWindow_zrzr";

    private static CallNewWindow instance;

    private SurfaceViewRenderer local_view;
    private SurfaceViewRenderer remote_view;

    private ProxyVideoSink localRender;
    private ProxyVideoSink remoteRender;

    private EglBase rootEglBase;

    private boolean isSwappedFeeds;
    private boolean videoEnable = true;

    private WebRtcManager manager;

    private static Activity mActivity;

    public static CallNewWindow getInstance(Context context) {
        if (instance == null) {
            mActivity = (Activity) context;
            instance = new CallNewWindow(context);
        }
        return instance;
    }

    public CallNewWindow(Context context) {
        super(context);
    }

    @Override
    protected int setLayoutId() {
        return R.layout.layout_call_new;
    }

    @Override
    protected void initView(View mRootView) {
        local_view = mRootView.findViewById(R.id.local_view_render);
        remote_view = mRootView.findViewById(R.id.remote_view_render);

        rootEglBase = EglBase.create();

        //本地图像初始化
        local_view.init(rootEglBase.getEglBaseContext(), null);
        local_view.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        local_view.setZOrderMediaOverlay(true);
        local_view.setMirror(true);
        localRender = new ProxyVideoSink();
        //远端图像初始化
        remote_view.init(rootEglBase.getEglBaseContext(), null);
        remote_view.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_BALANCED);
        remote_view.setMirror(true);
        remoteRender = new ProxyVideoSink();

        setSwappedFeeds(false);

        startCall();
    }

    private void startCall() {
        manager = WebRtcManager.getInstance();
        manager.setCallback(new IViewCallback() {
            @Override
            public void onSetLocalStream(MediaStream stream) {
                Log.e(TAG, "onSetLocalStream");
                if (stream.videoTracks.size() > 0) {
                    stream.videoTracks.get(0).addSink(localRender);
                }

                if (videoEnable) {
                    stream.videoTracks.get(0).setEnabled(true);
                }
            }

            @Override
            public void onSetScreenStream(MediaStream stream) {

            }

            @Override
            public void onAddRemoteStream(MediaStream stream) {
                Log.e(TAG, "onAddRemoteStream");
                Log.e(TAG, "stream size = " + stream.videoTracks.size());
                if (stream.videoTracks.size() > 0) {
                    stream.videoTracks.get(0).addSink(remoteRender);
                }
                if (videoEnable) {
                    stream.videoTracks.get(0).setEnabled(true);

                    setSwappedFeeds(false);
                }
            }

            @Override
            public void onClose() {

            }
        });

    }

    private void setSwappedFeeds(boolean isSwappedFeeds) {
        this.isSwappedFeeds = isSwappedFeeds;
        localRender.setTarget(isSwappedFeeds ? remote_view : local_view);
        remoteRender.setTarget(isSwappedFeeds ? local_view : remote_view);
    }

    @Override
    protected void onBindListener() {

    }

    @Override
    public void show(int marginRight, int marginBottom, OnShowListener onShowListener) {
        super.show(marginRight, marginBottom, onShowListener);
    }

    @Override
    public void showTopRight(OnShowListener onShowListener, Intent captureIntent) {
        super.showTopRight(onShowListener);
        Log.e("zrzr", "showTopRight");
        manager.sendPreCall(mContext, rootEglBase, captureIntent);
    }

}
