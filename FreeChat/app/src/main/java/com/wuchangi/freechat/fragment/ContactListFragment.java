package com.wuchangi.freechat.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.ui.EaseContactListFragment;
import com.hyphenate.exceptions.HyphenateException;
import com.wuchangi.freechat.R;
import com.wuchangi.freechat.activity.AddNewFriendActivity;
import com.wuchangi.freechat.activity.ChatActivity;
import com.wuchangi.freechat.activity.GroupListActivity;
import com.wuchangi.freechat.activity.InvitationListActivity;
import com.wuchangi.freechat.bean.UserInfo;
import com.wuchangi.freechat.constant.Constant;
import com.wuchangi.freechat.utils.Model;
import com.wuchangi.freechat.utils.SPUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by WuchangI on 2018/11/20.
 */

public class ContactListFragment extends EaseContactListFragment
{
    private ImageView mIvContactRedDot;

    private LinearLayout mLlNewFriend;

    private LinearLayout mLlGroupChat;

    private LocalBroadcastManager mLBM;

    private String userEMId;

    private BroadcastReceiver mContactInviteChangeReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            // 显示红点
            mIvContactRedDot.setVisibility(View.VISIBLE);
            SPUtils.getInstance().save(SPUtils.IS_NEW_INVITE, true);
        }
    };

    private BroadcastReceiver mContactChangeReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            refreshContactList();
        }
    };
    private BroadcastReceiver mGroupChangeReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            // 显示红点
            mIvContactRedDot.setVisibility(View.VISIBLE);
            SPUtils.getInstance().save(SPUtils.IS_NEW_INVITE, true);
        }
    };

    @Override
    protected void initView()
    {
        super.initView();

        titleBar.setRightImageResource(R.drawable.add);

        View contactListFragmentHeader = View.inflate(getActivity(), R.layout.fragment_contact_list_header, null);
        listView.addHeaderView(contactListFragmentHeader);

        mIvContactRedDot = (ImageView) contactListFragmentHeader.findViewById(R.id.iv_contact_red_dot);
        mLlNewFriend = (LinearLayout) contactListFragmentHeader.findViewById(R.id.ll_new_friend);
        mLlGroupChat = (LinearLayout) contactListFragmentHeader.findViewById(R.id.ll_group_chat);


        setContactListItemClickListener(new EaseContactListItemClickListener()
        {
            @Override
            public void onListItemClicked(EaseUser user)
            {
                if (user == null)
                {
                    return;
                }

                ChatActivity.actionStart(getActivity(), user.getUsername());
            }
        });
    }

    @Override
    protected void setUpView()
    {
        super.setUpView();

        titleBar.setRightLayoutClickListener(v ->
        {
            AddNewFriendActivity.actionStart(ContactListFragment.this.getActivity());
        });

        boolean isNewInvite = SPUtils.getInstance().getBoolean(SPUtils.IS_NEW_INVITE, false);

        mIvContactRedDot.setVisibility(isNewInvite ? View.VISIBLE : View.INVISIBLE);

        mLBM = LocalBroadcastManager.getInstance(getActivity());
        mLBM.registerReceiver(mContactInviteChangeReceiver, new IntentFilter(Constant.CONTACT_INVITE_CHANGED));
        mLBM.registerReceiver(mContactChangeReceiver, new IntentFilter(Constant.CONTACT_CHANGED));
        mLBM.registerReceiver(mGroupChangeReceiver, new IntentFilter(Constant.GROUP_INVITE_CHANGED));


        mLlNewFriend.setOnClickListener(v ->
        {
            mIvContactRedDot.setVisibility(View.INVISIBLE);
            SPUtils.getInstance().save(SPUtils.IS_NEW_INVITE, false);

            InvitationListActivity.actionStart(getActivity());
        });


        mLlGroupChat.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                GroupListActivity.actionStart(getActivity());
            }
        });

        getContactListFromEM();

        // 为联系人列表listView注册上下文菜单
        registerForContextMenu(listView);
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);

        int position = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;

        EaseUser easeUser = (EaseUser) listView.getItemAtPosition(position);
        userEMId = easeUser.getUsername();

        // 加载删除联系人的上下文菜单的界面布局
        getActivity().getMenuInflater().inflate(R.menu.contact_delete, menu);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.contact_delete)
        {

            deleteContact();

            return true;
        }

        return super.onContextItemSelected(item);
    }

    // 删除选中的联系人
    private void deleteContact()
    {
        Model.getInstance().getGlobalThreadPool().execute(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    EMClient.getInstance().contactManager().deleteContact(userEMId);

                    Model.getInstance().getContactAndInvitationDBManager().getContactTableDao().deleteContactByEMId(userEMId);

                    if (getActivity() == null)
                    {
                        return;
                    }

                    getActivity().runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Toast.makeText(getActivity(), getResources().getString(R.string.delete) + userEMId + getResources().getString(R.string.success), Toast.LENGTH_SHORT).show();

                            refreshContactList();
                        }
                    });
                }
                catch (HyphenateException e)
                {
                    e.printStackTrace();

                    if (getActivity() == null)
                    {
                        return;
                    }

                    getActivity().runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Toast.makeText(getActivity(), getResources().getString(R.string.delete) + userEMId + getResources().getString(R.string.failure), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });
    }

    /**
     * 去环信服务器获取所有联系人信息
     */
    private void getContactListFromEM()
    {
        Model.getInstance().getGlobalThreadPool().execute(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    // 当前用户的所有好友的环信id的列表
                    List<String> EMidList = EMClient.getInstance().contactManager().getAllContactsFromServer();

                    if (EMidList != null && EMidList.size() >= 0)
                    {
                        List<UserInfo> contactList = new ArrayList<>();

                        for (String EMId : EMidList)
                        {
                            UserInfo userInfo = new UserInfo(EMId);
                            contactList.add(userInfo);
                        }

                        Model.getInstance().getContactAndInvitationDBManager().getContactTableDao().saveContactList(contactList, true);

                        if (getActivity() == null)
                        {
                            return;
                        }

                        getActivity().runOnUiThread(() -> refreshContactList());
                    }
                }
                catch (HyphenateException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }


    private void refreshContactList()
    {
        List<UserInfo> contactList = Model.getInstance().getContactAndInvitationDBManager().getContactTableDao().getContactList();

        if (contactList != null && contactList.size() >= 0)
        {
            Map<String, EaseUser> contactListMap = new HashMap<>();

            for (UserInfo contact : contactList)
            {
                EaseUser easeUser = new EaseUser(contact.getEMId());

                contactListMap.put(contact.getEMId(), easeUser);
            }

            setContactsMap(contactListMap);

            refresh();
        }
    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();
        mLBM.unregisterReceiver(mContactInviteChangeReceiver);
        mLBM.unregisterReceiver(mContactChangeReceiver);
        mLBM.unregisterReceiver(mGroupChangeReceiver);
    }
}
