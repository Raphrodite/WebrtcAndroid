package com.example.chatwebrtc.bean;

/**
 * * Copyright * 圣通电力
 *
 * @ProjectName: WebrtcAndroid
 * @Package: com.example.chatwebrtc.bean
 * @ClassName:
 * @Description:
 * @Author: Raphrodite
 * @CreateDate: 2023/5/25
 */
public class EventMessage {
    private int type;
    private String message;
    public EventMessage(int type, String message) {
        this.type = type;
        this.message = message;
    }
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public String getMessage() {
        return message;
    }public void setMessage(String message) {
        this.message = message;
    }
    @Override
    public String toString() {
        return "type="+type+"--message= "+message;
    }
}
