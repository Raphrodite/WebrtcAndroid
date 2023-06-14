package com.example.chatwebrtc.websocket;

import static com.example.chatwebrtc.webrtc.WebRtcUtil.TOKEN;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.chatwebrtc.bean.VideoInfoBean;

import org.webrtc.IceCandidate;

import java.util.HashMap;
import java.util.List;
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

    /**
     * 心跳报文
     *  type 固定为HEART
     *  id 登录接口返回得token
     * @return
     */
    public static String getHeartMapByToken() {
        Map<String, Object> map = new HashMap<>();
        map.put("type", "HEART");
        map.put("id", TOKEN);
        return mapToString(map);
    }

    /**
     * 匹配客户报文
     * type 固定为QUEUE
     * id 登录获取的token
     * againQueue 重新排队时传1 （客服长时间未接听、客服拒接）
     * @return
     */
    public static String getQueueMapByToken(String againQueue) {
        Map<String, Object> map = new HashMap<>();
        map.put("type", "QUEUE");
        map.put("id", TOKEN);
        map.put("againQueue", againQueue);
        return mapToString(map);
    }

    /**
     * 发起通话报文
     * type 固定为CALL
     * fromId 发起方id - Token
     * @param toId 应答方id
     * @return
     */
    public static String getCallMapByToken(String toId) {
        Map<String, Object> map = new HashMap<>();
        map.put("type", "CALL");
        map.put("fromId", TOKEN);
        map.put("toId", toId);
        return mapToString(map);
    }

    /**
     * 建立通话连接报文
     * type 固定为OFFER
     * fromId 发起方id
     * toId 应答方id
     * description SDP描述
     * videoInfo array 视频轨道描述对象：mid：轨道ID，videoType：视频轨道类型 VIDEO（视频）、SCREEN（屏幕共享）
     * @return
     */
    public static String getOfferByToken(String toId, String description, List<VideoInfoBean> videoInfos) {

        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < videoInfos.size(); i++) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("mid", videoInfos.get(i).mid);
            jsonObject.put("videoType", videoInfos.get(i).videoType);
            jsonArray.add(jsonObject);
        }

        Map<String, Object> childMap = new HashMap<>();
        childMap.put("type", "offer");
        childMap.put("sdp", description);

        Map<String, Object> map = new HashMap<>();
        map.put("type", "OFFER");
        map.put("fromId", TOKEN);
        map.put("toId", toId);
        map.put("description", childMap);
        map.put("videoInfo", jsonArray);
        return mapToString(map);
    }

    /**
     * 通道信息报文
     * type 固定为ICE
     * fromId 发起方id
     * toId 应答方id
     * candidate
     * @return
     */
    public static String getIceByToken(String toId, IceCandidate iceCandidate) {
        Map<String, Object> map = new HashMap<>();
        map.put("type", "ICE");
        map.put("fromId", TOKEN);
        map.put("toId", toId);
        map.put("candidate", iceCandidate);
        return mapToString(map);
    }

    /**
     * 挂断应答报文
     * type 固定为HANGUP
     * id token
     * @return
     */
    public static String getHangUpByToken() {
        Map<String, Object> map = new HashMap<>();
        map.put("type", "HANGUP_ACK");
        map.put("id", TOKEN);
        return mapToString(map);
    }

    /**
     * 切换通话方式应答报文
     * @param toId 接收方id
     * @param ack 应答状态 AGREE同意、REFUSE拒绝
     * @return
     */
    public static String getChangeCallTypeAckByToken(String toId, String ack) {
        Map<String, Object> map = new HashMap<>();
        map.put("type", "CHANGE_CALL_TYPE_ACK");
        map.put("fromId", TOKEN);
        map.put("toId", toId);
        map.put("ack", ack);
        return mapToString(map);
    }

}
