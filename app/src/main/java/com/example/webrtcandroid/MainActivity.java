package com.example.webrtcandroid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.chatwebrtc.dialog.CallConfirmDialog;
import com.example.chatwebrtc.view.RobotWindow;
import com.example.chatwebrtc.webrtc.WebRtcUtil;
import com.example.webrtcandroid.databinding.ActivityMainBinding;

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

        //呼叫客服
        binding.tvCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //出现弹窗提示
                CallConfirmDialog callConfirmDialog = new CallConfirmDialog(MainActivity.this);
                callConfirmDialog.showDialog(callConfirmDialog);
                callConfirmDialog.setOnConfirmListener(new CallConfirmDialog.OnConfirmListener() {
                    @Override
                    public void onConfirm() {
                        //弹窗消失
                        callConfirmDialog.dismiss();
                        //进行通话 首先要判断是否拥有屏幕共享权限
                        if (!PermissionUtil.isNeedRequestPermission(MainActivity.this)) {
                            //屏幕共享权限
                            permissionCheckForProjection();
                        }
                    }
                });

//                RobotWindow.getInstance(MainActivity.this).show(0, 0, null);
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
        //权限回调 发起屏幕共享请求
        permissionCheckForProjection();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PROJECTION_REQUEST_CODE && resultCode == RESULT_OK) {
            //屏幕共享回调 获取data
            Intent captureIntent = data;
            //创建VideoCapture
            createVideoCapture(captureIntent);
        }
    }

    /**
     * 创建VideoCapture
     */
    private void createVideoCapture(Intent captureIntent) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            VideoCapturer videoCapturer = new ScreenCapturerAndroid(captureIntent,
                    new MediaProjection.Callback() {
                        @Override
                        public void onStop() {
                            super.onStop();
                        }
                    });

            //用得到的captureIntent 创建屏幕共享视频流 开启通话
            WebRtcUtil.callNewWindow(MainActivity.this, "", true, captureIntent);
        }
    }
}