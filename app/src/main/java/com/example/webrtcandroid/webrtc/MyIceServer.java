package com.example.webrtcandroid.webrtc;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * * Copyright * 圣通电力
 *
 * @ProjectName: WebrtcAndroid
 * @Package: com.example.webrtcandroid.webrtc
 * @ClassName: MyIceServer
 * @Description: 信令服务器bean
 * @Author: Raphrodite
 * @CreateDate: 2023/2/14
 */
public class MyIceServer implements Parcelable {

    public final String uri;
    public final String username;
    public final String password;

    public MyIceServer(String uri) {
        this(uri, "", "");
    }

    public MyIceServer(String uri, String username, String password) {
        this.uri = uri;
        this.username = username;
        this.password = password;
    }

    protected MyIceServer(Parcel in) {
        uri = in.readString();
        username = in.readString();
        password = in.readString();
    }

    public static final Creator<MyIceServer> CREATOR = new Creator<MyIceServer>() {
        @Override
        public MyIceServer createFromParcel(Parcel in) {
            return new MyIceServer(in);
        }

        @Override
        public MyIceServer[] newArray(int size) {
            return new MyIceServer[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uri);
        dest.writeString(username);
        dest.writeString(password);
    }
}
