package com.example.chatwebrtc.websocket;

import static com.example.chatwebrtc.websocket.WebSocketData.getCallMapByToken;
import static com.example.chatwebrtc.websocket.WebSocketData.getChangeCallTypeAckByToken;
import static com.example.chatwebrtc.websocket.WebSocketData.getHangUpByToken;
import static com.example.chatwebrtc.websocket.WebSocketData.getHeartMapByToken;
import static com.example.chatwebrtc.websocket.WebSocketData.getIceByToken;
import static com.example.chatwebrtc.websocket.WebSocketData.getOfferByToken;
import static com.example.chatwebrtc.websocket.WebSocketData.getQueueMapByToken;

import android.os.Handler;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.example.chatwebrtc.bean.EventMessage;
import com.example.chatwebrtc.bean.MouseEventBean;
import com.example.chatwebrtc.bean.VideoInfoBean;
import com.example.chatwebrtc.utils.ActionConfigs;
import com.example.chatwebrtc.webrtc.IWebRtcEvents;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

import org.greenrobot.eventbus.EventBus;
import org.webrtc.IceCandidate;

/**
 * * Copyright * 圣通电力
 *
 * @ProjectName: WebrtcAndroid
 * @Package: com.example.webrtcandroid.websocket
 * @ClassName: WebSocketManager
 * @Description: WebSocket管理 okhttp实现
 * @Author: Raphrodite
 * @CreateDate: 2023/2/14
 */
public class WebSocketManager extends WebSocketListener implements IWebSocket {

    /**
     * Log tag
     */
    private static final String TAG = "WebSocketManager_zrzr";

    /**
     * 可替换为自己的主机名和端口号 ws://192.168.13.109:14000/wst  ws://192.168.13.14:14000/vtm/wst wss://rpc.stdlnj.cn/wst ws://192.168.13.109:14000/vtm/wst
     */
    private static final String WEBSOCKET_HOST_AND_PORT = "https://rpc.stdlnj.cn/vtm/wst";

    /**
     * 是否连接成功
     */
    private boolean isOpen;

    /**
     * WebSocket实例
     */
    private WebSocket mWebSocket;

    /**
     * webSocket连接地址
     */
    private String webSocketUrl;

    /**
     * 心跳检测时间 每隔10秒进行一次对长连接的心跳检测
     */
    private static final long HEART_BEAT_RATE = 10 * 1000;

    /**
     * 记录的发送时间 心跳时间判断参数
     */
    private long sendTime = 0L;

    /**
     * Handler 发送心跳包
     */
    private Handler mHandler = new Handler();

    /**
     * 对回调信息进行处理
     */
    private IWebRtcEvents events;

    /**
     * 客服id-websocket回调
     */
    private String callFromId = "";

    /**
     * 初始化events
     * @param events
     */
    public WebSocketManager(IWebRtcEvents events) {
        this.events = events;
    }

