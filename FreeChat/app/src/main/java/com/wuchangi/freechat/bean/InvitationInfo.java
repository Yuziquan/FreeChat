package com.wuchangi.freechat.bean;

/**
 * Created by WuchangI on 2018/11/20.
 */


/**
 * 邀请信息：包括联系人的邀请信息（比如别人邀请添加你为好友）、群组的邀请信息（别人给你发送群邀请）
 */
public class InvitationInfo {

    /**
     * 联系人
     */
    private UserInfo userInfo;

    /**
     * 群组
     */
    private GroupInfo groupInfo;

    /**
     * 邀请原因
     */
    private String reason;

    /**
     * 邀请状态
     */
    private InvitationStatus status;

    public InvitationInfo() {
    }

    public InvitationInfo(UserInfo userInfo, GroupInfo groupInfo, String reason, InvitationStatus status) {
        this.userInfo = userInfo;
        this.groupInfo = groupInfo;
        this.reason = reason;
        this.status = status;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public GroupInfo getGroupInfo() {
        return groupInfo;
    }

    public void setGroupInfo(GroupInfo groupInfo) {
        this.groupInfo = groupInfo;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public InvitationStatus getStatus() {
        return status;
    }

    public void setStatus(InvitationStatus status) {
        this.status = status;
    }


    public enum InvitationStatus {

        /**
         * 联系人邀请信息状态：A给B发送好友邀请
         */
        NEW_INVITE,             // B收到新邀请
        INVITE_ACCEPT,          // B接受邀请
        INVITE_ACCEPT_BY_PEER,  // A的好友邀请被接受


        /**
         * 群组邀请信息状态：
         * 1、A给B发送群邀请
         * 2、B为某群群主，C申请加入该群
         */
        NEW_GROUP_INVITE,           // 1、B收到群邀请
        NEW_GROUP_APPLICATION,      // 2、B收到C的群申请（C想要加入B的群）
        GROUP_INVITE_ACCEPTED,      // 1、B接受了A的群邀请，加入了群
        GROUP_APPLICATION_ACCEPTED, // 2、B同意C的群申请，同意C加入B的群
        GROUP_ACCEPT_INVITE,        // 接受了群邀请
        GROUP_ACCEPT_APPLICATION,   //批准的群加入申请
        GROUP_REJECT_INVITE,        //拒绝了群邀请
        GROUP_REJECT_APPLICATION,   //拒绝了群申请加入
        GROUP_INVITE_DECLINED,      // 1、B拒绝了群邀请
        GROUP_APPLICATION_DECLINED  // 2、C的群申请被拒绝，B不同意C加入B的群
    }

}
