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
     * websocket连接成功之后，状态-发起匹配客服请求
     */
    public static final String CALL_STATUS_ING = "callStatusIng";

    /**
     * 匹配客服应答-未匹配到客服需等待
     */
    public static final String CALL_STATUS_WAIT = "callStatusWait";

    /**
     * 匹配客服应答-匹配到客服
     */
    public static final String CALL_STATUS_MATCH = "callStatusMatch";

    /**
     * websocket连接成功之后，状态-即将接通
     */
    public static final String CALL_STATUS_SOON = "callStatusSoon";

    /**
     * websocket连接成功之后，状态-挂断
     */
    public static final String CALL_STATUS_HANG_UP = "callStatusHangUp";
}
