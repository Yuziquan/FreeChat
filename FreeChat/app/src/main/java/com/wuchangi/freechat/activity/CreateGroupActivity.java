package com.wuchangi.freechat.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.chat.EMGroupOptions;
import com.hyphenate.exceptions.HyphenateException;
import com.wuchangi.freechat.R;
import com.wuchangi.freechat.utils.Model;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateGroupActivity extends AppCompatActivity
{
    @BindView(R.id.et_group_name)
    EditText mEtGroupName;

    @BindView(R.id.et_group_intro)
    EditText mEtGroupIntro;

    @BindView(R.id.cb_is_public)
    CheckBox mCbIsPublic;

    @BindView(R.id.cb_is_open_group_invitation)
    CheckBox mCbIsOpenGroupInvitation;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        ButterKnife.bind(this);

        initTransparentStatusBar();
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


    @OnClick({R.id.iv_back, R.id.tv_back, R.id.btn_create_group})
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

            case R.id.btn_create_group:
                Intent intent = new Intent(CreateGroupActivity.this, PickContactActivity.class);
                startActivityForResult(intent, 1);
                break;

            default:
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // 成功获取到联系人
        if (resultCode == RESULT_OK)
        {
            createGroup(data.getStringArrayExtra("members"));
        }
    }


    private void createGroup(String[] members)
    {
        String groupName = mEtGroupName.getText().toString();
        String groupIntro = mEtGroupIntro.getText().toString();

        Model.getInstance().getGlobalThreadPool().execute(new Runnable()
        {
            @Override
            public void run()
            {
                /** 去环信服务器创建群
                 *  createGroup(): 参数一：群名称；参数二：群描述；参数三：群成员；参数四：原因；参数五：参数设置
                 */
                EMGroupOptions options = new EMGroupOptions();

                options.maxUsers = 200;
                EMGroupManager.EMGroupStyle groupStyle = null;

                if(mCbIsPublic.isChecked())
                {
                    if(mCbIsOpenGroupInvitation.isChecked())
                    {
                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePublicOpenJoin;
                    }
                    else
                    {
                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePublicJoinNeedApproval;
                    }
                }
                else
                {
                    if(mCbIsOpenGroupInvitation.isChecked())
                    {
                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePrivateMemberCanInvite;
                    }
                    else
                    {
                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePrivateOnlyOwnerInvite;
                    }
                }

                options.style = groupStyle;

                try
                {
                    EMClient.getInstance().groupManager().createGroup(groupName, groupIntro, members,
                            getResources().getString(R.string.group_application), options);

                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Toast.makeText(CreateGroupActivity.this,
                                    getResources().getString(R.string.create_group_success), Toast.LENGTH_SHORT).show();

                            finish();
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
                            Toast.makeText(CreateGroupActivity.this,
                                    getResources().getString(R.string.create_group_failure), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }


    public static void actionStart(Context context)
    {
        context.startActivity(new Intent(context, CreateGroupActivity.class));
    }

}
