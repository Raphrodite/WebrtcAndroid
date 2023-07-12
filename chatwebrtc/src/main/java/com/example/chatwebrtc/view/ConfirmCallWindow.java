package com.example.chatwebrtc.view;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.chatwebrtc.R;
import com.example.chatwebrtc.control.AccessibilityOpenHelperActivity;
import com.example.chatwebrtc.control.AccessibilityUtil;
import com.example.chatwebrtc.control.SimulatedClickService;
import com.example.chatwebrtc.utils.CommonUtil;

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
        return R.layout.layout_confirm_call_window;
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

                CommonUtil.openServicePermissonRoot(mContext, SimulatedClickService.class);
                if (!AccessibilityUtil.isAccessibilitySettingsOn(mContext)) {
                    Log.e("zrzr", "isAccessibilitySettingsOn");
                    Intent intent = new Intent(mContext,  AccessibilityOpenHelperActivity.class);
                    intent.putExtra("action", "action_start_accessibility_setting");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                } else {
                    hide(null);
                    if (onConfirmListener != null) {
                        onConfirmListener.onConfirm();
                    }
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
