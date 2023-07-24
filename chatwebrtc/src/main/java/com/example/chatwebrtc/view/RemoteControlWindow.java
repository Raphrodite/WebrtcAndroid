package com.example.chatwebrtc.view;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.example.chatwebrtc.R;

/**
 * * Copyright * 圣通电力
 *
 * @ProjectName: WebrtcAndroid
 * @Package: com.example.chatwebrtc.view
 * @ClassName:
 * @Description:
 * @Author: Raphrodite
 * @CreateDate: 2023/7/24
 */
public class RemoteControlWindow extends BaseFloatingWindow{

    private static RemoteControlWindow instance;

    private TextView tvConfirm;

    private TextView tvCancel;

    public static RemoteControlWindow getInstance(Context context) {
        if (instance == null) {
            instance = new RemoteControlWindow(context);
        }
        return instance;
    }

    public RemoteControlWindow(Context context) {
        super(context);
    }

    @Override
    protected int setLayoutId() {
        return R.layout.layout_hangup_window;
    }

    @Override
    protected void initView(View mRootView) {
        mRootView.setOnTouchListener(null);
        mRootView.setBackgroundColor(Color.parseColor("#33333333"));
        tvCancel = mRootView.findViewById(R.id.tv_cancel);
        tvConfirm = mRootView.findViewById(R.id.tv_confirm);
    }

    @Override
    protected void onBindListener() {
        //取消 监听
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide(null);
            }
        });
        //确认 监听
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide(null);
            }
        });

    }

    @Override
    public void showMatch(OnShowListener onShowListener) {
        super.showMatch(onShowListener);
    }

    @Override
    public boolean isSetAnimal() {
        return false;
    }

}
