package com.example.chatwebrtc.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.example.chatwebrtc.IViewCallback;
import com.example.chatwebrtc.R;
import com.example.chatwebrtc.bean.MouseEventBean;
import com.example.chatwebrtc.control.AccessibilityOperator;
import com.example.chatwebrtc.control.BRScreenManagerUtils;
import com.example.chatwebrtc.utils.ActionConfigs;
import com.example.chatwebrtc.utils.CallConfigs;
import com.example.chatwebrtc.utils.Utils;
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
public class CallChatWindow extends BaseFloatingWindow {

    /**
     * Log TAG
     */
    public final static String TAG = "CallNewWindow_zrzr";

    /**
     * 呼叫弹窗 单例
     */
    private static CallChatWindow instance;

    /**
     * 控件，继承于SurfaceView的渲染View，提供了OpenGL渲染图像数据的功能，加载本地和远端
     */
    private SurfaceViewRenderer local_view, remote_view, draw_view;

    /**
     * ProxyVideoSink 本地、远端
     */
    private ProxyVideoSink localRender, remoteRender, drawRender;

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
     * 挂断按钮;静音按钮
     */
    private ImageView ivHangUp, ivMute;

    /**
     * 呼叫状态 区域; 没有客服摄像头 区域
     */
    private RelativeLayout rlCallStatus, rlNoCamera;

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

    /**
     * web发送的涂鸦图片
     */
    private ImageView ivImage;

    private boolean enableMic = true;

    public static CallChatWindow getInstance(Context context) {
        if (instance == null) {
            mActivity = (Activity) context;
            instance = new CallChatWindow(context);
        }
        return instance;
    }

    public CallChatWindow(Context context) {
        super(context);
    }

    @Override
    protected int setLayoutId() {
        return R.layout.layout_call_chat;
    }

    @Override
    protected void initView(View mRootView) {
        local_view = mRootView.findViewById(R.id.local_view_render);
        remote_view = mRootView.findViewById(R.id.remote_view_render);
        draw_view = mRootView.findViewById(R.id.draw_render);
        ivHangUp = mRootView.findViewById(R.id.iv_hang_up);
        ivMute = mRootView.findViewById(R.id.iv_mute);
        rlCallStatus = mRootView.findViewById(R.id.rl_call_status);
        tvCallStatus = mRootView.findViewById(R.id.tv_call_status);
        tvInfo = mRootView.findViewById(R.id.tv_info);
        timer = mRootView.findViewById(R.id.timer);
        rlNoCamera = mRootView.findViewById(R.id.rl_no_camera);
        ivImage = mRootView.findViewById(R.id.iv_image);

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
        //画笔掩盖图像 初始化
        draw_view.init(rootEglBase.getEglBaseContext(), null);
        draw_view.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        draw_view.setZOrderMediaOverlay(true);
        draw_view.setMirror(true);
        drawRender = new ProxyVideoSink();

//        setSwappedFeeds(false);

        startCall();
    }

    private void startCall() {
        manager = WebRtcManager.getInstance();
        manager.setCallback(new IViewCallback() {
            @Override
            public void onSetLocalStream(MediaStream stream) {
                //设置本地
                Log.e(TAG, "onSetLocalStream");
                if (stream.videoTracks.size() > 0) {
                    stream.videoTracks.get(0).addSink(localRender);
                }

                if (videoEnable) {
                    stream.videoTracks.get(0).setEnabled(true);

                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            local_view.setVisibility(View.GONE);
                        }
                    });
                }
            }

            @Override
            public void onSetScreenStream(MediaStream stream) {

            }

