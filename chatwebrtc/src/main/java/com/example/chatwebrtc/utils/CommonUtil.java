package com.example.chatwebrtc.utils;

import android.content.Context;

import com.blankj.utilcode.util.ShellUtils;

/**
 * * Copyright * 圣通电力
 *
 * @ProjectName: WebrtcAndroid
 * @Package: com.example.chatwebrtc.utils
 * @ClassName:
 * @Description:
 * @Author: Raphrodite
 * @CreateDate: 2023/7/11
 */
public class CommonUtil {

    /**
     * 开启辅助功能
     *
     * @param ct
     * @param service
     */
    public static void openServicePermissonRoot(Context ct, Class service) {
        String cmd1 = "settings put secure enabled_accessibility_services " + ct.getPackageName() + "/" + service.getName();
        String cmd2 = "settings put secure accessibility_enabled 1";
        String[] cmds = new String[]{cmd1, cmd2};
        ShellUtils.execCmd(cmds, true);
    }

}
