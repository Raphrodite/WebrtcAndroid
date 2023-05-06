package com.example.chatwebrtc.peer;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.projection.MediaProjection;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.chatwebrtc.IViewCallback;
import com.example.chatwebrtc.utils.webrtc.MediaType;
import com.example.chatwebrtc.utils.webrtc.MyIceServer;
import com.example.chatwebrtc.websocket.IWebSocket;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.CameraVideoCapturer;
import org.webrtc.DataChannel;
import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RtpReceiver;
import org.webrtc.RtpTransceiver;
import org.webrtc.ScreenCapturerAndroid;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoDecoderFactory;
import org.webrtc.VideoEncoderFactory;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;
import org.webrtc.audio.JavaAudioDeviceModule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * * Copyright * 圣通电力
 *
 * @ProjectName: WebrtcAndroid
 * @Package: com.example.webrtcandroid.peer
 * @ClassName: PeerConnectionHelper
 * @Description: peer通道连接
 * @Author: Raphrodite
 * @CreateDate: 2023/2/14
 */
public class PeerConnectionHelper {

    /**
     * Log TAG
     */
    public final static String TAG = "Peer_zrzr";

    /**
     * 视频相关参数 320-240  720-1080
     */
    public static final int VIDEO_RESOLUTION_WIDTH = 720;
    public static final int VIDEO_RESOLUTION_HEIGHT = 1080;
    public static final int FPS = 30;
    public static final String VIDEO_CODEC_H264 = "H264";
    public static final String VIDEO_TRACK_ID = "ARDAMSv0";
    public static final String AUDIO_TRACK_ID = "ARDAMSa0";

    /**
     * 信令服务器集合
     */
    public ArrayList<PeerConnection.IceServer> ICEServers;

    /**
     * peer连接工厂 音频视频轨道 音频视频资源
     */
    public PeerConnectionFactory factory;
    public MediaStream localStream;
    public VideoTrack localVideoTrack;
    public AudioTrack localAudioTrack;
    public MediaStream screenStream;
    public VideoTrack screenVideoTrack;
    public AudioTrack screenAudioTrack;
    public VideoCapturer captureAndroid;
    public VideoSource videoSource;
    public AudioSource audioSource;
    public VideoSource screenVideoSource;
    public AudioSource screemAudioSource;

    /**
     * 初始化参数
     */
    private Context context;
    private EglBase rootEglBase;
    private Intent captureIntent;

    /**
     * WebSocket
     */
    private IWebSocket webSocket;

    /**
     * 是否存在视频
     */
    public boolean videoEnable;

    /**
     * 音频管理者
     */
    private AudioManager mAudioManager;

    /**
     * 媒体类型
     */
    public int mediaType;

    /**
     * peer连接id集合
     */
    public ArrayList<String> connectionIdArray;
    public Map<String, Peer> connectionPeerDic;

    /**
     * 枚举 Caller-发送者 Receiver-接收
     */
    enum Role {Caller, Receiver,}
    /**
     * 发送者 接收者
     */
    private Role role;

    public IViewCallback viewCallback;

    private SurfaceTextureHelper surfaceTextureHelper;

    /**
     * 线程池
     */
    private final ExecutorService executor;

    /**
     * 初始化peer通道
     * @param webSocket
     * @param iceServers
     */
    public PeerConnectionHelper(IWebSocket webSocket, MyIceServer[] iceServers) {
        this.connectionPeerDic = new HashMap<>();
        this.connectionIdArray = new ArrayList<>();
        this.ICEServers = new ArrayList<>();

        this.webSocket = webSocket;
        executor = Executors.newSingleThreadExecutor();
        if (iceServers != null) {
            //遍历信令服务器
            for (MyIceServer myIceServer : iceServers) {
                PeerConnection.IceServer iceServer = PeerConnection.IceServer
                        .builder(myIceServer.uri)
                        .setUsername(myIceServer.username)
                        .setPassword(myIceServer.password)
                        .createIceServer();
                ICEServers.add(iceServer);
            }
        }
    }

    /**
     * 设置界面回调
     * @param callback
     */
    public void setViewCallback(IViewCallback callback) {
        viewCallback = callback;
    }

