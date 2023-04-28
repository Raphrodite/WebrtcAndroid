package com.example.webrtcandroid.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.example.webrtcandroid.utils.ScreenUtil;

/**
 * * Copyright * 圣通电力
 *
 * @ProjectName: WebrtcAndroid
 * @Package: com.example.webrtcandroid.view
 * @ClassName: BaseFloatingWindow
 * @Description: 悬浮窗基类
 * @Author: Raphrodite
 * @CreateDate: 2023/4/6
 */
public abstract class BaseFloatingWindow {

    private FloatingWindowManager mWindowManager;
    public Context mContext;
    //屏幕宽高
    private int[] screenWH;
    public ViewGroup mRootView;
    private float left, top;
    private float disX, disY;
    private float downX, downY;
    private float lastDisX, lastDisY;
    private WindowManager.LayoutParams mParams;
    private long animalTime = 300;

    public BaseFloatingWindow(Context context) {
        mContext = context.getApplicationContext();
        screenWH = ScreenUtil.getScreenWH(mContext);
        mWindowManager = FloatingWindowManager.getInstance(mContext);
        mRootView = (ViewGroup) LayoutInflater.from(mContext).inflate(setLayoutId(), null, false);
        mRootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        actionDown(event);
                        return false;
                    case MotionEvent.ACTION_MOVE:
                        actionMove(event);
                        return true;
                    case MotionEvent.ACTION_UP:
                        return actionUp();
                    default:
                        return true;
                }
            }
        });
        initView(mRootView);
        onBindListener();
    }

    public boolean actionUp() {
        if (Math.abs(lastDisX - disX) < 1f && Math.abs(lastDisY - disY) < 1f) {
            lastDisX = disX;
            lastDisY = disY;
            return false;
        } else {
            lastDisX = disX;
            lastDisY = disY;
            return true;
        }
    }

    public void actionMove(MotionEvent event) {
        disX = lastDisX + event.getRawX() - downX;
        disY = lastDisY + event.getRawY() - downY;
        left = disX;
        top = disY;
        updateView();
    }

    public void actionDown(MotionEvent event) {
        downX = event.getRawX();
        downY = event.getRawY();
    }

    protected abstract int setLayoutId();

    protected abstract void initView(View mRootView);

    protected abstract void onBindListener();

    public void show(final int marginRight, final int marginBottom, final OnShowListener onShowListener) {
        int LAYOUT_FLAG;
        // 类型
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //8.0
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //6.0
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
            // LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_TOAST;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        mParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        mParams.gravity = Gravity.TOP | Gravity.LEFT;
        mParams.x = (int) screenWH[0] - mRootView.getMeasuredWidth();
        mParams.y = (int) screenWH[1] - mRootView.getMeasuredHeight();
        lastDisX = mParams.x;
        lastDisY = mParams.y;
        mWindowManager.addView(mRootView, mParams);
        mRootView.setVisibility(View.INVISIBLE);
        mRootView.post(new Runnable() {
            @Override
            public void run() {
                mParams.x = (int) screenWH[0] - mRootView.getMeasuredWidth() - marginRight;
                if (mParams.x < 0) {
                    mParams.x = 0;
                }
                mParams.y = (int) screenWH[1] - mRootView.getMeasuredHeight() - ScreenUtil.getStatusBarHeight(mContext) - marginBottom;
                if (mParams.y < 0) {
                    mParams.y = 0;
                }

                lastDisX = mParams.x;
                lastDisY = mParams.y;
                disX = mParams.x;
                disY = mParams.y;
                mWindowManager.updateView(mRootView, mParams);
                mRootView.setVisibility(View.VISIBLE);
                if (!isSetAnimal()) {
                    return;
                }
                ValueAnimator valueAnimator = ValueAnimator.ofFloat(1, 0);
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        Float value = (Float) animation.getAnimatedValue();
                        mRootView.setTranslationX(value * mRootView.getWidth());
                        if (onShowListener != null && value == 0) {
                            onShowListener.onShowFinish();
                        }
                    }
                });
                valueAnimator.setDuration(animalTime).start();
            }
        });
    }

    public void showCenter(final OnShowListener onShowListener) {
        int LAYOUT_FLAG;
        // 类型
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//8.0
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//6.0
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
            // LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_TOAST;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        mParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        mParams.gravity = Gravity.TOP | Gravity.LEFT;
        mParams.x = (int) screenWH[0] - mRootView.getMeasuredWidth();
        mParams.y = (int) screenWH[1] - mRootView.getMeasuredHeight();
        lastDisX = mParams.x;
        lastDisY = mParams.y;
        mWindowManager.addView(mRootView, mParams);
        mRootView.setVisibility(View.INVISIBLE);
        mRootView.post(new Runnable() {
            @Override
            public void run() {
                mParams.x = (int) screenWH[0] - mRootView.getMeasuredWidth();
                mParams.x = mParams.x / 2;
                if (mParams.x < 0) {
                    mParams.x = 0;
                }
                mParams.y = (int) screenWH[1] - mRootView.getMeasuredHeight() - ScreenUtil.getStatusBarHeight(mContext);
                mParams.y = mParams.y / 2;
                if (mParams.y < 0) {
                    mParams.y = 0;
                }

                lastDisX = mParams.x;
                lastDisY = mParams.y;
                disX = mParams.x;
                disY = mParams.y;
                mWindowManager.updateView(mRootView, mParams);
                mRootView.setVisibility(View.VISIBLE);
                if (!isSetAnimal()) {
                    return;
                }
                ValueAnimator valueAnimator = ValueAnimator.ofFloat(1, 0);
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        Float value = (Float) animation.getAnimatedValue();
                        mRootView.setTranslationX(value * mRootView.getWidth());
                        if (onShowListener != null && value == 0) {
                            onShowListener.onShowFinish();
                        }
                    }
                });
                valueAnimator.setDuration(animalTime).start();
            }
        });
    }

    public void showTopRight(final OnShowListener onShowListener) {
        int LAYOUT_FLAG;
        // 类型
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//8.0
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//6.0
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
            // LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_TOAST;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        mParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        mParams.gravity = Gravity.TOP | Gravity.LEFT;
        mParams.x = (int) screenWH[0] - mRootView.getMeasuredWidth();
        mParams.y = (int) screenWH[1] - mRootView.getMeasuredHeight();
        lastDisX = mParams.x;
        lastDisY = mParams.y;
        mWindowManager.addView(mRootView, mParams);
        mRootView.setVisibility(View.INVISIBLE);
        mRootView.post(new Runnable() {
            @Override
            public void run() {
                mParams.x = (int) screenWH[0] - mRootView.getMeasuredWidth();
                if (mParams.x < 0) {
                    mParams.x = 0;
                }
                mParams.y = 0;
                if (mParams.y < 0) {
                    mParams.y = 0;
                }

                lastDisX = mParams.x;
                lastDisY = mParams.y;
                disX = mParams.x;
                disY = mParams.y;
                mWindowManager.updateView(mRootView, mParams);
                mRootView.setVisibility(View.VISIBLE);
                if (!isSetAnimal()) {
                    return;
                }
                ValueAnimator valueAnimator = ValueAnimator.ofFloat(1, 0);
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        Float value = (Float) animation.getAnimatedValue();
                        mRootView.setTranslationX(value * mRootView.getWidth());
                        if (onShowListener != null && value == 0) {
                            onShowListener.onShowFinish();
                        }
                    }
                });
                valueAnimator.setDuration(animalTime).start();
            }
        });
    }

    public void showTopRight(final OnShowListener onShowListener, Intent captureIntent) {
        int LAYOUT_FLAG;
        // 类型
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//8.0
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//6.0
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
            // LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_TOAST;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        mParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        mParams.gravity = Gravity.TOP | Gravity.LEFT;
        mParams.x = (int) screenWH[0] - mRootView.getMeasuredWidth();
        mParams.y = (int) screenWH[1] - mRootView.getMeasuredHeight();
        lastDisX = mParams.x;
        lastDisY = mParams.y;
        mWindowManager.addView(mRootView, mParams);
        mRootView.setVisibility(View.INVISIBLE);
        mRootView.post(new Runnable() {
            @Override
            public void run() {
                mParams.x = (int) screenWH[0] - mRootView.getMeasuredWidth();
                if (mParams.x < 0) {
                    mParams.x = 0;
                }
                mParams.y = 0;
                if (mParams.y < 0) {
                    mParams.y = 0;
                }

                lastDisX = mParams.x;
                lastDisY = mParams.y;
                disX = mParams.x;
                disY = mParams.y;
                mWindowManager.updateView(mRootView, mParams);
                mRootView.setVisibility(View.VISIBLE);
                if (!isSetAnimal()) {
                    return;
                }
                ValueAnimator valueAnimator = ValueAnimator.ofFloat(1, 0);
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        Float value = (Float) animation.getAnimatedValue();
                        mRootView.setTranslationX(value * mRootView.getWidth());
                        if (onShowListener != null && value == 0) {
                            onShowListener.onShowFinish();
                        }
                    }
                });
                valueAnimator.setDuration(animalTime).start();
            }
        });
    }

    public void showOffset(int x, int y, final OnShowListener onShowListener) {
        int LAYOUT_FLAG;
        // 类型
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //8.0
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //6.0
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
            //  LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_TOAST;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        WindowManager.LayoutParams lastMParams = mParams;
        if (lastMParams == null) {
            lastMParams = new WindowManager.LayoutParams();
            lastMParams.x = 0;
            lastMParams.y = 0;
        }
        mParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        mParams.gravity = Gravity.TOP | Gravity.LEFT;
        mParams.x = (int) (lastMParams.x + x);
        mParams.y = (int) (lastMParams.y + y);
        lastDisX = mParams.x;
        lastDisY = mParams.y;
        mWindowManager.addView(mRootView, mParams);
        mRootView.setVisibility(View.INVISIBLE);
        mRootView.post(new Runnable() {
            @Override
            public void run() {
                if (mParams.x < 0) {
                    mParams.x = 0;
                }
                if (mParams.y < 0) {
                    mParams.y = 0;
                }
                int measuredWidth = mRootView.getMeasuredWidth();
                int measuredHeight = mRootView.getMeasuredHeight();
                if (mParams.x >= screenWH[0] - measuredWidth) {
                    mParams.x = screenWH[0] - measuredWidth;
                }
                if (mParams.y >= screenWH[1] - measuredHeight) {
                    mParams.y = screenWH[1] - measuredHeight;
                }

                lastDisX = mParams.x;
                lastDisY = mParams.y;
                disX = mParams.x;
                disY = mParams.y;
                mWindowManager.updateView(mRootView, mParams);
                mRootView.setVisibility(View.VISIBLE);
                if (!isSetAnimal()) {
                    return;
                }
                ValueAnimator valueAnimator = ValueAnimator.ofFloat(1, 0);
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        Float value = (Float) animation.getAnimatedValue();
                        mRootView.setTranslationX(value * mRootView.getWidth());
                        if (onShowListener != null && value == 0) {
                            onShowListener.onShowFinish();
                        }
                    }
                });
                valueAnimator.setDuration(animalTime).start();
            }
        });
    }

    public void showMatch(final OnShowListener onShowListener) {//软键盘可弹出
        int LAYOUT_FLAG;
        // 类型
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//8.0
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//6.0
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
            // LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_TOAST;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        mParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        mParams.gravity = Gravity.TOP | Gravity.LEFT;
        mParams.x = (int) screenWH[0] - mRootView.getMeasuredWidth();
        mParams.y = (int) screenWH[1] - mRootView.getMeasuredHeight();
        lastDisX = mParams.x;
        lastDisY = mParams.y;
        mWindowManager.addView(mRootView, mParams);
        mRootView.setVisibility(View.INVISIBLE);
        mRootView.post(new Runnable() {
            @Override
            public void run() {
                mParams.x = (int) screenWH[0] - mRootView.getMeasuredWidth();
                mParams.x = mParams.x / 2;
                if (mParams.x < 0) {
                    mParams.x = 0;
                }
                mParams.y = (int) screenWH[1] - mRootView.getMeasuredHeight() - ScreenUtil.getStatusBarHeight(mContext);
                mParams.y = mParams.y / 2;
                if (mParams.y < 0) {
                    mParams.y = 0;
                }

                lastDisX = mParams.x;
                lastDisY = mParams.y;
                disX = mParams.x;
                disY = mParams.y;
                mWindowManager.updateView(mRootView, mParams);
                mRootView.setVisibility(View.VISIBLE);
                if (!isSetAnimal()) {
                    return;
                }
                ValueAnimator valueAnimator = ValueAnimator.ofFloat(1, 0);
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        Float value = (Float) animation.getAnimatedValue();
                        mRootView.setTranslationX(value * mRootView.getWidth());
                        if (onShowListener != null && value == 0) {
                            onShowListener.onShowFinish();
                        }
                    }
                });
                valueAnimator.setDuration(animalTime).start();
            }
        });
    }

    public void hide(final OnHideListener onHideListener) {
        if (!isSetAnimal()) {
            mRootView.setVisibility(View.GONE);
            mWindowManager.removeView(mRootView);
            return;
        }
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float value = (Float) animation.getAnimatedValue();
                mRootView.setTranslationX(value * mRootView.getWidth());
                if (value == 1) {
                    mRootView.setVisibility(View.GONE);
                    mWindowManager.removeView(mRootView);
                    if (onHideListener != null) {
                        onHideListener.onHideFinish(getMarginRight(), getMarginBottom());
                    }
                }
            }
        });
        valueAnimator.setDuration(animalTime).start();
    }

    public boolean isSetAnimal() {
        return true;
    }

    public int getMarginRight() {
        if (mParams == null) {
            return 0;
        }
        return (int) screenWH[0] - mRootView.getMeasuredWidth() - mParams.x;
    }

    public int getMarginBottom() {
        if (mParams == null) {
            return 0;
        }
        return (int) screenWH[1] - mRootView.getMeasuredHeight() - ScreenUtil.getStatusBarHeight(mContext) - mParams.y;
    }

    private void updateView() {
        mParams.x = (int) left;
        mParams.y = (int) top;
        mWindowManager.updateView(mRootView, mParams);
    }

    public interface OnShowListener {
        void onShowFinish();
    }

    public interface OnHideListener {
        void onHideFinish(int marginRight, int marginBottom);
    }

}
