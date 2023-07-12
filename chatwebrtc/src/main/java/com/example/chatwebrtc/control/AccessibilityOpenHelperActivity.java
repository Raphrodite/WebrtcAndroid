package com.example.chatwebrtc.control;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.chatwebrtc.R;


/**
 * 开启辅助功能处理的透明类
 */
public class AccessibilityOpenHelperActivity extends Activity {

    private Context mContext;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accessibility_open_helper);
        mContext = AccessibilityOpenHelperActivity.this;


        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivityForResult(intent,1000);
//        CommonUtil.showStatusNavBar();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        CommonUtil.hideStatusNavBar();
        if (AccessibilityUtil.isAccessibilitySettingsOn(mContext)) {
            //Toast.makeText(AccessibilityOpenHelperActivity.this, "辅助功能已开启", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(AccessibilityOpenHelperActivity.this, "辅助功能未开启", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();



    }





}
