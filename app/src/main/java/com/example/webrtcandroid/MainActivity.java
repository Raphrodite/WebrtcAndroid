package com.example.webrtcandroid;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.chatwebrtc.bean.EventMessage;
import com.example.webrtcandroid.databinding.ActivityMainBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class MainActivity extends BaseActivity {

    /**
     * 屏幕共享常量
     */
    public static final int PROJECTION_REQUEST_CODE = 100;

    private String token = "";

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //绑定试图
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        EventBus.getDefault().register(this);

        initChatService();

        binding.tvCall.setEnabled(false);
        //呼叫客服
        binding.tvCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, SecondActivity.class));

                //出现弹窗提示
//                CallConfirmDialog callConfirmDialog = new CallConfirmDialog(MainActivity.this);
//                callConfirmDialog.showDialog(callConfirmDialog);
//                callConfirmDialog.setOnConfirmListener(new CallConfirmDialog.OnConfirmListener() {
//                    @Override
//                    public void onConfirm() {
//                        //弹窗消失
//                        callConfirmDialog.dismiss();
//
//                        Map<String, Object> map = new HashMap<>();
//                        map.put("deviceId", "123");
//                        //JSONObject
//                        JSONObject object = new JSONObject(map);
//                        //转化为json字符串
//                        String jsonString = object.toJSONString();
//                        //客户登录接口
//                        OkhttpUtils.getInstance().stringPost(MainActivity.this, "/login/use", jsonString, new OkhttpUtils.ICallBack() {
//                            @Override
//                            public void onResponse(JsonResult result) {
//                                //回调 获取token
//                                LoginUseBean bean = new Gson().fromJson(result.getData(), LoginUseBean.class);
//                                token = bean.getToken();
//                                Log.e("zrzr", "token = " + token);
//
//                                //进行通话 首先要判断是否拥有屏幕共享权限
//                                if (!PermissionUtil.isNeedRequestPermission(MainActivity.this)) {
//                                    //屏幕共享权限
//                                    permissionCheckForProjection();
//                                }
//                            }
//                        });
//                    }
//                });
            }
        });

        binding.tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("zrzr", "点击了红色");
                Toast.makeText(MainActivity.this, "点击了红色", Toast.LENGTH_SHORT).show();
            }
        });

        binding.tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("zrzr", "点击了绿色");
                Toast.makeText(MainActivity.this, "点击了绿色", Toast.LENGTH_SHORT).show();
            }
        });

        binding.tv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("zrzr", "点击了蓝色");
                Toast.makeText(MainActivity.this, "点击了蓝色", Toast.LENGTH_SHORT).show();
            }
        });

        getScreen();
        Log.e("zrzr", getScreenInfo(this));
    }

    private void initChatService() {
        //判断服务是否运行
        if (!isServiceRunning("com.example.webrtcandroid.ChatService", this)) {
            startService(new Intent(this, ChatService.class));
        }
    }

    /**
     * 判断服务是否运行
     * @param serviceName
     * @param context
     * @return
     */
    public static boolean isServiceRunning(String serviceName, Context context) {
        //活动管理器
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //获取运行的服务,参数表示最多返回的数量
        List<ActivityManager.RunningServiceInfo> runningServices = am.getRunningServices(100);
        for (ActivityManager.RunningServiceInfo runningServiceInfo : runningServices) {
            String className = runningServiceInfo.service.getClassName();
            if (className.equals(serviceName)) {
                //判断服务是否运行
                return true;
            }
        }
        return false;
    }

    private String event = "";

    /**
     * 接收到的 WebSocket发送的报文
     * @param message
     */
    @Subscribe
    public void onReceiveMsg(EventMessage message) {
        String receiveMesg = message.getMessage();
        event = event + receiveMesg + "\n";
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.tvEvent.setText(event);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        stopService(new Intent(this, ChatService.class));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initChatService();
    }

    public void getScreen(){
        // 通过Activity类中的getWindowManager()方法获取窗口管理，再调用getDefaultDisplay()方法获    取获取Display对象
        Display display = getWindowManager().getDefaultDisplay();

        // 方法一(推荐使用)使用Point来保存屏幕宽、高两个数据
        Point outSize = new Point();
        // 通过Display对象获取屏幕宽、高数据并保存到Point对象中
        display.getSize(outSize);
        // 从Point对象中获取宽、高
        int x = outSize.x;
        int y = outSize.y;
        // 通过吐司显示屏幕宽、高数据
        Log.e("zrzr", "手机像素为：x:" + x + ",y:" + y+",screen:"+getPhysicsScreenSize(this));
//        Toast.makeText(this, "手机像素为：x:" + x + ",y:" + y+",screen:"+getPhysicsScreenSize(this), Toast.LENGTH_LONG).show();
    }

    /**
     * 得到屏幕的物理尺寸，由于该尺寸是在出厂时，厂商写死的，所以仅供参考
     * 计算方法：获取到屏幕的分辨率:point.x和point.y，再取出屏幕的DPI（每英寸的像素数量），
     * 计算长和宽有多少英寸，即：point.x / dm.xdpi，point.y / dm.ydpi，屏幕的长和宽算出来了，
     * 再用勾股定理，计算出斜角边的长度，即屏幕尺寸。
     *
     * @param context
     * @return
     */
    public static double getPhysicsScreenSize(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        manager.getDefaultDisplay().getRealSize(point);
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int densityDpi = dm.densityDpi;//得到屏幕的密度值，但是该密度值只能作为参考，因为他是固定的几个密度值。
        double x = Math.pow(point.x / dm.xdpi, 2);//dm.xdpi是屏幕x方向的真实密度值，比上面的densityDpi真实。
        double y = Math.pow(point.y / dm.ydpi, 2);//dm.xdpi是屏幕y方向的真实密度值，比上面的densityDpi真实。
        double screenInches = Math.sqrt(x + y);
        return screenInches;
    }

    /**
     * 获取屏幕像素，尺寸，dpi相关信息
     * @param activity 上下文
     * @return 屏幕信息
     */
    public static String getScreenInfo(Activity activity){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            //4.2开始有虚拟导航栏，增加了该方法才能准确获取屏幕高度
            activity.getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
        }else{
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            //displayMetrics = activity.getResources().getDisplayMetrics();//或者该方法也行
        }
        Point point = new Point();
        activity.getWindowManager().getDefaultDisplay().getRealSize(point);
        double x = Math.pow(point.x / displayMetrics.xdpi, 2);//dm.xdpi是屏幕x方向的真实密度值，比上面的densityDpi真实。
        double y = Math.pow(point.y / displayMetrics.ydpi, 2);//dm.xdpi是屏幕y方向的真实密度值，比上面的densityDpi真实。
        double screenInches = Math.sqrt(x + y);
        return "screenSize="+screenInches
                + ",densityDpi="+displayMetrics.densityDpi
                + ",width="+displayMetrics.widthPixels
                +",height="+displayMetrics.heightPixels;
    }

}