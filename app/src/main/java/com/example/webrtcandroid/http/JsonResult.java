package com.example.webrtcandroid.http;

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
public class JsonResult<T> {
    //状态 0 成功  1失败
    private int state;
    //主要数据
    private String data;
    //附加数据
    private String message;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String mmessagesg) {
        this.message = message;
    }
}
