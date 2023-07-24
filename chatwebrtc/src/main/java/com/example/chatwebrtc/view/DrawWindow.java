package com.example.chatwebrtc.view;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.example.chatwebrtc.R;
import com.example.chatwebrtc.utils.ActionConfigs;

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
public class DrawWindow extends BaseFloatingWindow{

    private static DrawWindow instance;

    private TextView tvConfirm;

    private TextView tvCancel;

    public static DrawWindow getInstance(Context context) {
        if (instance == null) {
            instance = new DrawWindow(context);
        }
        return instance;
    }

    public DrawWindow(Context context) {
        super(context);
    }

    @Override
    protected int setLayoutId() {
        return R.layout.layout_draw_window;
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
                CallChatWindow.getInstance(mContext).sendCustomAction(ActionConfigs.DRAW_CANCEL);
            }
        });
        //确认 监听
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide(null);
                CallChatWindow.getInstance(mContext).sendCustomAction(ActionConfigs.DRAW_AGREE);
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
