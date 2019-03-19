package com.wuchangi.freechat.db.biz;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wuchangi.freechat.bean.UserInfo;
import com.wuchangi.freechat.constant.ContactTable;
import com.wuchangi.freechat.db.helper.ContactAndInvitationDbHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WuchangI on 2018/11/20.
 */

public class ContactTableDao {

    /**
     * 待操作的数据库
     */
    private final ContactAndInvitationDbHelper mHelper;

    public ContactTableDao(ContactAndInvitationDbHelper contactAndInvitationDbHelper) {
        mHelper = contactAndInvitationDbHelper;
    }


    public List<UserInfo> getContactList() {
        SQLiteDatabase db = mHelper.getReadableDatabase();

        String sql = "SELECT * FROM " + ContactTable.TABLE_NAME + " WHERE " + ContactTable.COL_IS_CONTACT + "=1";

        Cursor cursor = db.rawQuery(sql, null);

        List<UserInfo> userInfoList = new ArrayList<>();

        while (cursor.moveToNext()) {
            UserInfo userInfo = new UserInfo();

            userInfo.setEMId(cursor.getString(cursor.getColumnIndex(ContactTable.COL_EM_ID)));
            userInfo.setUserName(cursor.getString(cursor.getColumnIndex(ContactTable.COL_USER_NAME)));
            userInfo.setNickName(cursor.getString(cursor.getColumnIndex(ContactTable.COL_NICK_NAME)));
            userInfo.setPhoto(cursor.getString(cursor.getColumnIndex(ContactTable.COL_PHOTO)));

            userInfoList.add(userInfo);
        }

        cursor.close();

        return userInfoList;
    }


    public UserInfo getContactByEMId(String EMId) {
        SQLiteDatabase db = mHelper.getReadableDatabase();

        String sql = "SELECT * FROM " + ContactTable.TABLE_NAME + " WHERE " + ContactTable.COL_EM_ID + "=?";

        Cursor cursor = db.rawQuery(sql, new String[]{EMId});

        UserInfo contactInfo = null;

        if (cursor.moveToNext()) {
            contactInfo = new UserInfo();

            contactInfo.setEMId(cursor.getString(cursor.getColumnIndex(ContactTable.COL_EM_ID)));
            contactInfo.setUserName(cursor.getString(cursor.getColumnIndex(ContactTable.COL_USER_NAME)));
            contactInfo.setNickName(cursor.getString(cursor.getColumnIndex(ContactTable.COL_NICK_NAME)));
            contactInfo.setPhoto(cursor.getString(cursor.getColumnIndex(ContactTable.COL_PHOTO)));
        }

        cursor.close();

        return contactInfo;
    }


    public List<UserInfo> getContactListByEMIdList(List<String> EMIdList) {
        if (EMIdList == null || EMIdList.size() <= 0) {
            return null;
        }

        List<UserInfo> contactInfoList = new ArrayList<>();

        for (String EMId : EMIdList) {
            UserInfo contactInfo = getContactByEMId(EMId);
            contactInfoList.add(contactInfo);
        }

        return contactInfoList;
    }


    public void saveContact(UserInfo userInfo, boolean isMyContact) {
        if (userInfo == null) {
            return;
        }

        SQLiteDatabase db = mHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ContactTable.COL_EM_ID, userInfo.getEMId());
        values.put(ContactTable.COL_USER_NAME, userInfo.getUserName());
        values.put(ContactTable.COL_NICK_NAME, userInfo.getNickName());
        values.put(ContactTable.COL_PHOTO, userInfo.getPhoto());
        values.put(ContactTable.COL_IS_CONTACT, isMyContact ? 1 : 0);

        db.replace(ContactTable.TABLE_NAME, null, values);
    }

    public void saveContactList(List<UserInfo> userInfoList, boolean isMyContact) {
        if (userInfoList == null || userInfoList.size() <= 0) {
            return;
        }

        for (UserInfo userInfo : userInfoList) {
            saveContact(userInfo, isMyContact);
        }
    }


    public void deleteContactByEMId(String EMId) {
        SQLiteDatabase db = mHelper.getWritableDatabase();

        db.delete(ContactTable.TABLE_NAME, ContactTable.COL_EM_ID + "=?", new String[]{EMId});
    }
}
