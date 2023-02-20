package com.example.webrtcandroid.websocket;

import static com.example.webrtcandroid.websocket.WebSocketData.getCall;
import static com.example.webrtcandroid.websocket.WebSocketData.getHeartMap;
import static com.example.webrtcandroid.websocket.WebSocketData.getIceCandidate;
import static com.example.webrtcandroid.websocket.WebSocketData.getOffer;
import static com.example.webrtcandroid.websocket.WebSocketData.getPreCall;

import android.os.Handler;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.example.webrtcandroid.webrtc.IWebRtcEvents;

import org.webrtc.IceCandidate;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

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
    private final String TAG = "WebSocketManager_zrzr";

    /**
     * 可替换为自己的主机名和端口号 ws://192.168.13.109:14000/wst
     */
    private final String WEBSOCKET_HOST_AND_PORT = "wss://rpc.stdlnj.cn/wst";

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
        boolean isSuccess = send(getHeartMap());
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
        Log.e(TAG, "onMessage String = " + text);
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
        Log.e(TAG, "onClosing");
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
        Log.e(TAG, "onFailure");
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
        Log.e(TAG, "connect");
        close();
        //连接地址
        webSocketUrl = url;
        new InitSocketThread().start();
    }

    /**
     * 发起预通话配置(分配对应客服)
     */
    @Override
    public void sendPre() {
        Log.e(TAG, "sendPre");
        String message = getPreCall();
        send(message);
    }

    /**
     * 发起正式通话
     * @param offerId  安卓客户端ID
     * @param answerId 分配的客服ID（浏览器端登录用户ID，仅当connectStatus为0时有值）
     */
    @Override
    public void sendCall(String offerId, String answerId) {
        Log.e(TAG, "sendCall");
        String message = getCall(offerId, answerId);
        send(message);
    }


    @Override
    public void sendOffer(String sdp) {
        Log.e(TAG, "sendOffer");
        String message = getOffer("111", "111", sdp);
        send(message);
    }

    @Override
    public void sendIceCandidate(IceCandidate iceCandidate) {
        Log.e(TAG, "sendIceCandidate");
        String message = getIceCandidate(iceCandidate);
        send(message);
    }

    //============================需要发送的 end=====================================

    //============================需要接收的 start=====================================

    @Override
    public void handleMessage(String message) {
        try {
            //这个是回调数据
            JSONObject jsonObject = JSON.parseObject(message);

            //发起预通话配置回调
            if (jsonObject.getInteger("connectStatus") != null) {
                //连接状态 connectStatus 0-分配成功 1-需要排队
                int connectStatus = jsonObject.getIntValue("connectStatus");
                String offerId = jsonObject.getString("offerId");
                if (connectStatus == 0) {
                    //预通话配置 分配成功 发起正式通话
                    String answerId = jsonObject.getString("answerId");
                    sendCall(answerId, offerId);
                } else {
                    //排队中
                    events.onQueue();
                }
            }

            //发起正式通话回调
            if (jsonObject.getString("callStatus") != null) {
                String callStatus = jsonObject.getString("callStatus");
                String offerId = jsonObject.getString("offerId");
                //接通
                if("ANSWER".equals(callStatus)) {
                    String answerId = jsonObject.getString("answerId");
                    ArrayList<String> connections = new ArrayList<>();
                    connections.add(answerId);
                    //发起正式通话
                    events.onSendCall(connections);
                }
            }

            //发送offer 接收到answer
            if(jsonObject.getString("type") != null
                    && "ANSWER".equals(jsonObject.getString("type"))) {
                Map map = JSON.parseObject(message, Map.class);
                Map desc = (Map) map.get("description");
                String sdp = (String) desc.get("sdp");

                String id = jsonObject.getString("answerId");

                events.onReceiveAnswer(id, sdp);
            }

            //接收到ice报文
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
            Log.e(TAG, "send = " + message);
            isSendSuccess = mWebSocket.send(message);
        }
        return isSendSuccess;
    }

    /**
     * 取消WebSocket
     */
    public void cancel() {
        Log.e(TAG, "cancel");
        if (mWebSocket != null) {
            mWebSocket.cancel();
        }
    }

    @Override
    public boolean isOpen() {
        return isOpen;
    }

    /**
     * 关闭WebSocket
     */
    @Override
    public void close() {
        Log.e(TAG, "close");
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
                    boolean isSuccess = send(getHeartMap());
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
