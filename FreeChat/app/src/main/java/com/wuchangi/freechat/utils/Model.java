package com.wuchangi.freechat.utils;

/**
 * Created by WuchangI on 2018/11/19.
 */

import android.content.Context;

import com.wuchangi.freechat.bean.UserInfo;
import com.wuchangi.freechat.db.ContactAndInvitationDBManager;
import com.wuchangi.freechat.db.biz.UserInfoTableDao;
import com.wuchangi.freechat.listener.GlobalEventListener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 数据模型层全局类
 */
public class Model {

    private static Model sModel = new Model();
    private Context mContext;
    private ExecutorService mExecutorService = Executors.newCachedThreadPool();
    private UserInfoTableDao mUserInfoTableDao;
    private ContactAndInvitationDBManager mContactAndInvitationDBManager;

    private Model() {

    }


    public static Model getInstance() {
        return sModel;
    }

    public void init(Context context) {
        mContext = context;
        mUserInfoTableDao = new UserInfoTableDao(mContext);

        // 开启全局监听
        GlobalEventListener globalEventListener = new GlobalEventListener(context);
    }


    /**
     * 获取全局线程池对象
     *
     * @return
     */
    public ExecutorService getGlobalThreadPool() {
        return mExecutorService;
    }

    public UserInfoTableDao getUserInfoTableDao() {
        return mUserInfoTableDao;
    }

    public ContactAndInvitationDBManager getContactAndInvitationDBManager() {
        return mContactAndInvitationDBManager;
    }


    /**
     * 用户登录成功后的处理
     *
     * @param userInfo
     */
    public void loginSuccess(UserInfo userInfo) {
        if (userInfo == null) {
            return;
        }

        if (mContactAndInvitationDBManager != null) {
            mContactAndInvitationDBManager.close();
        }

        // 创建一个名称为用户名的数据库
        mContactAndInvitationDBManager
                = new ContactAndInvitationDBManager(mContext, userInfo.getUserName());
    }
}