            @Override
            public void onAddRemoteStream(MediaStream stream) {
                //设置远程
                Log.e(TAG, "onAddRemoteStream");
                Log.e(TAG, "stream size = " + stream.videoTracks.size());
                if (stream.videoTracks.size() > 0) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            rlNoCamera.setVisibility(View.GONE);
                        }
                    });
                    stream.videoTracks.get(0).addSink(remoteRender);

                    stream.videoTracks.get(0).addSink(drawRender);
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            draw_view.setVisibility(View.GONE);
                        }
                    });

                    stream.videoTracks.get(0).setEnabled(true);
                }
                if (videoEnable) {
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
        drawRender.setTarget(isSwappedFeeds ? remote_view : draw_view);
    }

    @Override
    protected void onBindListener() {
        //挂断 监听
        ivHangUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HangUpWindow.getInstance(mContext).showMatch(null);
            }
        });

        //静音 监听
        ivMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableMic = !enableMic;
                if (enableMic) {
                    ivMute.setImageResource(R.drawable.webrtc_mute_default);
//                    Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.webrtc_mute_default);
//                    if (drawable != null) {
//                        drawable.setBounds(0, 0, Utils.dip2px(mContext, 60), Utils.dip2px(mContext, 60));
//                    }
//                    ivMute.setCompoundDrawables(null, drawable, null, null);
                } else {
                    ivMute.setImageResource(R.drawable.webrtc_mute);
//                    Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.webrtc_mute);
//                    if (drawable != null) {
//                        drawable.setBounds(0, 0, Utils.dip2px(mContext, 60), Utils.dip2px(mContext, 60));
//                    }
//                    ivMute.setCompoundDrawables(null, drawable, null, null);
                }
                manager.toggleMute(enableMic);
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
        Log.e("zrzr", "showTopRight---");
        manager.sendQueue(mContext, rootEglBase, captureIntent);
    }

    /**
     * 展示呼叫状态界面
     * @param status callStatusIng-呼叫中， callStatusQueue-排队中
     */
    public void showCallStatus(String status) {
        rlCallStatus.setVisibility(View.VISIBLE);
        tvInfo.setVisibility(View.GONE);
        timer.setVisibility(View.GONE);
        ivImage.setImageBitmap(null);
        ivImage.setVisibility(View.GONE);
        draw_view.setVisibility(View.GONE);
        switch (status) {
            case CallConfigs.CALL_STATUS_ING:
                //呼叫中
                tvCallStatus.setText("匹配客服中...");
                break;
            case CallConfigs.CALL_STATUS_MATCH:
                //匹配客服应答-已匹配到客服
                tvCallStatus.setText("已匹配到客服...正在发起通话");
                break;
            case CallConfigs.CALL_STATUS_HANG_UP:
                //挂断
                disconnect();
                break;
        }
    }

    /**
     * 展示呼叫状态界面--目前仅是排队中
     * @param status callStatusIng-呼叫中， callStatusQueue-排队中
     */
    public void showCallStatusQueue(String status, int queueCount) {
        rlCallStatus.setVisibility(View.VISIBLE);
        tvInfo.setVisibility(View.GONE);
        timer.setVisibility(View.GONE);
        switch (status) {
            case CallConfigs.CALL_STATUS_WAIT:
                //匹配客服应答-未匹配到客服需等待
                tvCallStatus.setText("等待匹配客服中..." + "前面排队人数" + queueCount + "人。");
                break;
        }
    }

    /**
     * 呼叫接听 接入方式
     * @param status
     * @param callType 通话类型：AUDIO 音频、VIDEO 视频
     */
    public void showCallStatusCallType(String status, String callType) {
        rlCallStatus.setVisibility(View.VISIBLE);
        tvInfo.setVisibility(View.GONE);
        timer.setVisibility(View.GONE);
        switch (status) {
            case CallConfigs.CALL_STATUS_SOON:
                String callTypeText = callType.equals("AUDIO") ? "语音接入" : "视频接入";
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
                                tvCallStatus.setText("即将接通..." + (aLong - 1) + "s" + "..." + callTypeText);
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
                                //语音接入 不显示客服摄像头
                                if (callType.equals("AUDIO")) {
                                    rlNoCamera.setVisibility(View.VISIBLE);
                                }
                            }
                        });

                break;
        }
    }

    /**
     * 切换通话方式
     * @param beforeCallType 变更前通话类型 AUDIO、VIDEO
     * @param afterCallType 变更前通话类型 AUDIO、VIDEO
     */
    public void showChangeCallType(String beforeCallType, String afterCallType) {
        ChangeCallTypeWindow.getInstance(mContext).showMatch(null);
        ChangeCallTypeWindow.getInstance(mContext).showChangeCallTypeText(beforeCallType, afterCallType);
    }

    /**
     * 切换通话方式结果
     * @param result REFUSE-拒绝 AGREE-同意
     */
    public void changeCallTypeResult(String result) {
        manager.sendChangeCallTypeAck(result);
    }

    /**
     * 切换通话方式取消
     */
    public void showChangeCallTypeCancal() {
        ChangeCallTypeWindow.getInstance(mContext).hide(null);
    }

    /**
     * 自定义消息 摄像头的切换
     * @param action
     */
    public void showAction(String action) {
        switch (action) {
            case ActionConfigs.ACTION_OPEN_VIDEO:
                //摄像头打开
                rlNoCamera.setVisibility(View.GONE);
                break;
            case ActionConfigs.ACTION_CLOSE_VIDEO:
                //摄像头关闭
                rlNoCamera.setVisibility(View.VISIBLE);
                break;
            case ActionConfigs.ACTION_OPEN_DRAW:
                //开启涂鸦
                Toast.makeText(mContext, "开启涂鸦", Toast.LENGTH_LONG).show();
                break;
            case ActionConfigs.ACTION_CLOSE_DRAW:
                //关闭涂鸦
                Toast.makeText(mContext, "关闭涂鸦", Toast.LENGTH_LONG).show();
                // 清空图片
                ivImage.setImageBitmap(null);
                ivImage.setVisibility(View.GONE);
                draw_view.setVisibility(View.GONE);
                break;
            case ActionConfigs.ACTION_OPEN_CONTROLLER:
                //开启远程控制
                Toast.makeText(mContext, "开启远程控制", Toast.LENGTH_LONG).show();
                break;
            case ActionConfigs.ACTION_CLOSE_CONTROLLER:
                //关闭远程控制
                Toast.makeText(mContext, "关闭远程控制", Toast.LENGTH_LONG).show();
                break;
            default:

                break;
        }
    }

    /**
     * 展示web发送的图片
     * @param imageStr
     */
    public void showImage(String imageStr) {
        Log.e(TAG, "showImage imageStr = " + imageStr);
        String base64Image = imageStr.split(",")[1];
        byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        ivImage.setImageBitmap(bitmap);
        ivImage.setVisibility(View.VISIBLE);
        draw_view.setVisibility(View.VISIBLE);
    }

    public void showPoint(MouseEventBean mouseEventBean) {
        String mouseEventType = mouseEventBean.getType();

        //远程屏幕宽高
        int screenwidth = mouseEventBean.getScreenSize().getWidth();
        int screenheight = mouseEventBean.getScreenSize().getHeight();
        //本地设备宽高
        int screenRealWidth = BRScreenManagerUtils.getScreenRealWidth(mContext);
        int screenRealHeight = BRScreenManagerUtils.getScreenRealHeight(mContext);
        int statusBarHeight = BRScreenManagerUtils.getStatusBarHeight(mContext);
        //获取pc端显示的宽度
        int pcWidth = (int)(((double)screenheight / (double)screenRealHeight)*screenRealWidth);

        //pc居中原点屏幕开始位置
        int startWidth=(screenwidth-pcWidth)/2;

        int dx = mouseEventBean.getStartPoint().getPointX();
        int dy = mouseEventBean.getStartPoint().getPointY();

        if ("CLICK".equals(mouseEventType)) {
            //点击
            int x=((dx-startWidth) * screenRealWidth) / (screenwidth-(startWidth*2));
            //云终端获取状态栏数据不对直接写死1920
            // int y=dy1 * (screenRealHeight + statusBarHeight) / screenheight;
            int y=dy * (1920) / screenheight;

            AccessibilityOperator.getInstance().dispatchGestureClick(x, y);
        } else if ("DRAG".equals(mouseEventType)) {
            //滑动 有结束点坐标
            int dx_end = mouseEventBean.getEndPoint().getPointX();
            int dy_end = mouseEventBean.getEndPoint().getPointY();

            int x1=((dx-startWidth) * screenRealWidth) / (screenwidth-(startWidth*2));
            int y1=dy * (screenRealHeight + statusBarHeight) / screenheight;

            int x2=((dx_end-startWidth) * screenRealWidth) / (screenwidth-(startWidth*2));
            int y2=dy_end * (screenRealHeight + statusBarHeight) / screenheight;
            AccessibilityOperator.getInstance().dispatchGestureSlide(x1, y1, x2, y2);
        }
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, "连接已断开，通话关闭", Toast.LENGTH_LONG).show();
            }
        });
        manager.exitCall();
        if (localRender != null) {
            localRender.setTarget(null);
            localRender = null;
        }
        if (remoteRender != null) {
            remoteRender.setTarget(null);
            remoteRender = null;
        }
        if (drawRender != null) {
            drawRender.setTarget(null);
            drawRender = null;
        }

        if (local_view != null) {
            local_view.release();
            local_view = null;
        }
        if (remote_view != null) {
            remote_view.release();
            remote_view = null;
        }
        if (draw_view != null) {
            draw_view.release();
            draw_view = null;
        }

        //计时器结束
        timer.stop();

        hide(new OnHideListener() {
            @Override
            public void onHideFinish(int marginRight, int marginBottom) {
                RobotWindow.getInstance(mContext).show(0, 0, null);
            }
        });
        instance = null;
    }

}
