package com.wuchangi.freechat.bean;

/**
 * Created by WuchangI on 2018/11/20.
 */

/**
 * 群组信息
 */
public class GroupInfo {

    /**
     * 群组名
     */
    private String groupName;

    /**
     * 群组id
     */
    private String groupEMId;

    /**
     * 邀请人
     */
    private String invitePerson;

    public GroupInfo() {
    }

    public GroupInfo(String groupName, String groupEMId, String invitePerson) {
        this.groupName = groupName;
        this.groupEMId = groupEMId;
        this.invitePerson = invitePerson;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupEMId() {
        return groupEMId;
    }

    public void setGroupEMId(String groupEMId) {
        this.groupEMId = groupEMId;
    }

    public String getInvitePerson() {
        return invitePerson;
    }

    public void setInvitePerson(String invitePerson) {
        this.invitePerson = invitePerson;
    }

}
