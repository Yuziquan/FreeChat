package com.wuchangi.freechat.activity;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.transition.Explode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.wuchangi.freechat.R;
import com.wuchangi.freechat.bean.UserInfo;
import com.wuchangi.freechat.utils.Model;
import com.wuchangi.freechat.utils.TypefaceUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.tv_login_title)
    TextView mTvLoginTitle;

    @BindView(R.id.et_user_name)
    EditText mEtUserName;

    @BindView(R.id.et_password)
    EditText mEtPassword;

    @BindView(R.id.btn_login)
    Button mBtnLogin;

    @BindView(R.id.fab_plus)
    FloatingActionButton mFabPlus;

    @BindView(R.id.cv_login)
    CardView mCvLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        initTransparentStatusBar();

        initTypefaceOfTitle();
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        mFabPlus.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFabPlus.setVisibility(View.VISIBLE);
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


    /**
     * 初始化登录界面的标题的字体
     */
    private void initTypefaceOfTitle() {
        mTvLoginTitle.setTypeface(TypefaceUtils.getInstance().getTypeface1());
    }


    @OnClick({R.id.btn_login, R.id.fab_plus})
    public void handleAllClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                login();
                break;

            case R.id.fab_plus:

                getWindow().setExitTransition(null);
                getWindow().setEnterTransition(null);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, mFabPlus, mFabPlus.getTransitionName());
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class), options.toBundle());

                break;

            default:
                break;
        }
    }



    /**
     * 执行登录操作
     */
    private void login() {
        Explode explode = new Explode();
        explode.setDuration(500);

        getWindow().setExitTransition(explode);
        getWindow().setEnterTransition(explode);
        ActivityOptionsCompat oc2 = ActivityOptionsCompat.makeSceneTransitionAnimation(LoginActivity.this);


        String userName = mEtUserName.getText().toString();
        String password = mEtPassword.getText().toString();

        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, getResources().getString(R.string.not_empty_user_name_or_password), Toast.LENGTH_SHORT).show();
            return;
        }

        // 去环信服务器登录
        Model.getInstance().getGlobalThreadPool().execute(() ->
        {
            // 这里使用用户名userName充当用户的环信ID
            EMClient.getInstance().login(userName, password, new EMCallBack() {
                @Override
                public void onSuccess() {
                    // 登录成功后的处理
                    Model.getInstance().loginSuccess(new UserInfo(userName));

                    // 保存用户个人信息到本地数据库
                    Model.getInstance().getUserInfoTableDao().addUserInfo(new UserInfo(userName, "", "", userName));

                    runOnUiThread(() ->
                    {
                        Toast.makeText(LoginActivity.this, getResources().getString(R.string.login_success), Toast.LENGTH_SHORT).show();

                        MainActivity.actionStart(LoginActivity.this, oc2.toBundle());

                        finish();
                    });

                }

                @Override
                public void onError(int i, String s) {
                    runOnUiThread(() -> Toast.makeText(LoginActivity.this, getResources().getString(R.string.login_failure) + s, Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onProgress(int i, String s) {

                }
            });
        });

    }


    public static void actionStart(Context context) {
        context.startActivity(new Intent(context, LoginActivity.class));
    }
}
