package com.wuchangi.freechat.db.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.wuchangi.freechat.constant.ContactTable;
import com.wuchangi.freechat.constant.InvitationTable;

/**
 * Created by WuchangI on 2018/11/20.
 */

/**
 * 存放联系人信息、邀请信息的数据库
 */
public class ContactAndInvitationDbHelper extends SQLiteOpenHelper {

    /**
     * 不同的用户名，新建的数据库名 name 不同
     *
     * @param context
     * @param name    数据库名称，为了唯一标识特定用户使用的数据库，这里传入用户名
     */
    public ContactAndInvitationDbHelper(Context context, String name) {
        super(context, name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建联系人的表
        db.execSQL(ContactTable.CREATE_TABLE);

        // 创建邀请信息的表
        db.execSQL(InvitationTable.CREATE_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
