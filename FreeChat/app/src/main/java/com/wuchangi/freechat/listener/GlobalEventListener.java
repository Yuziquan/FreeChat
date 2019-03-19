package com.wuchangi.freechat.listener;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.hyphenate.EMContactListener;
import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMucSharedFile;
import com.wuchangi.freechat.bean.GroupInfo;
import com.wuchangi.freechat.bean.InvitationInfo;
import com.wuchangi.freechat.bean.UserInfo;
import com.wuchangi.freechat.constant.Constant;
import com.wuchangi.freechat.utils.Model;
import com.wuchangi.freechat.utils.SPUtils;

import java.util.List;

/**
 * Created by WuchangI on 2018/11/21.
 */


/**
 * 全局事件监听类
 */
public class GlobalEventListener
{
    private Context mContext;
    private LocalBroadcastManager mLBM;

    public GlobalEventListener(Context context)
    {
        mContext = context;

        mLBM = LocalBroadcastManager.getInstance(context);

        // 注册联系人变化的监听器
        EMClient.getInstance().contactManager().setContactListener(mEmContactListener);

        // 注册群信息变化的监听器
        EMClient.getInstance().groupManager().addGroupChangeListener(mEmGroupChangeListener);

    }


    private final EMContactListener mEmContactListener = new EMContactListener()
    {
        // 联系人增加后执行
        @Override
        public void onContactAdded(String userEMId)
        {
            // 数据库更新
            Model.getInstance().getContactAndInvitationDBManager().getContactTableDao().saveContact(new UserInfo(userEMId), true);

            // 发送联系人变化的广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_CHANGED));
        }

        // 联系人删除后执行
        @Override
        public void onContactDeleted(String userEMId)
        {
            // 数据库更新
            Model.getInstance().getContactAndInvitationDBManager().getContactTableDao().deleteContactByEMId(userEMId);
            Model.getInstance().getContactAndInvitationDBManager().getInvitationTableDao().deleteInvitationByUserEMId(userEMId);

            // 发送联系人变化的广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_CHANGED));
        }

        // 接收到联系人的好友邀请后执行
        @Override
        public void onContactInvited(String EMId, String reason)
        {
            // 数据库更新
            InvitationInfo invitationInfo = new InvitationInfo();

            invitationInfo.setUserInfo(new UserInfo(EMId));
            invitationInfo.setReason(reason);
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.NEW_INVITE);

            Model.getInstance().getContactAndInvitationDBManager().getInvitationTableDao().addInvitation(invitationInfo);


            // 红点显示
            SPUtils.getInstance().save(SPUtils.IS_NEW_INVITE, true);

            // 发送邀请信息变化的广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));
        }


        // 别人同意了你的好友邀请后执行
        @Override
        public void onFriendRequestAccepted(String EMId)
        {
            // 数据库更新
            InvitationInfo invitationInfo = new InvitationInfo();

            invitationInfo.setUserInfo(new UserInfo(EMId));
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER);

            Model.getInstance().getContactAndInvitationDBManager().getInvitationTableDao().addInvitation(invitationInfo);

            // 红点显示
            SPUtils.getInstance().save(SPUtils.IS_NEW_INVITE, true);

