package com.wuchangi.freechat.db.biz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wuchangi.freechat.bean.UserInfo;
import com.wuchangi.freechat.constant.UserInfoTable;
import com.wuchangi.freechat.db.helper.UserInfoDbHelper;

/**
 * Created by WuchangI on 2018/11/19.
 */

public class UserInfoTableDao {

    /**
     * 待操作的数据库
     */
    private final UserInfoDbHelper mHelper;

    public UserInfoTableDao(Context context) {
        mHelper = new UserInfoDbHelper(context);
    }


    public void addUserInfo(UserInfo userInfo) {
        SQLiteDatabase db = mHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(UserInfoTable.COL_EM_ID, userInfo.getEMId());
        values.put(UserInfoTable.COL_USER_NAME, userInfo.getUserName());
        values.put(UserInfoTable.COL_NICK_NAME, userInfo.getNickName());
        values.put(UserInfoTable.COL_PHOTO, userInfo.getPhoto());

        db.replace(UserInfoTable.TABLE_NAME, null, values);
    }



    public UserInfo getUserInfoByEMId(String EMId) {
        SQLiteDatabase db = mHelper.getReadableDatabase();

        String sql = "SELECT * FROM " + UserInfoTable.TABLE_NAME
                + " WHERE " + UserInfoTable.COL_EM_ID + "=?";

        Cursor cursor = db.rawQuery(sql, new String[]{EMId});

        UserInfo userInfo = null;

        if (cursor.moveToNext()) {
            userInfo = new UserInfo();
            userInfo.setEMId(cursor.getString(cursor.getColumnIndex(UserInfoTable.COL_EM_ID)));
            userInfo.setUserName(cursor.getString(cursor.getColumnIndex(UserInfoTable.COL_USER_NAME)));
            userInfo.setNickName(cursor.getString(cursor.getColumnIndex(UserInfoTable.COL_NICK_NAME)));
            userInfo.setPhoto(cursor.getString(cursor.getColumnIndex(UserInfoTable.COL_PHOTO)));
        }

        cursor.close();

        return userInfo;
    }

}