    /**
     * 初始化WebSocket
     * @throws Exception
     */
    private void initWebSocket() throws Exception {
        //创建okHttpClient实例
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .build();
        //创建Request实例
        Request request = new Request.Builder()
                .url(WEBSOCKET_HOST_AND_PORT)
                .build();
        //连接WebSocket
        mWebSocket = client.newWebSocket(request, this);
        //执行异步请求的线程池 停止接收新任务，原来的任务继续执行
        client.dispatcher().executorService().shutdown();
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        super.onOpen(webSocket, response);
        //开启长连接成功回调
        Log.e(TAG, "onOpen");
        isOpen = true;
        mWebSocket = webSocket;
        //回调连接成功
        events.onWebSocketOpen();
        //连接成功 发送心跳包
        boolean isSuccess = send(getHeartMapByToken());
        if (!isSuccess) {
            //初始心跳消息发送成功失败
            mHandler.removeCallbacks(heartBeatRunnable);
            //取消掉以前的长连接
            cancel();
            //创建一个新的连接
            new InitSocketThread().start();
        } else {
            //初始心跳消息发送成功 handler开启心跳检测
            mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        super.onMessage(webSocket, text);
        isOpen = true;
        //接收消息的回调
        Log.e(TAG, "WebSocket 接收的 onMessage = " + text);
        //处理接收到的消息
        handleMessage(text);
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        super.onMessage(webSocket, bytes);
        isOpen = true;
        //接收消息的回调
        Log.e(TAG, "onMessage ByteString");
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        super.onClosing(webSocket, code, reason);
        //客户端主动关闭时回调
        Log.e(TAG, "onClosing = " + reason + ", code = " + code);
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        super.onClosed(webSocket, code, reason);
        //WebSocket连接关闭
        Log.e(TAG, "onClosed");
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        super.onFailure(webSocket, t, response);
        //连接失败回调
        Log.e(TAG, "onFailure = " + t.getMessage());
        //回调连接失败
        events.onWebSocketFailed(t.toString());
    }

    //============================需要发送的 start=====================================

    /**
     * 连接WebSocket
     * @param url 连接地址
     */
    @Override
    public void connect(String url) {
        Log.e(TAG, "WebSocket connect--");
        //连接之前先关闭
        close();
        //连接地址 目前是写死的 不是传递过来的
        webSocketUrl = url;
        new InitSocketThread().start();
    }

    /**
     * 匹配客服
     * @param againQueue 重新排队时传1 （客服长时间未接听、客服拒接）
     */
    @Override
    public void sendQueue(String againQueue) {
        Log.e(TAG, "匹配客服---");
        String message = getQueueMapByToken(againQueue);
        send(message);
    }

    /**
     * 发起通话
     * @param serverId 配对到的客服id
     */
    @Override
    public void sendCall(String serverId) {
        Log.e(TAG, "发起通话---");
        String message = getCallMapByToken(serverId);
        send(message);
    }

    /**
     * 发送offer 建立通话连接
     * @param sdp sdp描述
     * @param mids mid集合
     */
    @Override
    public void sendOffer(String sdp, List<String> mids) {
        Log.e(TAG, "发送Offer---");
        List<VideoInfoBean> list = new ArrayList<>();
        for (int i = 0; i < mids.size(); i++) {
            VideoInfoBean bean = new VideoInfoBean();
            bean.mid = Integer.parseInt(mids.get(i));
            if (i == 0) {
                bean.videoType = "VIDEO";
            } else if (i == 1) {
                bean.videoType = "SCREEN";
            }
            list.add(bean);
        }
        String message = getOfferByToken(callFromId, sdp, list);
        send(message);
    }

    /**
     * 发送Ice 通道信息
     * @param iceCandidate ice通道信息描述
     */
    @Override
    public void sendIceCandidate(IceCandidate iceCandidate) {
        Log.e(TAG, "发送Ice---");
        String message = getIceByToken(callFromId, iceCandidate);
        send(message);
    }

    /**
     * 挂断应答
     */
    @Override
    public void sendHangUp() {
        Log.e(TAG, "挂断应答---");
        String message = getHangUpByToken();
        send(message);
    }

    /**
     * 切换通话方式应答
     * @param ack 应答状态 AGREE同意、REFUSE拒绝
     */
    @Override
    public void sendChangeCallTypeAck(String ack) {
        Log.e(TAG, "切换通话方式应答---");
        String message = getChangeCallTypeAckByToken(callFromId, ack);
        send(message);
    }

    //============================需要发送的 end=====================================

    //============================需要接收的 start=====================================

    @Override
    public void handleMessage(String message) {
        try {
            //这个是回调数据
            JSONObject jsonObject = JSON.parseObject(message);

            //匹配客服应答报文
            if (jsonObject.getString("type") != null
                    && "QUEUE_ACK".equals(jsonObject.getString("type"))) {
                //配对到的客服id，为null时表示未匹配到客服需等待
                String serverId = jsonObject.getString("serverId");
                if (serverId != null) {
                    //配对到客服 发起通话
                    events.onMatch();
                    sendCall(serverId);
                } else {
                    int queueCount = jsonObject.getIntValue("queueCount");
                    //客服等待中
                    events.onWait(queueCount);
                }
            }

            //通话响应报文
            if (jsonObject.getString("type") != null
                    && "CALL_ACK".equals(jsonObject.getString("type"))) {
                String callStatus = jsonObject.getString("callStatus");
                //web点击接听
                if ("ANSWER".equals(callStatus)) {
                    String fromId = jsonObject.getString("fromId");
                    String toId = jsonObject.getString("toId");
                    String callType = jsonObject.getString("callType");
                    callFromId = fromId;
                    ArrayList<String> connections = new ArrayList<>();
                    connections.add(fromId);
                    //发起通话
                    events.onSendCall(connections);
                    //即将接通
                    events.onCall(callType);
                }
            }

            //通话连接应答报文 接收到answer
            if(jsonObject.getString("type") != null
                    && "ANSWER".equals(jsonObject.getString("type"))) {
                Map map = JSON.parseObject(message, Map.class);
                Map desc = (Map) map.get("description");
                String sdp = (String) desc.get("sdp");

                String id = jsonObject.getString("fromId");

                events.onReceiveAnswer(id, sdp);
            }

            //通道信息应答报文
            if(jsonObject.getString("type") != null
                    && "ICE".equals(jsonObject.getString("type"))) {

                Map map = JSON.parseObject(message, Map.class);
                Map ice = (Map) map.get("candidate");
                String sdpMid = (String) ice.get("sdpMid");
                String candidate = (String) ice.get("candidate");
                int sdpMLineIndex = (int) ice.get("sdpMLineIndex");
                IceCandidate iceCandidate = new IceCandidate(sdpMid, sdpMLineIndex, candidate);

                String id = jsonObject.getString("fromId");

                events.onRemoteIceCandidate(id, iceCandidate);
            }

            //web挂断 收到的
            if (jsonObject.getString("type") != null
                    && "HANGUP".equals(jsonObject.getString("type"))) {

                //发送挂断应答报文
                sendHangUp();

                events.onHangUp();
            }

            //切换通话方式请求
            if (jsonObject.getString("type") != null
                    && "CHANGE_CALL_TYPE".equals(jsonObject.getString("type"))) {

                String beforeCallType = jsonObject.getString("beforeCallType");
                String afterCallType = jsonObject.getString("afterCallType");

                events.onChangeCall(beforeCallType, afterCallType);
            }

            //切换通话方式取消
            if (jsonObject.getString("type") != null
                    && "CHANGE_CALL_TYPE_CANCEL".equals(jsonObject.getString("type"))) {

                events.onChangeCancel();
            }

            //自定义消息
            if (jsonObject.getString("type") != null
                    && "CUSTOM".equals(jsonObject.getString("type"))) {
                //OPEN_VIDEO CLOSE_VIDEO OPEN_DRAW CLOSE_DRAW
                String action = jsonObject.getString("action");
                if (ActionConfigs.ACTION_SEND_IMAGE.equals(action)) {
                    //发送图片
                    String imageStr = jsonObject.getString("image");
                    events.onSendImage(imageStr);
                } else if (ActionConfigs.ACTION_SEND_POINT.equals(action)) {
                    //远程控制 发送坐标
                    String mouseEvent = jsonObject.getString("mouseEvent");
                    MouseEventBean mouseEventBean = new Gson().fromJson(mouseEvent, MouseEventBean.class);
                    events.onSendPoint(mouseEventBean);
                } else {
                    //web发送自定义消息 摄像头切换 开启或关闭涂鸦 开启或关闭远程控制
                    events.onAction(action);
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //============================需要接收的 end=====================================

    /**
     * 发送message
     * @param message
     * @return 是否发送成功
     */
    public boolean send(String message) {
        boolean isSendSuccess = false;
        if(mWebSocket != null) {
            Log.e(TAG, "WebSocket 发送的 = " + message);

            EventMessage msg = new EventMessage(1, message);
            EventBus.getDefault().post(msg);

            isSendSuccess = mWebSocket.send(message);
        }
        return isSendSuccess;
    }

    /**
     * 取消WebSocket
     */
    public void cancel() {
        Log.e(TAG, "WebSocket cancel--");
        if (mWebSocket != null) {
            mWebSocket.cancel();
        }
    }

    /**
     * WebSocket 是否打开
     * @return
     */
    @Override
    public boolean isOpen() {
        return isOpen;
    }

    /**
     * 关闭WebSocket
     */
    @Override
    public void close() {
        Log.e(TAG, "WebSocket close--");
        if (mWebSocket != null) {
            //关闭连接
            mWebSocket.close(1000, null);
        }
        if (mHandler != null) {
            //停止发送心跳消息
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * 创建WebSocket的线程
     */
    class InitSocketThread extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                //初始化WebSocket
                initWebSocket();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 心跳消息
     */
    private Runnable heartBeatRunnable = new Runnable() {
        @Override
        public void run() {
            if (System.currentTimeMillis() - sendTime >= HEART_BEAT_RATE) {
                if (mWebSocket != null) {
                    //发送一个消息给服务器，通过发送消息的成功失败来判断长连接的连接状态
                    boolean isSuccess = send(getHeartMapByToken());
                    if (!isSuccess) {
                        //长连接已断开
                        Log.e(TAG, "发送心跳包-------------长连接已断开");
                        mHandler.removeCallbacks(heartBeatRunnable);
                        //取消掉以前的长连接
                        cancel();
                        //创建一个新的连接
                        new InitSocketThread().start();
                    } else {
                        //长连接处于连接状态---
                        Log.e(TAG, "发送心跳包-------------长连接处于连接状态");
                    }
                }
                //发送时间
                sendTime = System.currentTimeMillis();
            }
            //每隔一定的时间，对长连接进行一次心跳检测
            mHandler.postDelayed(this, HEART_BEAT_RATE);
        }
    };

}
