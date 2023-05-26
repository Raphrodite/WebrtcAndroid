package com.example.chatwebrtc.view;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.example.chatwebrtc.R;

/**
 * * Copyright * 圣通电力
 *
 * @ProjectName: WebrtcAndroid
 * @Package: com.example.chatwebrtc.view
 * @ClassName:
 * @Description: 呼叫确认弹窗
 * @Author: Raphrodite
 * @CreateDate: 2023/5/18
 */
public class ConfirmCallWindow extends BaseFloatingWindow {

    private static ConfirmCallWindow instance;

    private TextView tvCancel, tvConfirm;

    public static ConfirmCallWindow getInstance(Context context) {
        if (instance == null) {
            instance = new ConfirmCallWindow(context);
        }
        return instance;
    }

    public ConfirmCallWindow(Context context) {
        super(context);
    }

    @Override
    protected int setLayoutId() {
        return R.layout.confirm_call_window_layout;
    }

    @Override
    protected void initView(View mRootView) {
        tvCancel = mRootView.findViewById(R.id.tv_cancel);
        tvConfirm = mRootView.findViewById(R.id.tv_confirm);
    }

    @Override
    protected void onBindListener() {
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hide(null);
                if(onConfirmListener != null) {
                    onConfirmListener.onConfirm();
                }
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hide(null);
                RobotWindow.getInstance(mContext).show(0, 0, null);
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

    public interface OnConfirmListener {
        void onConfirm();
    }

    private OnConfirmListener onConfirmListener;

    public void setOnConfirmListener(OnConfirmListener onConfirmListener) {
        this.onConfirmListener = onConfirmListener;
    }
}
