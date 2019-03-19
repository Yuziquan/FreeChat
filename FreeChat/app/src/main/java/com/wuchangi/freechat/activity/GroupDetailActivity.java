package com.wuchangi.freechat.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;
import com.wuchangi.freechat.R;
import com.wuchangi.freechat.adapter.GroupDetailGridViewAdapter;
import com.wuchangi.freechat.bean.UserInfo;
import com.wuchangi.freechat.constant.Constant;
import com.wuchangi.freechat.utils.Model;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GroupDetailActivity extends AppCompatActivity {
    @BindView(R.id.gv_group_detail)
    GridView mGvGroupDetail;

    @BindView(R.id.btn_leave_group)
    Button mBtnLeaveGroup;

    private EMGroup mGroup;

    private List<UserInfo> mUsers;

    private GroupDetailGridViewAdapter mGroupDetailGridViewAdapter;


    private GroupDetailGridViewAdapter.OnGroupDetailListener mOnGroupDetailListener = new GroupDetailGridViewAdapter.OnGroupDetailListener() {
        @Override
        public void onAddMembers() {
            Intent intent = new Intent(GroupDetailActivity.this, PickContactActivity.class);

            intent.putExtra(Constant.GROUP_EM_ID, mGroup.getGroupId());

            startActivityForResult(intent, 2);
        }

        @Override
        public void onDeleteMember(UserInfo member) {
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().groupManager().removeUserFromGroup(mGroup.getGroupId(), member.getUserName());

                        getMembersFromEM();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, getResources().getString(R.string.delete_member_success), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (HyphenateException e) {
                        e.printStackTrace();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, getResources().getString(R.string.delete_member_failure) + e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        ButterKnife.bind(this);

        initTransparentStatusBar();

        initData();

        initView();

    }


    /**
     * 初始化透明状态栏
     */
    private void initTransparentStatusBar() {
        // 实现透明状态栏效果
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();

            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

            decorView.setSystemUiVisibility(option);

            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }


    private void initView() {
        initButtonDisplay();

        initGridView();

        getMembersFromEM();


        initListener();
    }


    /**
     * 从环信服务器获取所有群成员
     */
    private void getMembersFromEM() {
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    EMGroup group = EMClient.getInstance().groupManager().getGroupFromServer(mGroup.getGroupId());

                    List<String> members = group.getMembers();

                    if (members != null && members.size() >= 0) {
                        mUsers = new ArrayList<>();

                        for (String member : members) {
                            UserInfo userInfo = new UserInfo(member);

                            mUsers.add(userInfo);
                        }
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mGroupDetailGridViewAdapter.refresh(mUsers);
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(GroupDetailActivity.this, getResources().getString(R.string.get_group_detail_failure) + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void initListener() {
        mGvGroupDetail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        if (mGroupDetailGridViewAdapter.isIsDeleteMode()) {
                            // 切换回非删除模式
                            mGroupDetailGridViewAdapter.setIsDeleteMode(false);

                            mGroupDetailGridViewAdapter.notifyDataSetChanged();
                        }

                        break;

                    default:
                        break;
                }

                return false;
            }
        });
    }


    private void initButtonDisplay() {
        // 当前用户为群主
        if (EMClient.getInstance().getCurrentUser().equals(mGroup.getOwner())) {
            mBtnLeaveGroup.setText(getResources().getString(R.string.destroy_group));

            mBtnLeaveGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                EMClient.getInstance().groupManager().destroyGroup(mGroup.getGroupId());

                                sendLeaveOrDestroyGroupBroadcast();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailActivity.this, getResources().getString(R.string.destroy_group_success), Toast.LENGTH_SHORT).show();

                                        finish();
                                    }
                                });

                            } catch (HyphenateException e) {
                                e.printStackTrace();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailActivity.this, getResources().
                                                getString(R.string.destroy_group_failure) + e.toString(), Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                        }
                    });
                }
            });
        }
        // 当前用户为群成员
        else {
            mBtnLeaveGroup.setText(getResources().getString(R.string.leave_group));

            mBtnLeaveGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                EMClient.getInstance().groupManager().leaveGroup(mGroup.getGroupId());

                                sendLeaveOrDestroyGroupBroadcast();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailActivity.this, getResources().getString(R.string.leave_group_success), Toast.LENGTH_SHORT).show();

                                        finish();
                                    }
                                });

                            } catch (HyphenateException e) {
                                e.printStackTrace();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailActivity.this, getResources().getString(R.string.leave_group_failure) + e.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }
            });

        }
    }


    private void initGridView() {
        // 当前用户是群主或者群为公开的
        boolean isCanModify = EMClient.getInstance().getCurrentUser().equals(mGroup.getOwner()) || mGroup.isPublic();

        mGroupDetailGridViewAdapter = new GroupDetailGridViewAdapter(this, isCanModify, mOnGroupDetailListener);

        mGvGroupDetail.setAdapter(mGroupDetailGridViewAdapter);

    }

    private void initData() {
        String groupEMId = getIntent().getStringExtra(Constant.GROUP_EM_ID);

        if (groupEMId == null) {
            return;
        } else {
            mGroup = EMClient.getInstance().groupManager().getGroup(groupEMId);
        }
    }


    private void sendLeaveOrDestroyGroupBroadcast() {
        LocalBroadcastManager mLBM = LocalBroadcastManager.getInstance(this);

        Intent intent = new Intent(Constant.LEAVE_GROUP);
        intent.putExtra(Constant.GROUP_EM_ID, mGroup.getGroupId());

        mLBM.sendBroadcast(intent);
    }


    @OnClick({R.id.iv_back, R.id.tv_back})
    public void handleAllClick(View view) {
        switch (view.getId()) {
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            String[] members = data.getStringArrayExtra("members");

            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 发送群邀请信息
                        EMClient.getInstance().groupManager().addUsersToGroup(mGroup.getGroupId(), members);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, getResources().getString(R.string.send_invitation_success), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (HyphenateException e) {
                        e.printStackTrace();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, getResources().getString(R.string.send_invitation_failure) + e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }

    }

    public static void actionStart(Context context, String groupEMId) {
        Intent intent = new Intent(context, GroupDetailActivity.class);
        intent.putExtra(Constant.GROUP_EM_ID, groupEMId);

        context.startActivity(intent);
    }

}
