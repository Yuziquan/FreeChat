package com.wuchangi.freechat.utils;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by WuchangI on 2018/11/19.
 */


/**
 * 系统自定义字体的工具类
 */
public class TypefaceUtils {

    private static TypefaceUtils sTypefaceUtils = new TypefaceUtils();
    private Context mContext;
    private Typeface typeface1;


    private TypefaceUtils() {
    }

    public static TypefaceUtils getInstance() {
        return sTypefaceUtils;
    }


    public void init(Context context) {
        mContext = context;
    }


    public Typeface getTypeface1() {
        return typeface1 == null ? Typeface.createFromAsset(mContext.getAssets(), "fonts/font1.ttf") : typeface1;
    }


}
