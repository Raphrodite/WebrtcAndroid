package com.example.chatwebrtc.bean;

import com.google.gson.annotations.SerializedName;

/**
 * * Copyright * 圣通电力
 *
 * @ProjectName: WebrtcAndroid
 * @Package: com.example.chatwebrtc.bean
 * @ClassName:
 * @Description:
 * @Author: Raphrodite
 * @CreateDate: 2023/7/11
 */
public class MouseEventBean {

    @SerializedName("type")
    private String type;
    @SerializedName("startPoint")
    private StartPointDTO startPoint;
    @SerializedName("endPoint")
    private EndPointDTO endPoint;
    @SerializedName("screenSize")
    private ScreenSizeDTO screenSize;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public StartPointDTO getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(StartPointDTO startPoint) {
        this.startPoint = startPoint;
    }

    public EndPointDTO getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(EndPointDTO endPoint) {
        this.endPoint = endPoint;
    }

    public ScreenSizeDTO getScreenSize() {
        return screenSize;
    }

    public void setScreenSize(ScreenSizeDTO screenSize) {
        this.screenSize = screenSize;
    }

    public static class StartPointDTO {
        @SerializedName("pointX")
        private Integer pointX;
        @SerializedName("pointY")
        private Integer pointY;

        public Integer getPointX() {
            return pointX;
        }

        public void setPointX(Integer pointX) {
            this.pointX = pointX;
        }

        public Integer getPointY() {
            return pointY;
        }

        public void setPointY(Integer pointY) {
            this.pointY = pointY;
        }
    }

    public static class EndPointDTO {
        @SerializedName("pointX")
        private Integer pointX;
        @SerializedName("pointY")
        private Integer pointY;

        public Integer getPointX() {
            return pointX;
        }

        public void setPointX(Integer pointX) {
            this.pointX = pointX;
        }

        public Integer getPointY() {
            return pointY;
        }

        public void setPointY(Integer pointY) {
            this.pointY = pointY;
        }
    }

    public static class ScreenSizeDTO {
        @SerializedName("width")
        private Integer width;
        @SerializedName("height")
        private Integer height;

        public Integer getWidth() {
            return width;
        }

        public void setWidth(Integer width) {
            this.width = width;
        }

        public Integer getHeight() {
            return height;
        }

        public void setHeight(Integer height) {
            this.height = height;
        }
    }
}
