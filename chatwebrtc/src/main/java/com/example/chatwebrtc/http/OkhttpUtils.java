package com.example.chatwebrtc.http;

import android.app.Activity;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.chatwebrtc.bean.JsonResult;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * * Copyright * 圣通电力
 *
 * @ProjectName: WebrtcAndroid
 * @Package: com.example.webrtcandroid.http
 * @ClassName:
 * @Description:
 * @Author: Raphrodite
 * @CreateDate: 2023/5/22
 */
public class OkhttpUtils {

    /**
     * Log tag
     */
    private final String TAG = "OkhttpUtils_zrzr";

    /**
     * http://192.168.13.14:14000/vtm http://192.168.13.109:14000/vtm
     */
    public static final String BASE_URL = "https://rpc.stdlnj.cn/vtm";

    public static final String API_URL = "http://yapi.stdlnj.cn/mock/160";

    private static OkHttpClient client;

    private static final OkhttpUtils okHttpRequestUtil = new OkhttpUtils();

    public static OkhttpUtils getInstance() {
        return okHttpRequestUtil;
    }

    private OkhttpUtils() {
        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    public interface ICallBack {
        void onResponse(JsonResult result);

        void onFailure(String message);
    }

    /*-------------------------------------------------------------------------------------------------------
    提供给外部调用的方法*/

    /**
     * post方式提交Json字符串
     *
     * @param activity  Activity
     * @param url       子路径
     * @param postBody  要发送的字符串
     * @param iCallBack 请求成功的回调
     */
    public void stringPost(Activity activity, String url, String postBody, ICallBack iCallBack) {
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(mediaType, postBody);
        Request request = new Request.Builder()
                .url(BASE_URL + url)
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e(TAG, "onFailure = " + e.getMessage());
                toast(activity, e.getMessage(), iCallBack);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    toast(activity, "code != 200", iCallBack);
                } else {
                    JsonResult jsonResult = new JsonResult();
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        jsonResult.setData(jsonObject.getString("data"));
                        jsonResult.setMessage(jsonObject.getString("message"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    requestSuccess(activity, jsonResult, iCallBack);
                }
            }
        });
    }

    /**
     * post方式提交表单（已自动添加personToken）
     *
     * @param activity  Activity
     * @param url       根路径后面的子路径
     * @param map       要发送的数据Map
     * @param iCallBack 请求成功的回调
     */
    public void formPost(Activity activity, String url, Map<String, String> map, final ICallBack iCallBack) {
        FormBody.Builder builder = new FormBody.Builder();
        //从SharedPreference中取出personToken添加到表单中
        builder.add("id", activity.getSharedPreferences("user", Activity.MODE_PRIVATE).getString("personToken", ""));
        for (Map.Entry<String, String> entry : map.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }
        FormBody formBody = builder.build();
        Request request = new Request.Builder()
                //根路径拼接子路径
                .url(BASE_URL + url)
                .post(formBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e(TAG, e.getMessage());
                toast(activity);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    toast(activity);
                } else {
                    JsonResult jsonResult = new JsonResult();
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        jsonResult.setData(jsonObject.getString("data"));
                        jsonResult.setMessage(jsonObject.getString("message"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    requestSuccess(activity, jsonResult, iCallBack);
                }
            }
        });
    }

    /**
     * Post方式上传图片
     *
     * @param activity  Activity
     * @param url       根路径后面的子路径
     * @param id        id
     * @param file      图片文件
     * @param iCallBack 请求成功的回调
     */
    public void formFilePost(Activity activity, String url, String id, File file, final ICallBack iCallBack) {
        MediaType mediaType = MediaType.parse("image/png");
        RequestBody fileBody = RequestBody.create(file, mediaType);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "file", fileBody)
                .addFormDataPart("id", id)
                .build();
        Request request = new Request.Builder()
                .url(BASE_URL + url)
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d(TAG, e.getMessage());
                toast(activity);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    toast(activity);
                } else {
                    JsonResult result = new Gson().fromJson(response.body().string(), JsonResult.class);
                    requestSuccess(activity, result, iCallBack);
                }
            }
        });
    }

    /**
     * get请求（已自动添加personToken）
     *
     * @param activity  Activity
     * @param url       根路径后面的子路径
     * @param map       要发送的数据Map
     * @param iCallBack 请求成功的回调
     */
    public void get(Activity activity, String url, Map<String, String> map, final ICallBack iCallBack) {
        StringBuilder builder = new StringBuilder(url);
        //从SharedPreference中取出personToken拼接到url后面
        builder.append("?id=");
        builder.append(activity.getSharedPreferences("user", Activity.MODE_PRIVATE).getString("personToken", ""));
        for (Map.Entry<String, String> entry : map.entrySet()) {
            builder.append("&");
            builder.append(entry.getKey());
            builder.append("=");
            builder.append(entry.getValue());
        }
        Request request = new Request.Builder()
                //根路径拼接子路径
                .url(BASE_URL + builder.toString())
                .get()
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d(TAG, e.getMessage());
                toast(activity);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    toast(activity);
                } else {
                    JsonResult jsonResult = new JsonResult();
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        jsonResult.setData(jsonObject.getString("data"));
                        jsonResult.setMessage(jsonObject.getString("message"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    requestSuccess(activity, jsonResult, iCallBack);
                }
            }
        });
    }

    /*-------------------------------------------------------------------------------------------------------*/

    /**
     * 请求成功后，将获取的数据传递给回调方法
     * 回调方法中的内容直接运行在主线程中
     *
     * @param activity  Activity
     * @param result    JsonResult 拿到的数据
     * @param iCallBack 回调接口     */
    private static void requestSuccess(Activity activity, final JsonResult result, final ICallBack iCallBack) {
        activity.runOnUiThread(() -> {
            if (iCallBack != null) {
                iCallBack.onResponse(result);
            }
        });
    }

    private void toast(Activity activity, String message, final ICallBack iCallBack) {
        activity.runOnUiThread(() -> {
            if (iCallBack != null) {
                iCallBack.onFailure(message);
            }
        });
    }

    private void toast(Activity activity) {
        Looper.prepare();
        Toast.makeText(activity, "网络连接失败", Toast.LENGTH_SHORT).show();
        Looper.loop();
    }

}
