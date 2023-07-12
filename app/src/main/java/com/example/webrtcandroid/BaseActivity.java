package com.example.webrtcandroid;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONObject;
import com.example.chatwebrtc.view.ConfirmCallWindow;
import com.example.chatwebrtc.view.RobotWindow;
import com.example.chatwebrtc.webrtc.WebRtcUtil;
import com.example.chatwebrtc.bean.LoginUseBean;
import com.example.chatwebrtc.bean.JsonResult;
import com.example.chatwebrtc.http.OkhttpUtils;
import com.google.gson.Gson;

import org.webrtc.ScreenCapturerAndroid;
import org.webrtc.VideoCapturer;

import java.util.HashMap;
import java.util.Map;

/**
 * * Copyright * 圣通电力
 *
 * @ProjectName: WebrtcAndroid
 * @Package: com.example.webrtcandroid
 * @ClassName:
 * @Description: Activity基类
 * @Author: Raphrodite
 * @CreateDate: 2023/5/24
 */
public class BaseActivity extends AppCompatActivity {

    /**
     * 屏幕共享常量
     */
    public static final int PROJECTION_REQUEST_CODE = 100;

    /**
     * Token
     */
    private String token;

    /**
     * 客户登录返回的数据
     */
    private LoginUseBean bean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
    }

    private void init() {
        ConfirmCallWindow.getInstance(BaseActivity.this).setOnConfirmListener(new ConfirmCallWindow.OnConfirmListener() {
            @Override
            public void onConfirm() {
                Map<String, Object> map = new HashMap<>();
                map.put("deviceId", "789");
                //JSONObject
                JSONObject object = new JSONObject(map);
                //转化为json字符串
                String jsonString = object.toJSONString();
                //客户登录接口
                OkhttpUtils.getInstance().stringPost(BaseActivity.this, "/login/use", jsonString, new OkhttpUtils.ICallBack() {
                    @Override
                    public void onResponse(JsonResult result) {
                        //回调 获取token
                        bean = new Gson().fromJson(result.getData(), LoginUseBean.class);

                        //进行通话 首先要判断是否拥有屏幕共享权限
                        if (!PermissionUtil.isNeedRequestPermission(BaseActivity.this)) {
                            //屏幕共享权限
                            permissionCheckForProjection();
                        }
                    }

                    @Override
                    public void onFailure(String message) {
                        //重新展示客服机器人弹窗
                        RobotWindow.getInstance(BaseActivity.this).show(0, 0, null);
                    }
                });
            }
        });
    }

    /**
     * 向系统发起屏幕截取请求
     */
    public void permissionCheckForProjection(){
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
        } else {
            //展示客服悬浮窗
            RobotWindow.getInstance(this).show(0, 0, null);
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

            if (bean != null) {
                //用得到的captureIntent 创建屏幕共享视频流 开启通话
                WebRtcUtil.callNewWindow(this, "", true, captureIntent, bean);
            } else {
                Log.e("zrzr", "bean==null");
            }
        }
    }

}
