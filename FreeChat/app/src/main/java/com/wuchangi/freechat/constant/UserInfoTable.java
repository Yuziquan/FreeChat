package com.wuchangi.freechat.constant;

/**
 * Created by WuchangI on 2018/11/19.
 */


/**
 * 本地用户数据建表语句，保存的是在本机中成功登录过的用户信息，因为通过 当前APP实例 登录的用户可能不止一个
 */
public class UserInfoTable {

    public static final String TABLE_NAME = "table_account";

    public static final String COL_USER_NAME = "user_name";
    public static final String COL_NICK_NAME = "nick_name";
    public static final String COL_PHOTO = "photo";
    public static final String COL_EM_ID = "em_id";

    public static final String CREATE_TABLE = "CREATE TABLE "
            + TABLE_NAME + " ("
            + COL_EM_ID + " TEXT PRIMARY KEY, "
            + COL_USER_NAME + " TEXT, "
            + COL_NICK_NAME + " TEXT, "
            + COL_PHOTO + " TEXT);";
}
