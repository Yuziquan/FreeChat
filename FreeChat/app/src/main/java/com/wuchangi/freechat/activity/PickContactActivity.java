package com.wuchangi.freechat.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.wuchangi.freechat.R;
import com.wuchangi.freechat.adapter.PickContactListViewAdapter;
import com.wuchangi.freechat.bean.PickContactInfo;
import com.wuchangi.freechat.bean.UserInfo;
import com.wuchangi.freechat.constant.Constant;
import com.wuchangi.freechat.utils.Model;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PickContactActivity extends AppCompatActivity
{
    @BindView(R.id.lv_contact_list)
    ListView mLvContactList;

    private List<PickContactInfo> mPickContactInfoList;

    private PickContactListViewAdapter mPickContactListViewAdapter;

    private List<String> mMembers;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_contact);

        ButterKnife.bind(this);

        initTransparentStatusBar();

        getData();

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

    private void getData()
    {
        String groupEMId = getIntent().getStringExtra(Constant.GROUP_EM_ID);

        if (groupEMId != null)
        {
            EMGroup group = EMClient.getInstance().groupManager().getGroup(groupEMId);

            mMembers = group.getMembers();
        }

        if(mMembers == null)
        {
            mMembers = new ArrayList<>();
        }
    }


    private void initView()
    {
        List<UserInfo> contactInfoList = Model.getInstance().getContactAndInvitationDBManager().getContactTableDao().getContactList();

        mPickContactInfoList = new ArrayList<>();

        if (contactInfoList != null && contactInfoList.size() >= 0)
        {
            for (UserInfo contactInfo : contactInfoList)
            {
                PickContactInfo pickContactInfo = new PickContactInfo(contactInfo, false);
                mPickContactInfoList.add(pickContactInfo);
            }
        }

        mPickContactListViewAdapter = new PickContactListViewAdapter(this, mPickContactInfoList, mMembers);

        mLvContactList.setAdapter(mPickContactListViewAdapter);
    }


    private void initListener()
    {
        mLvContactList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                CheckBox cbPick = view.findViewById(R.id.cb_pick);
                cbPick.setChecked(!cbPick.isChecked());

                PickContactInfo pickContactInfo = mPickContactInfoList.get(position);
                pickContactInfo.setChecked(cbPick.isChecked());

                mPickContactListViewAdapter.notifyDataSetChanged();
            }
        });
    }


    @OnClick({R.id.iv_back, R.id.tv_back, R.id.tv_pick_save})
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

            case R.id.tv_pick_save:
                savePickContactList();
                break;

            default:
                break;
        }
    }

    private void savePickContactList()
    {
        List<String> pickContactList = mPickContactListViewAdapter.getPickContactList();

        Intent intent = new Intent();
        intent.putExtra("members", pickContactList.toArray(new String[0]));

        setResult(RESULT_OK, intent);

        finish();
    }

    public static void actionStart(Context context)
    {
        context.startActivity(new Intent(context, PickContactActivity.class));
    }


}
