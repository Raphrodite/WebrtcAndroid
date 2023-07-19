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
 * @Description: 是否挂断 悬浮窗
 * @Author: Raphrodite
 * @CreateDate: 2023/7/17
 */
public class HangUpWindow extends BaseFloatingWindow{
    private TextView tvConfirm;

    private TextView tvCancel;

    private static HangUpWindow instance;

    public static HangUpWindow getInstance(Context context) {
        if (instance == null) {
            instance = new HangUpWindow(context);
        }
        return instance;
    }

    public HangUpWindow(Context context) {
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
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide(null);
            }
        });
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CallChatWindow.getInstance(mContext).disconnect();
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
