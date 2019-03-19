package com.wuchangi.freechat.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.wuchangi.freechat.R;
import com.wuchangi.freechat.bean.UserInfo;
import com.wuchangi.freechat.utils.Model;
import com.wuchangi.freechat.utils.TypefaceUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * APP启动页
 */
public class SplashActivity extends AppCompatActivity {

    @BindView(R.id.tv_splash_title)
    TextView mTvSplashTitle;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // 若当前Activity已经退出，则不处理handler中的消息
            if (isFinishing()) {
                return;
            }

            // 进行界面跳转
            toLoginOrMain();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ButterKnife.bind(this);

        initTransparentStatusBar();

        initTypefaceOfTitle();

        // 发送2s的延时消息
        mHandler.sendMessageDelayed(Message.obtain(), 2000);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 销毁消息
        mHandler.removeCallbacksAndMessages(null);
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
     * 初始化启动页的标题的字体
     */
    private void initTypefaceOfTitle() {
        mTvSplashTitle.setTypeface(TypefaceUtils.getInstance().getTypeface1());
    }


    /**
     * 判断进入登录页面还是主页面
     */
    private void toLoginOrMain() {
        Model.getInstance().getGlobalThreadPool().execute(() ->
        {
            // 用户之前登录过
            if (EMClient.getInstance().isLoggedInBefore()) {
                // 获取当前登录用户的信息
                UserInfo userInfo = Model.getInstance().getUserInfoTableDao().getUserInfoByEMId(EMClient.getInstance().getCurrentUser());

                // userInfo为null时，重新登录
                if (userInfo == null) {
                    LoginActivity.actionStart(SplashActivity.this);
                } else {
                    // 登录成功后的处理
                    Model.getInstance().loginSuccess(userInfo);

                    MainActivity.actionStart(SplashActivity.this);
                }
            }
            // 用户之前没有登录过
            else {
                LoginActivity.actionStart(SplashActivity.this);
            }

            finish();
        });
    }

}
