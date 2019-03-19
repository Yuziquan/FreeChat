package com.wuchangi.freechat.bean;

/**
 * Created by WuchangI on 2018/11/22.
 */


/**
 * 联系人选择列表 的每个列表项包含的数据
 */
public class PickContactInfo {

    private UserInfo userInfo;
    private boolean isChecked;

    public PickContactInfo() {
    }

    public PickContactInfo(UserInfo userInfo, boolean isChecked) {
        this.userInfo = userInfo;
        this.isChecked = isChecked;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
