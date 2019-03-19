package com.wuchangi.freechat.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.exceptions.HyphenateException;
import com.wuchangi.freechat.R;
import com.wuchangi.freechat.adapter.GroupListAdapter;
import com.wuchangi.freechat.utils.Model;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GroupListActivity extends AppCompatActivity
{
    @BindView(R.id.lv_group_list)
    ListView mLvGroupList;

    private LinearLayout mLlCreateGroup;

    private GroupListAdapter mGroupListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);

        ButterKnife.bind(this);

        initTransparentStatusBar();

        initView();

        initListener();
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


    private void initView()
    {
        View headerView = View.inflate(this, R.layout.header_group_list, null);
        mLvGroupList.addHeaderView(headerView);

        mLlCreateGroup = (LinearLayout) headerView.findViewById(R.id.ll_create_group);

        mGroupListAdapter = new GroupListAdapter(this);
        mLvGroupList.setAdapter(mGroupListAdapter);

        getGroupListFromEM();

    }


    /**
     * 去环信服务器获取所有群组信息
     */
    private void getGroupListFromEM()
    {
        Model.getInstance().getGlobalThreadPool().execute(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    List<EMGroup> EMGroupList = EMClient.getInstance().groupManager().getJoinedGroupsFromServer();

                    if (EMGroupList != null && EMGroupList.size() >= 0)
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                Toast.makeText(GroupListActivity.this, getResources().getString(R.string.load_group_list_success), Toast.LENGTH_SHORT).show();

                                //或 mGroupListAdapter.refresh(EMGroupList);

                                refresh();
                            }
                        });
                    }
                }
                catch (HyphenateException e)
                {
                    e.printStackTrace();

                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Toast.makeText(GroupListActivity.this, getResources().getString(R.string.load_group_list_failure), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });
    }


    private void initListener()
    {
        mLlCreateGroup.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                CreateGroupActivity.actionStart(GroupListActivity.this);
            }
        });

        mLvGroupList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if (position == 0)
                {
                    return;
                }

                EMGroup emGroup = EMClient.getInstance().groupManager().getAllGroups().get(position - 1);

                ChatActivity.actionStart(GroupListActivity.this, emGroup.getGroupId(), EaseConstant.CHATTYPE_GROUP);
            }
        });
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


    @Override
    protected void onResume()
    {
        super.onResume();

        refresh();
    }

    private void refresh()
    {
        mGroupListAdapter.refresh(EMClient.getInstance().groupManager().getAllGroups());
    }


    public static void actionStart(Context context)
    {
        Intent intent = new Intent(context, GroupListActivity.class);

        context.startActivity(intent);
    }

}
