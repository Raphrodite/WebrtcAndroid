package com.example.chatwebrtc.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.chatwebrtc.R;

/**
 * * Copyright * 圣通电力
 *
 * @ProjectName: WebrtcAndroid
 * @Package: com.example.chatwebrtc.dialog
 * @ClassName:
 * @Description: 呼叫客服确认弹窗
 * @Author: Raphrodite
 * @CreateDate: 2023/5/17
 */
public class CallConfirmDialog extends Dialog {

    /**
     * 上下文
     */
    private Context context;

    /**
     * 初始化
     * @param context
     */
    public CallConfirmDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public CallConfirmDialog showDialog(CallConfirmDialog dialog) {
        //dialog布局文件
        dialog.setContentView(R.layout.dialog_call_confirm);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        //取消按钮
        TextView tvCancel = findViewById(R.id.tv_cancel);
        //确认按钮
        TextView tvConfirm = findViewById(R.id.tv_confirm);

        //取消 监听
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        //确认 监听
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onConfirmListener != null) {
                    onConfirmListener.onConfirm();
                }
            }
        });

        dialog.show();

        return dialog;
    }

    public interface OnConfirmListener {

        /**
         * 确认
         */
        void onConfirm();
    }

    private OnConfirmListener onConfirmListener;

    public void setOnConfirmListener(OnConfirmListener onConfirmListener) {
        this.onConfirmListener = onConfirmListener;
    }
}
