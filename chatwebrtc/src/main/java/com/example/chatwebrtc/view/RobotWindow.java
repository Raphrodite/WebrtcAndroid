package com.example.chatwebrtc.view;

import android.content.Context;
import android.view.View;

import com.example.chatwebrtc.R;

/**
 * * Copyright * 圣通电力
 *
 * @ProjectName: WebrtcAndroid
 * @Package: com.example.chatwebrtc.view
 * @ClassName:
 * @Description: 机器人弹窗
 * @Author: Raphrodite
 * @CreateDate: 2023/5/18
 */
public class RobotWindow extends BaseFloatingWindow {

    private static RobotWindow instance;

    public static RobotWindow getInstance(Context context) {
        if (instance == null) {
            instance = new RobotWindow(context);
        }
        return instance;
    }

    public RobotWindow(Context context) {
        super(context);
    }

    @Override
    protected int setLayoutId() {
        return R.layout.layout_robot;
    }

    @Override
    protected void initView(View mRootView) {

    }

    @Override
    protected void onBindListener() {
        mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfirmCallWindow.getInstance(mContext).showMatch(null);
            }
        });
    }
}
