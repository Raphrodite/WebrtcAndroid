package com.example.chatwebrtc.utils;

/**
 * * Copyright * 圣通电力
 *
 * @ProjectName: WebrtcAndroid
 * @Package: com.example.chatwebrtc.utils
 * @ClassName:
 * @Description:
 * @Author: Raphrodite
 * @CreateDate: 2023/7/11
 */
public class ActionConfigs {

    // ===================================WebSocket 接收的ACTION start=======================================

    /**
     * 打开摄像头
     */
    public static final String ACTION_OPEN_VIDEO = "OPEN_VIDEO";

    /**
     * 关闭摄像头
     */
    public static final String ACTION_CLOSE_VIDEO = "CLOSE_VIDEO";

    /**
     * 开启画笔
     */
    public static final String ACTION_OPEN_DRAW = "OPEN_DRAW";

    /**
     * 关闭画笔
     */
    public static final String ACTION_CLOSE_DRAW = "CLOSE_DRAW";

    /**
     * 画笔-发送图片
     */
    public static final String ACTION_SEND_IMAGE = "SEND_IMAGE";

    /**
     * 开启远程控制
     */
    public static final String ACTION_OPEN_CONTROLLER = "OPEN_CONTROLLER";

    /**
     * 关闭远程控制
     */
    public static final String ACTION_CLOSE_CONTROLLER = "CLOSE_CONTROLLER";

    /**
     * 远程控制发送坐标
     */
    public static final String ACTION_SEND_POINT = "SEND_POINT";

    /**
     * 开启涂鸦-请求
     */
    public static final String ACTION_CALL_DRAW = "CALL_DRAW";

    /**
     * 涂鸦-取消请求
     */
    public static final String ACTION_CANCEL_DRAW_REQUEST = "CANCEL_DRAW_REQUEST";

    /**
     * 远程控制-请求
     */
    public static final String ACTION_CALL_CONTROLLER = "CALL_CONTROLLER";

    /**
     * 远程控制-取消请求
     */
    public static final String ACTION_CANCEL_CONTROLLER_REQUEST = "CANCEL_CONTROLLER_REQUEST";

    /**
     * 视频通话-视频接入-客服摄像头关闭
     */
    public static final String ACTION_SERVICE_VIDEO_CLOSE = "SERVICE_VIDEO_CLOSE";

    /**
     * 视频通话-视频接入-客服摄像头打开
     */
    public static final String ACTION_SERVICE_VIDEO_OPEN = "SERVICE_VIDEO_OPEN";

    // ===================================WebSocket 接收的ACTION end=======================================

    // ===================================WebSocket 发送的ACTION start=======================================

    /**
     * 切换通话方式-拒绝
     */
    public static final String CHANGE_CALL_TYPE_REFUSE = "REFUSE";

    /**
     * 切换通话方式-同意
     */
    public static final String CHANGE_CALL_TYPE_AGREE = "AGREE";

    /**
     * 开启画笔-取消
     */
    public static final String DRAW_CANCEL = "CANCEL_DRAW";

    /**
     * 开启画笔-同意
     */
    public static final String DRAW_AGREE = "AGREE_DRAW";

    /**
     * 远程控制-取消
     */
    public static final String CANCEL_CONTROLLER = "CANCEL_CONTROLLER";

    /**
     * 远程控制-同意
     */
    public static final String AGREE_CONTROLLER = "AGREE_CONTROLLER";

    // ===================================WebSocket 发送的ACTION end=======================================
}
