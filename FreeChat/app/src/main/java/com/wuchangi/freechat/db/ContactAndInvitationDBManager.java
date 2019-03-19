package com.wuchangi.freechat.db;

/**
 * Created by WuchangI on 2018/11/21.
 */

import android.content.Context;

import com.wuchangi.freechat.db.biz.ContactTableDao;
import com.wuchangi.freechat.db.biz.InvitationTableDao;
import com.wuchangi.freechat.db.helper.ContactAndInvitationDbHelper;

/**
 * 统一管理ContactTableDao和InvitationTableDao的一个管理类
 */
public class ContactAndInvitationDBManager {

    private final ContactAndInvitationDbHelper mDbHelper;

    private final ContactTableDao mContactTableDao;

    private final InvitationTableDao mInvitationTableDao;


    public ContactAndInvitationDBManager(Context context, String databaseName) {
        // 创建数据库
        mDbHelper = new ContactAndInvitationDbHelper(context, databaseName);

        // 创建这个数据库中两张表的操作类
        mContactTableDao = new ContactTableDao(mDbHelper);
        mInvitationTableDao = new InvitationTableDao(mDbHelper);
    }


    public ContactTableDao getContactTableDao() {
        return mContactTableDao;
    }

    public InvitationTableDao getInvitationTableDao() {
        return mInvitationTableDao;
    }


    public void close() {
        mDbHelper.close();
    }
}
