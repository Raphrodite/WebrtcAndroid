package com.example.chatwebrtc.websocket;

import com.alibaba.fastjson.JSONObject;
import org.webrtc.IceCandidate;

import java.util.HashMap;
import java.util.Map;

/**
 * * Copyright * 圣通电力
 *
 * @ProjectName: WebrtcAndroidDemo
 * @Package: com.example.webrtcandroiddemo.utils
 * @ClassName: WebSocketData
 * @Description: WebSocket发送的数据json字符串拼接
 * @Author: Raphrodite
 * @CreateDate: 2023/2/8
 */
public class WebSocketData {

    /**
     * 心跳连接
     * @return
     */
    public static String getHeartMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("type", "HEART");
        map.put("clientType", "ANDROID");
        map.put("id", "111");
        return mapToString(map);
    }

    /**
     * 发起预通话配置(分配对应客服)
     * @return
     */
    public static String getPreCall() {
        Map<String, Object> map = new HashMap<>();
        map.put("type", "START");
        map.put("id", "111");
        return mapToString(map);
    }

    /**
     * 正式通话
     * @param offerId 安卓客户端ID
     * @param answerId 分配的客服ID（浏览器端登录用户ID，仅当connectStatus为0时有值）
     * @return
     */
    public static String getCall(String offerId, String answerId) {
        Map<String, Object> map = new HashMap<>();
        map.put("type", "CALL");
        map.put("offerId", offerId);
        map.put("answerId", answerId);
        return mapToString(map);
    }

    /**
     * 安卓发起webrtc提案
     * @param offerId
     * @param answerId
     * @param sdp
     * @return
     */
    public static String getOffer(String offerId, String answerId, String sdp) {
        Map<String, Object> childMap = new HashMap<>();
        childMap.put("type", "offer");
        childMap.put("sdp", sdp);

        Map<String, Object> map = new HashMap<>();
        map.put("type", "OFFER");
        map.put("offerId", offerId);
        map.put("answerId", answerId);
        map.put("description", childMap);
        return mapToString(map);
    }

    /**
     * ICE交互
     * @return
     */
    public static String getIceCandidate(IceCandidate iceCandidate) {
        Map<String, Object> map = new HashMap<>();
        map.put("type", "ICE");
        map.put("fromId", "111");
        map.put("fromClientType", "ANDROID");
        map.put("toId", "111");
        map.put("candidate", iceCandidate);
        return mapToString(map);
    }

    /**
     * 将map转化为json string
     * @param map
     * @return
     */
    private static String mapToString(Map<String, Object> map) {
        //JSONObject
        JSONObject object = new JSONObject(map);
        //转化为json字符串
        String jsonString = object.toJSONString();
        //返回字符串
        return jsonString;
    }

}
