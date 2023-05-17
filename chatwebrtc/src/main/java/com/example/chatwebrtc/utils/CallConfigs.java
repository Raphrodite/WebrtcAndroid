package com.example.chatwebrtc.utils;

/**
 * * Copyright * 圣通电力
 *
 * @ProjectName: WebrtcAndroid
 * @Package: com.example.chatwebrtc.utils
 * @ClassName:
 * @Description: 呼叫常量
 * @Author: Raphrodite
 * @CreateDate: 2023/5/17
 */
public class CallConfigs {

    /**
     * websocket连接成功之后，状态-呼叫中
     */
    public static final String CALL_STATUS_ING = "callStatusIng";

    /**
     * websocket连接成功之后，状态-排队中
     */
    public static final String CALL_STATUS_QUEUE = "callStatusQueue";

    /**
     * websocket连接成功之后，状态-即将接通
     */
    public static final String CALL_STATUS_SOON = "callStatusSoon";

    /**
     * websocket连接成功之后，状态-挂断
     */
    public static final String CALL_STATUS_HANG_UP = "callStatusHangUp";
}
