package com.example.chatwebrtc.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.chatwebrtc.IViewCallback;
import com.example.chatwebrtc.R;
import com.example.chatwebrtc.utils.CallConfigs;
import com.example.chatwebrtc.webrtc.ProxyVideoSink;
import com.example.chatwebrtc.webrtc.WebRtcManager;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.webrtc.EglBase;
import org.webrtc.MediaStream;
import org.webrtc.RendererCommon;
import org.webrtc.SurfaceViewRenderer;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;

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

    /**
     * 呼叫弹窗 单例
     */
    private static CallNewWindow instance;

    /**
     * 控件，继承于SurfaceView的渲染View，提供了OpenGL渲染图像数据的功能，加载本地和远端
     */
    private SurfaceViewRenderer local_view, remote_view;

    /**
     * ProxyVideoSink 本地、远端
     */
    private ProxyVideoSink localRender, remoteRender;

    /**
     * 提供EGL的渲染上下文及EGL的版本兼容
     */
    private EglBase rootEglBase;

    private boolean videoEnable = true;

    /**
     * WebRtc工具类
     */
    private WebRtcManager manager;

    /**
     * Activity对象
     */
    private static Activity mActivity;

    /**
     * 挂断按钮
     */
    private TextView tvHangUp;

    /**
     * 呼叫状态 区域
     */
    private RelativeLayout rlCallStatus;

    /**
     * 呼叫状态 文字
     */
    private TextView tvCallStatus;

    /**
     * 客服信息
     */
    private TextView tvInfo;

    /**
     * 计时器
     */
    private Chronometer timer;

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
        tvHangUp = mRootView.findViewById(R.id.tv_hang_up);
        rlCallStatus = mRootView.findViewById(R.id.rl_call_status);
        tvCallStatus = mRootView.findViewById(R.id.tv_call_status);
        tvInfo = mRootView.findViewById(R.id.tv_info);
        timer = mRootView.findViewById(R.id.timer);

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

//        setSwappedFeeds(false);

        startCall();
    }

    private void startCall() {
        manager = WebRtcManager.getInstance();
        manager.setCallback(new IViewCallback() {
            @Override
            public void onSetLocalStream(MediaStream stream) {
                Log.e(TAG, "onSetLocalStream");
//                if (stream.videoTracks.size() > 0) {
//                    stream.videoTracks.get(0).addSink(localRender);
//                }
//
//                if (videoEnable) {
//                    stream.videoTracks.get(0).setEnabled(true);
//                }
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
                //通话通道关闭
                disconnect();
            }
        });

    }

    private void setSwappedFeeds(boolean isSwappedFeeds) {
        localRender.setTarget(isSwappedFeeds ? remote_view : local_view);
        remoteRender.setTarget(isSwappedFeeds ? local_view : remote_view);
    }

    @Override
    protected void onBindListener() {
        //挂断 监听
        tvHangUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //断开连接
                disconnect();
            }
        });
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

    /**
     * 展示呼叫状态界面
     * @param status callStatusIng-呼叫中， callStatusQueue-排队中
     */
    public void showCallStatus(String status) {
        rlCallStatus.setVisibility(View.VISIBLE);
        tvInfo.setVisibility(View.GONE);
        timer.setVisibility(View.GONE);
        switch (status) {
            case CallConfigs.CALL_STATUS_ING:
                //呼叫中
                tvCallStatus.setText("当前正在呼叫中...");
                break;
            case CallConfigs.CALL_STATUS_QUEUE:
                //排队中
                tvCallStatus.setText("当前正在排队中...");
                break;
            case CallConfigs.CALL_STATUS_SOON:
                //即将接通 倒计时提示文字
                long count = 5L;
                Flowable.interval(0, 1, TimeUnit.SECONDS)
                        .onBackpressureBuffer()
                        .take(count)
                        .map(aLong -> count - aLong)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<Long>() {
                            @Override
                            public void onSubscribe(Subscription s) {
                                //设置请求次数
                                s.request(Long.MAX_VALUE);
                            }

                            @Override
                            public void onNext(Long aLong) {
                                //倒计时过程中
                                Log.e(TAG, "aLong = " + aLong);
                                tvCallStatus.setText("即将接通..." + (aLong - 1) + "s");
                            }

                            @Override
                            public void onError(Throwable t) {
                                t.printStackTrace();
                            }

                            @Override
                            public void onComplete() {
                                //倒计时结束 隐藏呼叫状态界面 显示客服信息+通话时间
                                Log.e(TAG, "onComplete");
                                rlCallStatus.setVisibility(View.GONE);
                                tvInfo.setVisibility(View.VISIBLE);
                                timer.setVisibility(View.VISIBLE);
                                //计时器清零+开始
                                timer.setBase(SystemClock.elapsedRealtime());
                                timer.start();
                            }
                        });

                break;
            case CallConfigs.CALL_STATUS_HANG_UP:
                //挂断
                disconnect();
                break;
        }
    }

    /**
     * 断开连接
     */
    private void disconnect() {
        manager.exitCall();
        if (localRender != null) {
            localRender.setTarget(null);
            localRender = null;
        }
        if (remoteRender != null) {
            remoteRender.setTarget(null);
            remoteRender = null;
        }

        if (local_view != null) {
            local_view.release();
            local_view = null;
        }
        if (remote_view != null) {
            remote_view.release();
            remote_view = null;
        }

        //计时器结束
        timer.stop();

        hide(null);
        instance = null;
    }

}
