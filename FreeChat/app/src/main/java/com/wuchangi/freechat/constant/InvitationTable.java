package com.wuchangi.freechat.constant;

/**
 * Created by WuchangI on 2018/11/21.
 */

/**
 * 当前用户的相关邀请信息列表的建表语句
 */
public class InvitationTable {

    public static final String TABLE_NAME = "table_invitation";

    public static final String COL_USER_EM_ID = "user_em_id";
    public static final String COL_USER_NAME = "user_name";

    public static final String COL_GROUP_EM_ID = "group_em_id";
    public static final String COL_GROUP_NAME = "group_name";

    public static final String COL_REASON = "reason";
    public static final String COL_STATUS = "status";

    /**
     * 这里使用 用户的环信id 作为主键！
     */
    public static final String CREATE_TABLE = "CREATE TABLE "
            + TABLE_NAME + " ("
            + COL_USER_EM_ID + " TEXT PRIMARY KEY, "
            + COL_USER_NAME + " TEXT, "
            + COL_GROUP_EM_ID + " TEXT, "
            + COL_GROUP_NAME + " TEXT, "
            + COL_REASON + " TEXT, "
            + COL_STATUS + " INTEGER);";
}
