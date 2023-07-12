package com.example.chatwebrtc.control;

import android.annotation.TargetApi;
import android.content.Context;
import android.view.accessibility.AccessibilityEvent;


@TargetApi(16)
public class AccessibilityOperator {

    private Context mContext;
    private static AccessibilityOperator mInstance = new AccessibilityOperator();
    private AccessibilityEvent mAccessibilityEvent;
    private SimulatedClickService mAccessibilityService;

    private AccessibilityOperator() {
    }

    public static AccessibilityOperator getInstance() {
        return mInstance;
    }

    public void updateEvent(SimulatedClickService service, AccessibilityEvent event) {
        if (service != null && mAccessibilityService == null) {
            mAccessibilityService = service;
        }
        if (event != null) {
            mAccessibilityEvent = event;
        }
    }


    public void dispatchGestureSlide(int x1,int y1,int x2,int y2) {
        if (mAccessibilityService == null) {
            return;
        }
        mAccessibilityService.dispatchGestureSlide(x1, y1, x2, y2);
    }

    public void dispatchGestureClick(int x, int y) {
        if (mAccessibilityService == null) {
            return;
        }
        mAccessibilityService.dispatchGestureClick(x, y);
    }
}