            // 发送邀请信息变化的广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));
        }

        // 别人拒绝了你的好友邀请后执行
        @Override
        public void onFriendRequestDeclined(String s)
        {
            // 红点显示
            SPUtils.getInstance().save(SPUtils.IS_NEW_INVITE, true);

            // 发送邀请信息变化的广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));
        }
    };


    private final EMGroupChangeListener mEmGroupChangeListener = new EMGroupChangeListener()
    {
        // 收到群邀请，别人邀请你加入他的群
        @Override
        public void onInvitationReceived(String groupId, String groupName, String inviter, String reason)
        {
            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setReason(reason);
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.NEW_GROUP_INVITE);
            invitationInfo.setGroupInfo(new GroupInfo(groupName, groupId, inviter));

            Model.getInstance().getContactAndInvitationDBManager().getInvitationTableDao().addInvitation(invitationInfo);

            SPUtils.getInstance().save(SPUtils.IS_NEW_INVITE, true);

            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        // 收到群申请，别人申请加入你所在的群
        @Override
        public void onRequestToJoinReceived(String groupId, String groupName, String applicant, String reason)
        {
            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setReason(reason);
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.NEW_GROUP_APPLICATION);
            invitationInfo.setGroupInfo(new GroupInfo(groupName, groupId, applicant));

            Model.getInstance().getContactAndInvitationDBManager().getInvitationTableDao().addInvitation(invitationInfo);

            SPUtils.getInstance().save(SPUtils.IS_NEW_INVITE, true);

            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        // 群申请被接受，别人同意你加入他的群
        @Override
        public void onRequestToJoinAccepted(String groupId, String groupName, String accepter)
        {
            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_APPLICATION_ACCEPTED);
            invitationInfo.setGroupInfo(new GroupInfo(groupName, groupId, accepter));

            Model.getInstance().getContactAndInvitationDBManager().getInvitationTableDao().addInvitation(invitationInfo);

            SPUtils.getInstance().save(SPUtils.IS_NEW_INVITE, true);

            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));

        }

        // 群申请被拒绝，别人拒绝你加入他的群
        @Override
        public void onRequestToJoinDeclined(String groupId, String groupName, String decliner, String reason)
        {
            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setReason(reason);
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_APPLICATION_DECLINED);
            invitationInfo.setGroupInfo(new GroupInfo(groupName, groupId, decliner));

            Model.getInstance().getContactAndInvitationDBManager().getInvitationTableDao().addInvitation(invitationInfo);

            SPUtils.getInstance().save(SPUtils.IS_NEW_INVITE, true);

            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        // 群邀请被同意，你邀请别人加入你的群，别人同意了
        @Override
        public void onInvitationAccepted(String groupId, String inviter, String reason)
        {
            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setReason(reason);
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_INVITE_ACCEPTED);
            invitationInfo.setGroupInfo(new GroupInfo(groupId, groupId, inviter));

            Model.getInstance().getContactAndInvitationDBManager().getInvitationTableDao().addInvitation(invitationInfo);

            SPUtils.getInstance().save(SPUtils.IS_NEW_INVITE, true);

            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }


        // 群邀请被拒绝，你邀请别人加入你的群，别人拒绝了
        @Override
        public void onInvitationDeclined(String groupId, String inviter, String reason)
        {
            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setReason(reason);
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_INVITE_DECLINED);
            invitationInfo.setGroupInfo(new GroupInfo(groupId, groupId, inviter));

            Model.getInstance().getContactAndInvitationDBManager().getInvitationTableDao().addInvitation(invitationInfo);

            SPUtils.getInstance().save(SPUtils.IS_NEW_INVITE, true);

            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        // 群成员被删除
        @Override
        public void onUserRemoved(String groupId, String groupName)
        {

        }


        // 群被解散
        @Override
        public void onGroupDestroyed(String groupId, String groupName)
        {

        }

        // 收到群邀请被自动接受，你发送的群邀请被别人自动接受了
        @Override
        public void onAutoAcceptInvitationFromGroup(String groupId, String inviter, String inviteMessage)
        {
            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setReason(inviteMessage);
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_INVITE_ACCEPTED);
            invitationInfo.setGroupInfo(new GroupInfo(groupId, groupId, inviter));

            Model.getInstance().getContactAndInvitationDBManager().getInvitationTableDao().addInvitation(invitationInfo);

            SPUtils.getInstance().save(SPUtils.IS_NEW_INVITE, true);

            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }


        @Override
        public void onMuteListAdded(String s, List<String> list, long l)
        {

        }

        @Override
        public void onMuteListRemoved(String s, List<String> list)
        {

        }

        @Override
        public void onAdminAdded(String s, String s1)
        {

        }

        @Override
        public void onAdminRemoved(String s, String s1)
        {

        }

        @Override
        public void onOwnerChanged(String s, String s1, String s2)
        {

        }

        @Override
        public void onMemberJoined(String s, String s1)
        {

        }

        @Override
        public void onMemberExited(String s, String s1)
        {

        }

        @Override
        public void onAnnouncementChanged(String s, String s1)
        {

        }

        @Override
        public void onSharedFileAdded(String s, EMMucSharedFile emMucSharedFile)
        {

        }

        @Override
        public void onSharedFileDeleted(String s, String s1)
        {

        }
    };


}
