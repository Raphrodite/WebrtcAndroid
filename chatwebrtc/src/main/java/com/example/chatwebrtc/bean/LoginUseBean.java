package com.example.chatwebrtc.bean;

import com.google.gson.annotations.SerializedName;

/**
 * * Copyright * 圣通电力
 *
 * @ProjectName: WebrtcAndroid
 * @Package: com.example.webrtcandroid.bean
 * @ClassName:
 * @Description: 客服登录返回的数据 在data中
 * @Author: Raphrodite
 * @CreateDate: 2023/5/22
 */
public class LoginUseBean {


    @SerializedName("stunConfig")
    private StunConfigDTO stunConfig;
    @SerializedName("token")
    private String token;

    public StunConfigDTO getStunConfig() {
        return stunConfig;
    }

    public void setStunConfig(StunConfigDTO stunConfig) {
        this.stunConfig = stunConfig;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public static class StunConfigDTO {
        @SerializedName("address")
        private String address;
        @SerializedName("password")
        private String password;
        @SerializedName("username")
        private String username;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }
}