    // ===================================WebSocket回调信息 start=======================================

    /**
     * 初始化
     * @param context
     * @param eglBase
     */
    public void initContext(Context context, EglBase eglBase) {
        this.context = context;
        rootEglBase = eglBase;
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    /**
     * 初始化
     * @param context
     * @param eglBase
     * @param captureIntent
     */
    public void initContext(Context context, EglBase eglBase, Intent captureIntent) {
        this.context = context;
        rootEglBase = eglBase;
        this.captureIntent = captureIntent;
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    /**
     * 发起正式通话
     * @param connections
     * @param isVideoEnable
     * @param mediaType
     */
    public void onSendCall(ArrayList<String> connections, boolean isVideoEnable, int mediaType) {
        videoEnable = isVideoEnable;
        this.mediaType = mediaType;
        executor.execute(() -> {
            connectionIdArray.addAll(connections);
            if (factory == null) {
                factory = createConnectionFactory();
            }
            if (localStream == null) {
                //创建本地流 摄像头
                createLocalStream();
                Log.e("zrzr", "createLocalStream");
                Log.e("zrzr", "createScreenStream");
                if(screenStream == null) {
                    Log.e("zrzr", "createScreenStream 1111");
                    //屏幕共享
                    createScreenStream();
                }
            }

            createPeerConnections();
            addStreams();
            createOffers();
        });
    }

    /**
     * 接收到ice 设置ice
     * @param id
     * @param iceCandidate
     */
    public void onRemoteIceCandidate(String id, IceCandidate iceCandidate) {
        executor.execute(() -> {
            Peer peer = connectionPeerDic.get(id);
            if (peer != null) {
                Log.e(TAG, "onRemoteIceCandidate");
                peer.pc.addIceCandidate(iceCandidate);
            }
        });
    }

    /**
     * 发送offer后接收到的answer 设置sdp
     * @param id
     * @param sdp
     */
    public void onReceiverAnswer(String id, String sdp) {
        executor.execute(() -> {
            Peer mPeer = connectionPeerDic.get(id);
            SessionDescription sessionDescription = new SessionDescription(SessionDescription.Type.ANSWER, sdp);
            if (mPeer != null) {
                Log.e(TAG, "onReceiverAnswer");
                mPeer.pc.setRemoteDescription(mPeer, sessionDescription);
            }
        });
    }

    // ===================================WebSocket回调信息 end=======================================

    /**
     * 创建连接工厂
     * @return
     */
    private PeerConnectionFactory createConnectionFactory() {
        PeerConnectionFactory.initialize(
                PeerConnectionFactory.InitializationOptions.builder(context)
                        .createInitializationOptions());

        final VideoEncoderFactory encoderFactory;
        final VideoDecoderFactory decoderFactory;

        encoderFactory = new DefaultVideoEncoderFactory(
                rootEglBase.getEglBaseContext(),
                true,
                true);

        decoderFactory = new DefaultVideoDecoderFactory(rootEglBase.getEglBaseContext());

        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();

        return PeerConnectionFactory.builder()
                .setOptions(options)
                .setAudioDeviceModule(JavaAudioDeviceModule.builder(context).createAudioDeviceModule())
                .setVideoEncoderFactory(encoderFactory)
                .setVideoDecoderFactory(decoderFactory)
                .createPeerConnectionFactory();
    }

    /**
     * 创建本地流
     */
    private void createLocalStream() {
        localStream = factory.createLocalMediaStream("ARDAMS");
        // 音频
        audioSource = factory.createAudioSource(createAudioConstraints());
        localAudioTrack = factory.createAudioTrack(AUDIO_TRACK_ID, audioSource);
        localStream.addTrack(localAudioTrack);

        if (videoEnable) {
            //创建需要传入设备的名称
            //视频通话
            captureAndroid = createVideoCapture();
            // 视频
            surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", rootEglBase.getEglBaseContext());
            videoSource = factory.createVideoSource(captureAndroid.isScreencast());
            if (mediaType == MediaType.TYPE_MEETING) {
                // videoSource.adaptOutputFormat(200, 200, 15);
            }
            captureAndroid.initialize(surfaceTextureHelper, context, videoSource.getCapturerObserver());
            captureAndroid.startCapture(VIDEO_RESOLUTION_WIDTH, VIDEO_RESOLUTION_HEIGHT, FPS);
            localVideoTrack = factory.createVideoTrack(VIDEO_TRACK_ID, videoSource);
            localStream.addTrack(localVideoTrack);
        }
        if (viewCallback != null) {
            viewCallback.onSetLocalStream(localStream);
        }
    }

    /**
     * 创建屏幕共享的流
     */
    private void createScreenStream() {
        screenStream = factory.createLocalMediaStream("SCREENSTREAM");
        // 音频
        screemAudioSource = factory.createAudioSource(createAudioConstraints());
        screenAudioTrack = factory.createAudioTrack("audioScreen", screemAudioSource);
        screenStream.addTrack(screenAudioTrack);

        if (videoEnable) {
            //创建需要传入设备的名称
            //屏幕共享
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                captureAndroid = new ScreenCapturerAndroid(captureIntent, new MediaProjection.Callback() {
                    @Override
                    public void onStop() {
                        super.onStop();
                    }
                });
            }
            // 视频
            SurfaceTextureHelper surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThreadScreen", rootEglBase.getEglBaseContext());
            screenVideoSource = factory.createVideoSource(captureAndroid.isScreencast());
            if (mediaType == MediaType.TYPE_MEETING) {
                // videoSource.adaptOutputFormat(200, 200, 15);
            }
            captureAndroid.initialize(surfaceTextureHelper, context, screenVideoSource.getCapturerObserver());
            captureAndroid.startCapture(VIDEO_RESOLUTION_WIDTH, VIDEO_RESOLUTION_HEIGHT, FPS);
            screenVideoTrack = factory.createVideoTrack("videoScreen", screenVideoSource);
            screenStream.addTrack(screenVideoTrack);
        }
//        if (viewCallback != null) {
//            viewCallback.onSetScreenStream(screenStream);
//        }
    }

    /**
     * 创建所有连接
     */
    private void createPeerConnections() {
        for (Object str : connectionIdArray) {
            Peer peer = new Peer((String) str);
            connectionPeerDic.put((String) str, peer);
        }
    }

    /**
     * 为所有连接添加流
     */
    private void addStreams() {
        Log.e(TAG, "为所有连接添加流");
        for (Map.Entry<String, Peer> entry : connectionPeerDic.entrySet()) {
            if (localStream == null) {
                createLocalStream();
                if(screenStream == null) {
                    //屏幕共享
                    createScreenStream();
                }
            }
            try {
                //添加流
//                entry.getValue().pc.addStream(localStream);
                entry.getValue().pc.addTrack(localAudioTrack, localStream);
                entry.getValue().pc.addTrack(localVideoTrack, localStream);
                if(screenStream != null) {
                    //屏幕共享
//                        entry.getValue().pc.addStream(screenStream);
                    entry.getValue().pc.addTrack(screenVideoTrack, screenStream);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 为所有连接创建offer
     */
    private void createOffers() {
        for (Map.Entry<String, Peer> entry : connectionPeerDic.entrySet()) {
            role = Role.Caller;
            Peer mPeer = entry.getValue();
            mPeer.pc.createOffer(mPeer, offerOrAnswerConstraint());
        }
    }

    /**
     * 关闭通道流
     * @param connectionId
     */
    private void closePeerConnection(String connectionId) {
        Peer mPeer = connectionPeerDic.get(connectionId);
        if (mPeer != null) {
            mPeer.pc.close();
        }
        connectionPeerDic.remove(connectionId);
        connectionIdArray.remove(connectionId);
        if (viewCallback != null) {
            viewCallback.onClose();
        }

    }

    //**************************************逻辑控制 start**************************************

    /**
     * 切换摄像头 前置后置
     */
    public void switchCamera() {
        if (captureAndroid == null) {
            return;
        }
        if (captureAndroid instanceof CameraVideoCapturer) {
            CameraVideoCapturer cameraVideoCapturer = (CameraVideoCapturer) captureAndroid;
            cameraVideoCapturer.switchCamera(null);
        } else {
            Log.e(TAG, "Will not switch camera, video caputurer is not a camera");
        }
    }

    /**
     * 设置自己是否静音
     * @param enable
     */
    public void toggleMute(boolean enable) {
        if (localAudioTrack != null) {
            localAudioTrack.setEnabled(enable);
        }
        if (screenAudioTrack != null) {
            screenAudioTrack.setEnabled(enable);
        }
    }

    public void toggleSpeaker(boolean enable) {
        if (mAudioManager != null) {
            mAudioManager.setSpeakerphoneOn(enable);
        }
    }

    /**
     * 退出通话
     */
    public void exitCall() {
        if (viewCallback != null) {
            viewCallback = null;
        }
        executor.execute(() -> {
            ArrayList myCopy;
            myCopy = (ArrayList) connectionIdArray.clone();
            for (Object Id : myCopy) {
                closePeerConnection((String) Id);
            }
            if (connectionIdArray != null) {
                connectionIdArray.clear();
            }
            if (audioSource != null) {
                audioSource.dispose();
                audioSource = null;
            }
            if (videoSource != null) {
                videoSource.dispose();
                videoSource = null;
            }
            if (captureAndroid != null) {
                try {
                    captureAndroid.stopCapture();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                captureAndroid.dispose();
                captureAndroid = null;
            }
            if (surfaceTextureHelper != null) {
                surfaceTextureHelper.dispose();
                surfaceTextureHelper = null;
            }
            if (factory != null) {
                factory.dispose();
                factory = null;
            }

            if (webSocket != null) {
                webSocket.close();
                webSocket = null;
            }
        });
    }

    private VideoCapturer createVideoCapture() {
        VideoCapturer videoCapturer;
        if (useCamera2()) {
            videoCapturer = createCameraCapture(new Camera2Enumerator(context));
        } else {
            videoCapturer = createCameraCapture(new Camera1Enumerator(true));
        }
        return videoCapturer;
    }

    private VideoCapturer createCameraCapture(CameraEnumerator enumerator) {
        final String[] deviceNames = enumerator.getDeviceNames();
        // First, try to find front facing camera
        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }
        // Front facing camera not found, try something else
        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }
        return null;
    }

    private boolean useCamera2() {
        return Camera2Enumerator.isSupported(context);
    }

    //**************************************逻辑控制 start**************************************

    //**************************************各种约束 start******************************************/

    private static final String AUDIO_ECHO_CANCELLATION_CONSTRAINT = "googEchoCancellation";
    private static final String AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT = "googAutoGainControl";
    private static final String AUDIO_HIGH_PASS_FILTER_CONSTRAINT = "googHighpassFilter";
    private static final String AUDIO_NOISE_SUPPRESSION_CONSTRAINT = "googNoiseSuppression";

    private MediaConstraints createAudioConstraints() {
        MediaConstraints audioConstraints = new MediaConstraints();
        audioConstraints.mandatory.add(
                new MediaConstraints.KeyValuePair(AUDIO_ECHO_CANCELLATION_CONSTRAINT, "true"));
        audioConstraints.mandatory.add(
                new MediaConstraints.KeyValuePair(AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT, "false"));
        audioConstraints.mandatory.add(
                new MediaConstraints.KeyValuePair(AUDIO_HIGH_PASS_FILTER_CONSTRAINT, "true"));
        audioConstraints.mandatory.add(
                new MediaConstraints.KeyValuePair(AUDIO_NOISE_SUPPRESSION_CONSTRAINT, "true"));
        return audioConstraints;
    }

    private MediaConstraints offerOrAnswerConstraint() {
        MediaConstraints mediaConstraints = new MediaConstraints();
        ArrayList<MediaConstraints.KeyValuePair> keyValuePairs = new ArrayList<>();
        keyValuePairs.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        keyValuePairs.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", String.valueOf(videoEnable)));
        mediaConstraints.mandatory.addAll(keyValuePairs);
        return mediaConstraints;
    }

    //**************************************各种约束 end******************************************/

    //**************************************内部类 start******************************************/

    private class Peer implements SdpObserver, PeerConnection.Observer {

        /**
         * peer连接实例
         */
        private PeerConnection pc;

        /**
         * 连接id
         */
        private String id;

        public Peer(String id) {
            this.pc = createPeerConnection();
            this.id = id;
        }

        //****************************PeerConnection.Observer start****************************/

        @Override
        public void onSignalingChange(PeerConnection.SignalingState signalingState) {
            Log.e(TAG, "onSignalingChange: " + signalingState);
        }

        @Override
        public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
            Log.e(TAG, "onIceConnectionChange: " + iceConnectionState);
        }

        @Override
        public void onConnectionChange(PeerConnection.PeerConnectionState newState) {
            Log.e(TAG, "onConnectionChange: " + newState.toString());
        }

        @Override
        public void onIceConnectionReceivingChange(boolean b) {
            Log.e(TAG, "onIceConnectionReceivingChange: " + b);
        }

        @Override
        public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
            Log.e(TAG, "onIceGatheringChange: " + iceGatheringState.toString());
        }

        @Override
        public void onIceCandidate(IceCandidate iceCandidate) {
            Log.e(TAG, "onIceCandidate: " + iceCandidate.toString());
            // 发送IceCandidate
            webSocket.sendIceCandidate(iceCandidate);
        }

        @Override
        public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {
            Log.e(TAG, "onIceCandidatesRemoved: ");
        }

        @Override
        public void onAddStream(MediaStream mediaStream) {
            Log.e(TAG, "onAddStream");
            //添加远程流
            if (viewCallback != null) {
                viewCallback.onAddRemoteStream(mediaStream);
            }
        }

        @Override
        public void onRemoveStream(MediaStream mediaStream) {
            Log.e(TAG, "onRemoveStream");
            if (viewCallback != null) {
                viewCallback.onClose();
            }
        }

        @Override
        public void onDataChannel(DataChannel dataChannel) {
            Log.e(TAG, "onDataChannel");
        }

        @Override
        public void onRenegotiationNeeded() {
            Log.e(TAG, "onRenegotiationNeeded");
        }

        @Override
        public void onAddTrack(RtpReceiver receiver, MediaStream[] mediaStreams) {
            Log.e(TAG, "onAddTrack");
        }

        @Override
        public void onTrack(RtpTransceiver transceiver) {
            Log.e(TAG, "onTrack");
        }

        //****************************PeerConnection.Observer end****************************/

        //****************************SdpObserver start****************************/

        @Override
        public void onCreateSuccess(SessionDescription sessionDescription) {
            Log.e(TAG, "onCreateSuccess: spd创建成功 = " + sessionDescription.type);
            //设置本地sdp
            String sdpDescription = sessionDescription.description;
            if (videoEnable) {
                sdpDescription = preferCodec(sdpDescription, VIDEO_CODEC_H264, false);
            }
            Log.e(TAG, "sdpDescription = " + sdpDescription);
            final SessionDescription sdp = new SessionDescription(sessionDescription.type, sdpDescription);
            pc.setLocalDescription(Peer.this, sdp);
        }

        @Override
        public void onSetSuccess() {
            Log.e(TAG, "onSetSuccess: spd连接成功 = " + pc.signalingState().toString());
            //目前 android终端只作为发送者
            if (pc.signalingState() == PeerConnection.SignalingState.HAVE_REMOTE_OFFER) {
                pc.createAnswer(Peer.this, offerOrAnswerConstraint());
            } else if (pc.signalingState() == PeerConnection.SignalingState.HAVE_LOCAL_OFFER) {
                //判断连接状态为本地发送offer
                if (role == Role.Receiver) {
                    //接收者，发送Answer

                } else if (role == Role.Caller) {
                    //发送者,发送自己的offer
                    webSocket.sendOffer(pc.getLocalDescription().description);
                }

            } else if (pc.signalingState() == PeerConnection.SignalingState.STABLE) {
                // Stable 稳定的
                if (role == Role.Receiver) {

                }
            }
        }

        @Override
        public void onCreateFailure(String s) {
            Log.e(TAG, "onCreateFailure: " + s);
        }

        @Override
        public void onSetFailure(String s) {
            Log.e(TAG, "onSetFailure: " + s);
        }

        //****************************SdpObserver end****************************/

        /**
         * 初始化 RTCPeerConnection 连接管道
         * @return
         */
        private PeerConnection createPeerConnection() {
            if (factory == null) {
                factory = createConnectionFactory();
            }
            // 管道连接抽象类实现方法
            PeerConnection.RTCConfiguration rtcConfig = new PeerConnection.RTCConfiguration(ICEServers);
            return factory.createPeerConnection(rtcConfig, this);
        }
    }

    //**************************************内部类 end******************************************/

    // ===================================替换编码方式优先级 start========================================

    private static String preferCodec(String sdpDescription, String codec, boolean isAudio) {
        final String[] lines = sdpDescription.split("\r\n");
        final int mLineIndex = findMediaDescriptionLine(isAudio, lines);
        if (mLineIndex == -1) {
            Log.e(TAG, "No mediaDescription line, so can't prefer " + codec);
            return sdpDescription;
        }
        // A list with all the payload types with name |codec|. The payload types are integers in the
        // range 96-127, but they are stored as strings here.
        final List<String> codecPayloadTypes = new ArrayList<>();
        // a=rtpmap:<payload type> <encoding name>/<clock rate> [/<encoding parameters>]
        final Pattern codecPattern = Pattern.compile("^a=rtpmap:(\\d+) " + codec + "(/\\d+)+[\r]?$");
        for (String line : lines) {
            Matcher codecMatcher = codecPattern.matcher(line);
            if (codecMatcher.matches()) {
                codecPayloadTypes.add(codecMatcher.group(1));
            }
        }
        if (codecPayloadTypes.isEmpty()) {
            Log.e(TAG, "No payload types with name " + codec);
            return sdpDescription;
        }

        final String newMLine = movePayloadTypesToFront(codecPayloadTypes, lines[mLineIndex]);
        if (newMLine == null) {
            return sdpDescription;
        }
        Log.e(TAG, "Change media description from: " + lines[mLineIndex] + " to " + newMLine);
        lines[mLineIndex] = newMLine;
        return joinString(Arrays.asList(lines), "\r\n", true);
        /* delimiterAtEnd */
    }

    private static int findMediaDescriptionLine(boolean isAudio, String[] sdpLines) {
        final String mediaDescription = isAudio ? "m=audio " : "m=video ";
        for (int i = 0; i < sdpLines.length; ++i) {
            if (sdpLines[i].startsWith(mediaDescription)) {
                return i;
            }
        }
        return -1;
    }

    private static @Nullable
    String movePayloadTypesToFront(
            List<String> preferredPayloadTypes, String mLine) {
        // The format of the media description line should be: m=<media> <port> <proto> <fmt> ...
        final List<String> origLineParts = Arrays.asList(mLine.split(" "));
        if (origLineParts.size() <= 3) {
            Log.e(TAG, "Wrong SDP media description format: " + mLine);
            return null;
        }
        final List<String> header = origLineParts.subList(0, 3);
        final List<String> unpreferredPayloadTypes =
                new ArrayList<>(origLineParts.subList(3, origLineParts.size()));
        unpreferredPayloadTypes.removeAll(preferredPayloadTypes);
        // Reconstruct the line with |preferredPayloadTypes| moved to the beginning of the payload
        // types.
        final List<String> newLineParts = new ArrayList<>();
        newLineParts.addAll(header);
        newLineParts.addAll(preferredPayloadTypes);
        newLineParts.addAll(unpreferredPayloadTypes);
        return joinString(newLineParts, " ", false);
        /* delimiterAtEnd */
    }

    private static String joinString(
            Iterable<? extends CharSequence> s, String delimiter, boolean delimiterAtEnd) {
        Iterator<? extends CharSequence> iter = s.iterator();
        if (!iter.hasNext()) {
            return "";
        }
        StringBuilder buffer = new StringBuilder(iter.next());
        while (iter.hasNext()) {
            buffer.append(delimiter).append(iter.next());
        }
        if (delimiterAtEnd) {
            buffer.append(delimiter);
        }
        return buffer.toString();
    }

    // ===================================替换编码方式优先级 end========================================
}
