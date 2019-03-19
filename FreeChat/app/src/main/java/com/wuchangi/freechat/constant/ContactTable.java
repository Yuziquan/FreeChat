package com.wuchangi.freechat.constant;

/**
 * Created by WuchangI on 2018/11/20.
 */


/**
 * 当前用户的联系人列表的建表语句
 */
public class ContactTable {

    public static final String TABLE_NAME = "table_contact";

    public static final String COL_USER_NAME = "user_name";
    public static final String COL_NICK_NAME = "nick_name";
    public static final String COL_PHOTO = "photo";
    public static final String COL_EM_ID = "em_id";

    /**
     * 是否为当前用户的联系人，0 表示 不是，1 表示 是
     * （因为你加入一个群，群中不一定所有人都是你的联系人/好友，但是你却可以查看对方的信息）
     */
    public static final String COL_IS_CONTACT = "is_contact";

    public static final String CREATE_TABLE = "CREATE TABLE "
            + TABLE_NAME + " ("
            + COL_EM_ID + " TEXT PRIMARY KEY, "
            + COL_USER_NAME + " TEXT, "
            + COL_NICK_NAME + " TEXT, "
            + COL_PHOTO + " TEXT, "
            + COL_IS_CONTACT + " INTEGER);";
}
