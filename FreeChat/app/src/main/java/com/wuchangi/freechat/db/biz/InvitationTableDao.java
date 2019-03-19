package com.wuchangi.freechat.db.biz;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wuchangi.freechat.bean.GroupInfo;
import com.wuchangi.freechat.bean.InvitationInfo;
import com.wuchangi.freechat.bean.UserInfo;
import com.wuchangi.freechat.constant.InvitationTable;
import com.wuchangi.freechat.db.helper.ContactAndInvitationDbHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WuchangI on 2018/11/21.
 */

public class InvitationTableDao {

    /**
     * 待操作的数据库
     */
    private final ContactAndInvitationDbHelper mHelper;

    public InvitationTableDao(ContactAndInvitationDbHelper contactAndInvitationDbHelper) {
        mHelper = contactAndInvitationDbHelper;
    }


    public void addInvitation(InvitationInfo invitationInfo) {
        if (invitationInfo == null) {
            return;
        }

        SQLiteDatabase db = mHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(InvitationTable.COL_REASON, invitationInfo.getReason());
        values.put(InvitationTable.COL_STATUS, invitationInfo.getStatus().ordinal());


        UserInfo userInfo = invitationInfo.getUserInfo();
        GroupInfo groupInfo = invitationInfo.getGroupInfo();

        // 为联系人的邀请信息
        if (userInfo != null) {
            values.put(InvitationTable.COL_USER_EM_ID, userInfo.getEMId());
            values.put(InvitationTable.COL_USER_NAME, userInfo.getUserName());
        }
        // 为群组的邀请信息
        else {
            values.put(InvitationTable.COL_GROUP_EM_ID, groupInfo.getGroupEMId());
            values.put(InvitationTable.COL_GROUP_NAME, groupInfo.getGroupName());

            // 将群组的邀请人存进作为主键的COL_USER_EM_ID中，确保COL_USER_EM_ID不为空
            values.put(InvitationTable.COL_USER_EM_ID, groupInfo.getInvitePerson());
        }

        db.replace(InvitationTable.TABLE_NAME, null, values);
    }


    public List<InvitationInfo> getInvitationList() {
        SQLiteDatabase db = mHelper.getReadableDatabase();

        String sql = "SELECT * FROM " + InvitationTable.TABLE_NAME;
        Cursor cursor = db.rawQuery(sql, null);

        List<InvitationInfo> invitationInfoList = new ArrayList<>();

        while (cursor.moveToNext()) {
            InvitationInfo invitationInfo = new InvitationInfo();

            invitationInfo.setReason(cursor.getString(cursor.getColumnIndex(InvitationTable.COL_REASON)));
            invitationInfo.setStatus(int2InvitationStatus(cursor.getInt(cursor.getColumnIndex(InvitationTable.COL_STATUS))));

            String groupEMId = cursor.getString(cursor.getColumnIndex(InvitationTable.COL_GROUP_EM_ID));

            // 为联系人的邀请信息
            if (groupEMId == null) {
                UserInfo userInfo = new UserInfo();
                userInfo.setEMId(cursor.getString(cursor.getColumnIndex(InvitationTable.COL_USER_EM_ID)));
                userInfo.setUserName(cursor.getString(cursor.getColumnIndex(InvitationTable.COL_USER_NAME)));

                invitationInfo.setUserInfo(userInfo);
            }
            // 为群组的邀请信息
            else {
                GroupInfo groupInfo = new GroupInfo();
                groupInfo.setGroupEMId(cursor.getString(cursor.getColumnIndex(InvitationTable.COL_GROUP_EM_ID)));
                groupInfo.setGroupName(cursor.getString(cursor.getColumnIndex(InvitationTable.COL_GROUP_NAME)));

                // 注意：群组的邀请人存放在COL_USER_EM_ID列
                groupInfo.setInvitePerson(cursor.getString(cursor.getColumnIndex(InvitationTable.COL_USER_EM_ID)));

                invitationInfo.setGroupInfo(groupInfo);
            }

            invitationInfoList.add(invitationInfo);
        }

        cursor.close();

        return invitationInfoList;
    }

    public void deleteInvitationByUserEMId(String userEMId) {
        SQLiteDatabase db = mHelper.getWritableDatabase();

        db.delete(InvitationTable.TABLE_NAME, InvitationTable.COL_USER_EM_ID + "=?", new String[]{userEMId});
    }

    public void updateInvitationStatus(String userEMId, InvitationInfo.InvitationStatus invitationStatus) {
        SQLiteDatabase db = mHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(InvitationTable.COL_STATUS, invitationStatus.ordinal());

        db.update(InvitationTable.TABLE_NAME, values, InvitationTable.COL_USER_EM_ID + "=?", new String[]{userEMId});
    }


    public InvitationInfo.InvitationStatus int2InvitationStatus(int intStatus) {

        if (intStatus == InvitationInfo.InvitationStatus.NEW_INVITE.ordinal()) {
            return InvitationInfo.InvitationStatus.NEW_INVITE;
        } else if (intStatus == InvitationInfo.InvitationStatus.INVITE_ACCEPT.ordinal()) {
            return InvitationInfo.InvitationStatus.INVITE_ACCEPT;
        } else if (intStatus == InvitationInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER.ordinal()) {
            return InvitationInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER;
        } else if (intStatus == InvitationInfo.InvitationStatus.NEW_GROUP_INVITE.ordinal()) {
            return InvitationInfo.InvitationStatus.NEW_GROUP_INVITE;
        } else if (intStatus == InvitationInfo.InvitationStatus.NEW_GROUP_APPLICATION.ordinal()) {
            return InvitationInfo.InvitationStatus.NEW_GROUP_APPLICATION;
        } else if (intStatus == InvitationInfo.InvitationStatus.GROUP_INVITE_ACCEPTED.ordinal()) {
            return InvitationInfo.InvitationStatus.GROUP_INVITE_ACCEPTED;
        } else if (intStatus == InvitationInfo.InvitationStatus.GROUP_APPLICATION_ACCEPTED.ordinal()) {
            return InvitationInfo.InvitationStatus.GROUP_APPLICATION_ACCEPTED;
        } else if (intStatus == InvitationInfo.InvitationStatus.GROUP_ACCEPT_INVITE.ordinal()) {
            return InvitationInfo.InvitationStatus.GROUP_ACCEPT_INVITE;
        } else if (intStatus == InvitationInfo.InvitationStatus.GROUP_ACCEPT_APPLICATION.ordinal()) {
            return InvitationInfo.InvitationStatus.GROUP_ACCEPT_APPLICATION;
        } else if (intStatus == InvitationInfo.InvitationStatus.GROUP_REJECT_INVITE.ordinal()) {
            return InvitationInfo.InvitationStatus.GROUP_REJECT_INVITE;
        } else if (intStatus == InvitationInfo.InvitationStatus.GROUP_REJECT_APPLICATION.ordinal()) {
            return InvitationInfo.InvitationStatus.GROUP_REJECT_APPLICATION;
        } else if (intStatus == InvitationInfo.InvitationStatus.GROUP_INVITE_DECLINED.ordinal()) {
            return InvitationInfo.InvitationStatus.GROUP_INVITE_DECLINED;
        } else if (intStatus == InvitationInfo.InvitationStatus.GROUP_APPLICATION_DECLINED.ordinal()) {
            return InvitationInfo.InvitationStatus.GROUP_APPLICATION_DECLINED;
        } else {
            return null;
        }
    }

}
