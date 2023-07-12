package com.example.chatwebrtc.control;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;


/**
 * 辅助功能相关检查的帮助类
 */
public class AccessibilityUtil {

    private static final String ACCESSIBILITY_SERVICE_PATH = SimulatedClickService.class.getCanonicalName();

    /**
     * 判断是否有辅助功能权限
     *
     * @param context
     * @return
     */
    public static boolean isAccessibilitySettingsOn(Context context) {
        if (context == null) {
            return false;
        }

        int accessibilityEnabled = 0;
        try {
            accessibilityEnabled = Settings.Secure.getInt(context.getApplicationContext().getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.e("accessibilityEnabled_zrzr", "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        String packageName = context.getPackageName();
        final String serviceStr = packageName + "/" + ACCESSIBILITY_SERVICE_PATH;
        Log.e("=======serviceStr: ", serviceStr);
        if (accessibilityEnabled == 1) {
            TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

            String settingValue = Settings.Secure.getString(context.getApplicationContext().getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
                splitter.setString(settingValue);
                while (splitter.hasNext()) {
                    String accessabilityService = splitter.next();

                    if (accessabilityService.equalsIgnoreCase(serviceStr)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
