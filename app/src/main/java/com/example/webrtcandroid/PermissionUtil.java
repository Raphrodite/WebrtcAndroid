package com.example.webrtcandroid;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * * Copyright * 圣通电力
 *
 * @ProjectName: WebrtcAndroid
 * @Package: com.example.webrtcandroid
 * @ClassName: PermissionUtil
 * @Description:
 * @Author: Raphrodite
 * @CreateDate: 2023/2/15
 */
public class PermissionUtil {

    // 檢查是否有權限
    public static boolean isNeedRequestPermission(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return false;
        }
        return isNeedRequestPermission(activity, Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private static boolean isNeedRequestPermission(Activity activity, String... permissions) {
        List<String> mPermissionListDenied = new ArrayList<>();
        for (String permission : permissions) {
            int result = checkPermission(activity, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                mPermissionListDenied.add(permission);
            }
        }
        if (mPermissionListDenied.size() > 0) {
            String[] pears = new String[mPermissionListDenied.size()];
            pears = mPermissionListDenied.toArray(pears);
            ActivityCompat.requestPermissions(activity, pears, 0);
            return true;
        } else {
            return false;
        }
    }

    private static int checkPermission(Activity activity, String permission) {
        return ContextCompat.checkSelfPermission(activity, permission);
    }

}
