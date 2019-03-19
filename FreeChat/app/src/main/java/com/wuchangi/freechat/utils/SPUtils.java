package com.wuchangi.freechat.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.wuchangi.freechat.base.MyIMApplication;

/**
 * Created by WuchangI on 2018/11/21.
 */

/**
 * 使用 SharedPreferences 获取和保存数据的工具类
 */
public class SPUtils {

    /**
     * 新的邀请的红点标记
     */
    public static final String IS_NEW_INVITE = "is_new_invite";

    private static SPUtils sSpUtils = new SPUtils();
    private static SharedPreferences sSp;

    private SPUtils() {
    }

    public static SPUtils getInstance() {
        if (sSp == null) {
            sSp = MyIMApplication.getGlobalApplication().getSharedPreferences("MyIM", Context.MODE_PRIVATE);
        }

        return sSpUtils;
    }


    public void save(String key, Object value) {
        if (value instanceof String) {
            sSp.edit().putString(key, (String) value).commit();
        } else if (value instanceof Integer) {
            sSp.edit().putInt(key, (Integer) value).commit();
        } else if (value instanceof Boolean) {
            sSp.edit().putBoolean(key, (Boolean) value).commit();
        }
    }

    public String getString(String key, String defaultValue) {
        return sSp.getString(key, defaultValue);
    }

    public int getInt(String key, int defaultValue) {
        return sSp.getInt(key, defaultValue);
    }

    public Boolean getBoolean(String key, Boolean defaultValue) {
        return sSp.getBoolean(key, defaultValue);
    }
}
