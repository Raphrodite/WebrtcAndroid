package com.example.webrtcandroid;

import static com.example.webrtcandroid.webrtc.WebRtcManager.IS_SCREEN;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.webrtcandroid.databinding.ActivityMainBinding;
import com.example.webrtcandroid.view.CallNewWindow;
import com.example.webrtcandroid.webrtc.WebRtcUtil;
import com.example.webrtcandroid.websocket.WebSocketManager;

import org.webrtc.ScreenCapturerAndroid;
import org.webrtc.VideoCapturer;

public class MainActivity extends AppCompatActivity {

    /**
     * 屏幕共享常量
     */
    public static final int PROJECTION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //绑定试图
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //视频通话
        binding.tvVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebRtcUtil.callVideo(MainActivity.this, "", true, false);
            }
        });

        //屏幕共享
        binding.tvScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                WebRtcUtil.callNewWindow(MainActivity.this, "", true, false);
//                CallNewWindow.getInstance(MainActivity.this).showTopRight(null);
                Log.e("zrzr", "aaaaa");
                if (!PermissionUtil.isNeedRequestPermission(MainActivity.this)) {
                    //屏幕共享权限
                    permissionCheckForProjection();
                }
            }
        });
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                finish();
                break;
            }
        }
        Log.e("zrzr", "onRequestPermissionsResult");
        permissionCheckForProjection();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PROJECTION_REQUEST_CODE && resultCode == RESULT_OK) {
            //屏幕共享回调 获取data 创建VideoCapture
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

            IS_SCREEN = 1;

            //用得到的captureIntent 创建屏幕共享视频流 开启通话
//            CallNewWindow.getInstance(this).getVideoCapture(captureIntent);

            WebRtcUtil.callNewWindow(MainActivity.this, "", true, false, captureIntent);
        }
    }
}