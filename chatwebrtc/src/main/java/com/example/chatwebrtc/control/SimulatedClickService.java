package com.example.chatwebrtc.control;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import androidx.annotation.RequiresApi;


/**
 * 模拟点击的服务类
 *
 * @author zhang
 * @since 2019-12-14
 */
public class SimulatedClickService extends AccessibilityService {
//    private Handler handler = new Handler(Looper.getMainLooper()) {
//        @TargetApi(Build.VERSION_CODES.N)
//        @Override
//        public void handleMessage(Message msg) {
//            if (msg.what == 1) {
//                AccessibilityEvent event = (AccessibilityEvent) msg.obj;
//                dispatchGestureClick(543, 2088);
//            } else if (msg.what == 2) {
//                AccessibilityEvent event = (AccessibilityEvent) msg.obj;
//                dispatchGestureSlide();
//            }
//        }
//    };

    /**
     * 监听窗口变化的回调
     *
     * @param event 窗口变化事件
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        int action = event.getAction();
//        Log.e("======", "onAccessibilityEvent" + eventType + "  action:" + action + " PackageName:" +
//                event.getPackageName() + "  ClassName:" + event.getClassName());
        AccessibilityOperator.getInstance().updateEvent(this, event);

//        switch (eventType) {
//            case AccessibilityEvent.TYPE_VIEW_CLICKED:
//                Message message1 = new Message();
//                message1.what = 1;
//                message1.obj = event;
//                handler.sendMessageDelayed(message1, 1500);
//                break;
//            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
//                Message message2 = new Message();
//                message2.what = 2;
//                message2.obj = event;
//                handler.sendMessageDelayed(message2, 3000);
//                break;
//        }
    }


    public void dispatchGestureSlide(int x1,int y1,int x2,int y2) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Path path = new Path();
            path.moveTo(x1, y1);//设置Path的起点
//            path.quadTo(450, 1036, 90, 864);
            path.lineTo(x2, y2);//滑动终点
            //100L 第一个是开始的时间，第二个是持续时
            gestureOnScreen(path, 0, 17L, new MyCallBack());//仿滑动
        }
    }

    public void dispatchGestureClick(int x, int y) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Path path = new Path();
            path.moveTo(x, y);
            gestureOnScreen(path, 0, 1, new MyCallBack());//仿点击
        }
    }

    protected void gestureOnScreen(Path path, long startTime, long duration, GestureResultCallback callback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            GestureDescription.Builder builder = new GestureDescription.Builder();
            builder.addStroke(new GestureDescription.StrokeDescription(path, startTime, duration));
            GestureDescription gestureDescription = builder.build();
            boolean gesture = dispatchGesture(gestureDescription, callback, null);
            Log.e("===gestureOnScreen", gesture + "");
        }
    }

    /**
     * 中断服务的回调
     */
    @Override
    public void onInterrupt() {
        Log.e("======", "onInterrupt");
    }

    /**
     * 当服务启动时的回调，可以做一些初始化操作
     */
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.e("======", "onServiceConnected");
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    class MyCallBack extends GestureResultCallback {
        @Override
        public void onCompleted(GestureDescription gestureDescription) {
            super.onCompleted(gestureDescription);
            Log.e("======", "dispatchGestureClick onCompleted");
        }

        @Override
        public void onCancelled(GestureDescription gestureDescription) {
            super.onCancelled(gestureDescription);
            Log.e("======", "dispatchGestureClick onCancelled");
        }

    }

}
