package com.example.webrtcandroid;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.chatwebrtc.IViewCallback;
import com.example.chatwebrtc.webrtc.ProxyVideoSink;
import com.example.chatwebrtc.webrtc.WebRtcManager;

import org.webrtc.EglBase;
import org.webrtc.MediaStream;
import org.webrtc.RendererCommon;
import org.webrtc.ScreenCapturerAndroid;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;

/**
 * * Copyright * 圣通电力
 *
 * @ProjectName: WebrtcAndroid
 * @Package: com.example.webrtcandroid
 * @ClassName: CallVideoActivity
 * @Description: 视频通话界面
 * @Author: Raphrodite
 * @CreateDate: 2023/2/14
 */
public class CallVideoActivity extends AppCompatActivity {

    /**
     * Log TAG
     */
    public final static String TAG = "CallVideoActivity_zrzr";

    private SurfaceViewRenderer local_view;
    private SurfaceViewRenderer remote_view;
    private SurfaceViewRenderer screen_view;
    private ProxyVideoSink localRender;
    private ProxyVideoSink remoteRender;
    private ProxyVideoSink screenRender;

    private WebRtcManager manager;

    private boolean videoEnable;
    private boolean isSwappedFeeds;

    private EglBase rootEglBase;

    private int previewX, previewY;
    private int moveX, moveY;

    /**
     * 屏幕共享常量
     */
    private int PROJECTION_REQUEST_CODE = 100;

    /**
     * activity跳转
     * @param activity
     * @param videoEnable
     */
    public static void openActivity(Activity activity, boolean videoEnable) {
        Intent intent = new Intent(activity, CallVideoActivity.class);
        intent.putExtra("videoEnable", videoEnable);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_video);

        initVar();
        initListener();
    }

    private void initVar() {
        Intent intent = getIntent();
        videoEnable = intent.getBooleanExtra("videoEnable", false);

        CallVideoFragment chatSingleFragment = new CallVideoFragment();
        replaceFragment(chatSingleFragment, videoEnable);
        rootEglBase = EglBase.create();
        if (videoEnable) {
            local_view = findViewById(R.id.local_view_render);
            remote_view = findViewById(R.id.remote_view_render);
            screen_view = findViewById(R.id.screen_view_render);
            // 本地图像初始化
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
            //屏幕共享初始化
            screen_view.init(rootEglBase.getEglBaseContext(), null);
            screen_view.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
            screen_view.setZOrderMediaOverlay(true);
            screen_view.setMirror(true);
            screenRender = new ProxyVideoSink();

            setSwappedFeeds(true);

            local_view.setOnClickListener(v -> setSwappedFeeds(!isSwappedFeeds));
        }

        startCall();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initListener() {
        if (videoEnable) {
            // 设置小视频可以移动
            local_view.setOnTouchListener((view, motionEvent) -> {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        previewX = (int) motionEvent.getX();
                        previewY = (int) motionEvent.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int x = (int) motionEvent.getX();
                        int y = (int) motionEvent.getY();
                        moveX = (int) motionEvent.getX();
                        moveY = (int) motionEvent.getY();
                        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) local_view.getLayoutParams();
                        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0); // Clears the rule, as there is no removeRule until API 17.
                        lp.addRule(RelativeLayout.ALIGN_PARENT_END, 0);
                        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                        lp.addRule(RelativeLayout.ALIGN_PARENT_START, 0);
                        int left = lp.leftMargin + (x - previewX);
                        int top = lp.topMargin + (y - previewY);
                        lp.leftMargin = left;
                        lp.topMargin = top;
                        view.setLayoutParams(lp);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (moveX == 0 && moveY == 0) {
                            view.performClick();
                        }
                        moveX = 0;
                        moveY = 0;
                        break;
                }
                return true;
            });

            screen_view.setOnTouchListener((view, motionEvent) -> {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        previewX = (int) motionEvent.getX();
                        previewY = (int) motionEvent.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int x = (int) motionEvent.getX();
                        int y = (int) motionEvent.getY();
                        moveX = (int) motionEvent.getX();
                        moveY = (int) motionEvent.getY();
                        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) screen_view.getLayoutParams();
                        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0); // Clears the rule, as there is no removeRule until API 17.
                        lp.addRule(RelativeLayout.ALIGN_PARENT_END, 0);
                        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                        lp.addRule(RelativeLayout.ALIGN_PARENT_START, 0);
                        int left = lp.leftMargin + (x - previewX);
                        int top = lp.topMargin + (y - previewY);
                        lp.leftMargin = left;
                        lp.topMargin = top;
                        view.setLayoutParams(lp);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (moveX == 0 && moveY == 0) {
                            view.performClick();
                        }
                        moveX = 0;
                        moveY = 0;
                        break;
                }
                return true;
            });
        }
    }

    private void setSwappedFeeds(boolean isSwappedFeeds) {
        this.isSwappedFeeds = isSwappedFeeds;
        localRender.setTarget(isSwappedFeeds ? remote_view : local_view);
        remoteRender.setTarget(isSwappedFeeds ? local_view : remote_view);
        screenRender.setTarget(isSwappedFeeds ? remote_view : screen_view);
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
                Log.e(TAG, "onSetScreenStream");
                if (stream.videoTracks.size() > 0) {
                    stream.videoTracks.get(0).addSink(screenRender);
                }

                if (videoEnable) {
                    stream.videoTracks.get(0).setEnabled(true);
                }
            }

            @Override
            public void onAddRemoteStream(MediaStream stream) {
                Log.e(TAG, "onAddRemoteStream");
                if (stream.videoTracks.size() > 0) {
                    stream.videoTracks.get(0).addSink(remoteRender);
                }
                if (videoEnable) {
                    stream.videoTracks.get(0).setEnabled(true);

                    runOnUiThread(() -> setSwappedFeeds(false));
                }
            }

            @Override
            public void onClose() {
                runOnUiThread(() -> {
                    disConnect();
                    CallVideoActivity.this.finish();
                });
            }
        });

