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
 * @Description: 改变接入方式 悬浮窗
 * @Author: Raphrodite
 * @CreateDate: 2023/7/19
 */
public class ChangeCallTypeWindow extends BaseFloatingWindow{

    private static ChangeCallTypeWindow instance;

    /**
     * 取消，确认，文字
     */
    private TextView tvCancel, tvConfirm, tv;

    public static ChangeCallTypeWindow getInstance(Context context) {
        if (instance == null) {
            instance = new ChangeCallTypeWindow(context);
        }
        return instance;
    }

    public ChangeCallTypeWindow(Context context) {
        super(context);
    }

    @Override
    protected int setLayoutId() {
        return R.layout.layout_change_call_type;
    }

    @Override
    protected void initView(View mRootView) {
        mRootView.setOnTouchListener(null);
        mRootView.setBackgroundColor(Color.parseColor("#33333333"));
        tvCancel = mRootView.findViewById(R.id.tv_cancel);
        tvConfirm = mRootView.findViewById(R.id.tv_confirm);
        tv = mRootView.findViewById(R.id.tv);
    }

    @Override
    protected void onBindListener() {
        //取消 监听
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hide(null);
                CallChatWindow.getInstance(mContext).changeCallTypeResult(ActionConfigs.CHANGE_CALL_TYPE_REFUSE);
            }
        });
        //确认 监听
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hide(null);
                CallChatWindow.getInstance(mContext).changeCallTypeResult(ActionConfigs.CHANGE_CALL_TYPE_AGREE);
            }
        });
    }

    /**
     * 展示悬浮窗的文字
     * @param beforeCallType
     * @param afterCallType
     */
    public void showChangeCallTypeText(String beforeCallType, String afterCallType) {
        String beforeText = beforeCallType.equals("AUDIO") ? "语音接入" : "视频接入";
        String afterText = afterCallType.equals("AUDIO") ? "语音接入" : "视频接入";

        tv.setText(beforeText + " 切换到 " + afterText + ", 是否同意?");
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
