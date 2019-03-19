package com.wuchangi.freechat.bean;

/**
 * Created by WuchangI on 2018/11/19.
 */


/**
 * 本地用户个人信息
 */
public class UserInfo {

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 用户头像
     */
    private String photo;

    /**
     * 用户在环信服务器的id，这里简称“用户环信id”
     */
    private String EMId;


    public UserInfo() {
    }

    /**
     * 这里暂且使用 用户名 充当 用户环信id
     *
     * @param userName
     */
    public UserInfo(String userName) {
        this.userName = userName;
        this.EMId = userName;
    }

    public UserInfo(String userName, String nickName, String photo, String EMId) {
        this.userName = userName;
        this.nickName = userName;
        this.photo = userName;
        this.EMId = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getEMId() {
        return EMId;
    }

    public void setEMId(String EMId) {
        this.EMId = EMId;
    }
}
