package com.example.webrtcandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.webrtcandroid.databinding.ActivityMainBinding;
import com.example.webrtcandroid.webrtc.WebRtcUtil;
import com.example.webrtcandroid.websocket.WebSocketManager;

public class MainActivity extends AppCompatActivity {

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
                WebRtcUtil.callVideo(MainActivity.this, "", true, true);
            }
        });

//        binding.wv.setWebViewClient(new WebViewClient() {
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                return false;
//            }
//        });
//        binding.wv.loadUrl("https://www.baidu.com/");
    }
}