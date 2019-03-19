package com.wuchangi.freechat.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.wuchangi.freechat.R;
import com.wuchangi.freechat.adapter.InvitationListViewAdapter;
import com.wuchangi.freechat.bean.InvitationInfo;
import com.wuchangi.freechat.constant.Constant;
import com.wuchangi.freechat.utils.Model;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InvitationListActivity extends AppCompatActivity
{
    @BindView(R.id.lv_invitation_list)
    ListView mLvInvitationList;

    private InvitationListViewAdapter mInvitationListViewAdapter;

    private LocalBroadcastManager mLBM;

    /***
     * 监听联系人邀请和群邀请信息的变化
     */
    private BroadcastReceiver mInviteChangeReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            refreshLvInvitationList();
        }
    };

    private InvitationListViewAdapter.OnInviteListener mOnInviteListener = new InvitationListViewAdapter.OnInviteListener()
    {
        @Override
        public void onAccept(InvitationInfo invitationInfo)
        {
            // 通知环信服务器已经接受了好友邀请
            Model.getInstance().getGlobalThreadPool().execute(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        EMClient.getInstance().contactManager().acceptInvitation(invitationInfo.getUserInfo().getEMId());

                        Model.getInstance().getContactAndInvitationDBManager().
                                getInvitationTableDao().updateInvitationStatus(invitationInfo.getUserInfo().getEMId(),
                                InvitationInfo.InvitationStatus.INVITE_ACCEPT);

                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                Toast.makeText(InvitationListActivity.this, getResources().getString(R.string.accept_a_invitation), Toast.LENGTH_SHORT).show();

                                refreshLvInvitationList();
                            }
                        });
                    }
                    catch (HyphenateException e)
                    {
                        e.printStackTrace();

                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                Toast.makeText(InvitationListActivity.this, getResources().getString(R.string.accept_a_invitation_failure), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }

        @Override
        public void onReject(InvitationInfo invitationInfo)
        {
            // 通知环信服务器已经拒绝了好友邀请
            Model.getInstance().getGlobalThreadPool().execute(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        EMClient.getInstance().contactManager().declineInvitation(invitationInfo.getUserInfo().getEMId());

                        Model.getInstance().getContactAndInvitationDBManager().getInvitationTableDao().deleteInvitationByUserEMId(invitationInfo.getUserInfo().getEMId());

                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                Toast.makeText(InvitationListActivity.this, getResources().getString(R.string.reject_a_invitation), Toast.LENGTH_SHORT).show();

                                refreshLvInvitationList();
                            }
                        });
                    }
                    catch (HyphenateException e)
                    {
                        e.printStackTrace();

                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                Toast.makeText(InvitationListActivity.this, getResources().getString(R.string.reject_a_invitation_failure), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }
            });
        }

        /**
         * 接受群邀请按钮的点击事件
         *
         * @param invitationInfo
         */
        @Override
        public void onGroupInvitationAccept(InvitationInfo invitationInfo)
        {
            // 通知环信服务器已经接受了群邀请
            Model.getInstance().getGlobalThreadPool().execute(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        EMClient.getInstance().groupManager().acceptInvitation(invitationInfo.getGroupInfo().getGroupEMId(), invitationInfo.getGroupInfo().getInvitePerson());

                        invitationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_ACCEPT_INVITE);
                        Model.getInstance().getContactAndInvitationDBManager().getInvitationTableDao().addInvitation(invitationInfo);

                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                Toast.makeText(InvitationListActivity.this, getResources().getString(R.string.accept_group_invitation), Toast.LENGTH_SHORT).show();

                                refreshLvInvitationList();
                            }
                        });
                    }
                    catch (HyphenateException e)
                    {
                        e.printStackTrace();

                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                Toast.makeText(InvitationListActivity.this, getResources().getString(R.string.accept_group_invitation_failure), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                }
            });
        }


        /**
         * 拒绝群邀请按钮的点击事件
         *
         * @param invitationInfo
         */
        @Override
        public void onGroupInvitationReject(InvitationInfo invitationInfo)
        {
            // 通知环信服务器已经拒绝了群邀请
            Model.getInstance().getGlobalThreadPool().execute(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        EMClient.getInstance().groupManager().declineInvitation(invitationInfo.getGroupInfo().getGroupEMId(),
                                invitationInfo.getGroupInfo().getInvitePerson(), getResources().getString(R.string.reject_group_invitation));

                        invitationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_REJECT_INVITE);
                        Model.getInstance().getContactAndInvitationDBManager().getInvitationTableDao().addInvitation(invitationInfo);

                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                Toast.makeText(InvitationListActivity.this, getResources().getString(R.string.reject_group_invitation), Toast.LENGTH_SHORT).show();

                                refreshLvInvitationList();
                            }
                        });
                    }
                    catch (HyphenateException e)
                    {
                        e.printStackTrace();

                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                Toast.makeText(InvitationListActivity.this, getResources().getString(R.string.reject_group_invitation_failure), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                }
            });

        }

        /**
         * 接受群申请按钮的点击事件
         *
         * @param invitationInfo
         */
        @Override
        public void onGroupApplicationAccept(InvitationInfo invitationInfo)
        {
            // 通知环信服务器已经接受了群申请
            Model.getInstance().getGlobalThreadPool().execute(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        EMClient.getInstance().groupManager().acceptApplication(invitationInfo.getGroupInfo().getGroupEMId(), invitationInfo.getGroupInfo().getInvitePerson());

                        invitationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_ACCEPT_APPLICATION);
                        Model.getInstance().getContactAndInvitationDBManager().getInvitationTableDao().addInvitation(invitationInfo);

                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                Toast.makeText(InvitationListActivity.this, getResources().getString(R.string.accept_group_application), Toast.LENGTH_SHORT).show();

                                refreshLvInvitationList();
                            }
                        });
                    }
                    catch (HyphenateException e)
                    {
                        e.printStackTrace();

                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                Toast.makeText(InvitationListActivity.this, getResources().getString(R.string.accept_group_application_failure), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                }
            });

        }

        /**
         * 拒绝群申请按钮的点击事件
         *
         * @param invitationInfo
         */
        @Override
        public void onGroupApplicationReject(InvitationInfo invitationInfo)
        {
            // 通知环信服务器已经拒绝了群申请
            Model.getInstance().getGlobalThreadPool().execute(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        EMClient.getInstance().groupManager().declineApplication(invitationInfo.getGroupInfo().getGroupEMId(), invitationInfo.getGroupInfo().getInvitePerson(),
                                getResources().getString(R.string.reject_group_application_failure));

                        invitationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_REJECT_APPLICATION);
                        Model.getInstance().getContactAndInvitationDBManager().getInvitationTableDao().addInvitation(invitationInfo);

                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                Toast.makeText(InvitationListActivity.this, getResources().getString(R.string.reject_group_application), Toast.LENGTH_SHORT).show();

                                refreshLvInvitationList();
                            }
                        });
                    }
                    catch (HyphenateException e)
                    {
                        e.printStackTrace();

                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                Toast.makeText(InvitationListActivity.this, getResources().getString(R.string.reject_group_application_failure), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation_list);

        ButterKnife.bind(this);

        initTransparentStatusBar();

        initView();
    }


    /**
     * 初始化透明状态栏
     */
    private void initTransparentStatusBar()
    {
        // 实现透明状态栏效果
        if (Build.VERSION.SDK_INT >= 21)
        {
            View decorView = getWindow().getDecorView();

            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

            decorView.setSystemUiVisibility(option);

            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().hide();
        }
    }



    @OnClick({R.id.iv_back, R.id.tv_back})
    public void handleAllClick(View view)
    {
        switch (view.getId())
        {
            case R.id.iv_back:
                finish();
                break;

            case R.id.tv_back:
                finish();
                break;

            default:
                break;
        }
    }

    private void initView()
    {
        initLvInvitationList();

        mLBM = LocalBroadcastManager.getInstance(this);
        mLBM.registerReceiver(mInviteChangeReceiver, new IntentFilter(Constant.CONTACT_INVITE_CHANGED));
        mLBM.registerReceiver(mInviteChangeReceiver, new IntentFilter(Constant.GROUP_INVITE_CHANGED));
    }



    private void initLvInvitationList()
    {
        mInvitationListViewAdapter = new InvitationListViewAdapter(this, mOnInviteListener);
        mLvInvitationList.setAdapter(mInvitationListViewAdapter);

        refreshLvInvitationList();
    }


    /**
     * 刷新邀请信息列表
     */
    private void refreshLvInvitationList()
    {
        List<InvitationInfo> invitationInfoList = Model.getInstance().getContactAndInvitationDBManager().getInvitationTableDao().getInvitationList();

        mInvitationListViewAdapter.refresh(invitationInfoList);
    }


    public static void actionStart(Context context)
    {
        context.startActivity(new Intent(context, InvitationListActivity.class));
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mLBM.unregisterReceiver(mInviteChangeReceiver);
    }
}
