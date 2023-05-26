package com.example.webrtcandroid;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
//        Log.e("zrzr", "onReceiveMsg = " + receiveMesg);
        event = event + receiveMesg + "\n";
//        Log.e("zrzr", "event = " + event);
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
}