//        manager.sendPreCall(getApplicationContext(), rootEglBase);
        permissionCheckForProjection();
    }

    private void replaceFragment(Fragment fragment, boolean videoEnable) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("videoEnable", videoEnable);
        fragment.setArguments(bundle);
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .replace(R.id.wr_container, fragment)
                .commit();

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_BACK || super.onKeyDown(keyCode, event);
    }


    // 切换摄像头
    public void switchCamera() {
        manager.switchCamera();
    }

    // 挂断
    public void hangUp() {
        disConnect();
        this.finish();
    }

    // 静音
    public void toggleMic(boolean enable) {
        manager.toggleMute(enable);
    }

    // 扬声器
    public void toggleSpeaker(boolean enable) {
        manager.toggleSpeaker(enable);

    }

    @Override
    protected void onDestroy() {
        disConnect();
        super.onDestroy();

    }

    private void disConnect() {
        manager.exitCall();
        if (localRender != null) {
            localRender.setTarget(null);
            localRender = null;
        }
        if (remoteRender != null) {
            remoteRender.setTarget(null);
            remoteRender = null;
        }
        if (screenRender != null) {
            screenRender.setTarget(null);
            screenRender = null;
        }

        if (local_view != null) {
            local_view.release();
            local_view = null;
        }
        if (remote_view != null) {
            remote_view.release();
            remote_view = null;
        }
        if (screen_view != null) {
            screen_view.release();
            screen_view = null;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                finish();
                break;
            }
        }

//        manager.sendPreCall(getApplicationContext(), rootEglBase);
        permissionCheckForProjection();
    }

    /**
     * 向系统发起屏幕截取请求
     */
    private void permissionCheckForProjection(){
        if(Build.VERSION.SDK_INT < 21) {
            Toast.makeText(this, "您的设备不支持这个功能", Toast.LENGTH_SHORT).show();
            return;
        }
        MediaProjectionManager manager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        if(manager == null) {
            Toast.makeText(this, "截屏服务不可用", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = manager.createScreenCaptureIntent();
        startActivityForResult(intent, PROJECTION_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PROJECTION_REQUEST_CODE && resultCode == RESULT_OK) {
            Intent captureIntent = data;
            createVideoCapture(captureIntent);
        }
    }

    /**
     * 创建VideoCapture
     */
    private void createVideoCapture(Intent captureIntent) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Log.e("zrzr", "createVideoCapture");
            VideoCapturer videoCapturer = new ScreenCapturerAndroid(captureIntent,
                    new MediaProjection.Callback() {
                        @Override
                        public void onStop() {
                            super.onStop();
                        }
                    });

            //创建视频源并初始化
//            VideoSource videoSource = peerConnectionFactory.createVideoSource(videoCapturer.isScreencast());
//            videoCapturer.initialize(surfaceTextureHelper, getApplicationContext(),
//                    videoSource.getCapturerObserver());
//            videoCapturer.startCapture(480, 640, 30);


            manager.sendPreCall(getApplicationContext(), rootEglBase, captureIntent);
        }
    }

}
