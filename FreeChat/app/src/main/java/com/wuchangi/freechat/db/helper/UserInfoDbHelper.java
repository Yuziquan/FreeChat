package com.wuchangi.freechat.db.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.wuchangi.freechat.constant.UserInfoTable;

/**
 * Created by WuchangI on 2018/11/19.
 */

/**
 * 存放已在 当前APP实例 成功登录过的用户的信息的数据库
 */
public class UserInfoDbHelper extends SQLiteOpenHelper {

    // 默认的数据库存储文件为 user_account.db
    public UserInfoDbHelper(Context context) {
        super(context, "user_account.db", null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(UserInfoTable.CREATE_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
