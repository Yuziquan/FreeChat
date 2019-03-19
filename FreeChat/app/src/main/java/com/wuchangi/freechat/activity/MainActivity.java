package com.wuchangi.freechat.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.wuchangi.freechat.R;
import com.wuchangi.freechat.adapter.MyFragmentPagerAdapter;
import com.wuchangi.freechat.fragment.ContactListFragment;
import com.wuchangi.freechat.fragment.ConversationListFragment;
import com.wuchangi.freechat.utils.Model;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    @BindView(R.id.bottom_navigation_bar)
    BottomNavigationBar mBottomNavigationBar;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.navigation_view)
    NavigationView mNavigationView;

    private List<Fragment> mFragmentList;

    /**
     * 当前选中的fragment
     */
    private int mCurFragmentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Explode explode = new Explode();
        explode.setDuration(500);
        getWindow().setExitTransition(explode);
        getWindow().setEnterTransition(explode);

        ButterKnife.bind(this);

        initView();
    }


    private void initView() {
        initTransparentStatusBar();
        initNavigationView();
        initBottomNavigationBar();
        initViewpager();
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


    @SuppressLint("ResourceType")
    private void initNavigationView() {
        View headerView = mNavigationView.getHeaderView(0);
        TextView tvUserName = headerView.findViewById(R.id.tv_name);
        tvUserName.setText(EMClient.getInstance().getCurrentUser());

        Resources resources = getBaseContext().getResources();

        // 设置侧滑菜单的菜单项图标icon颜色
        mNavigationView.setItemIconTintList(resources.getColorStateList(R.drawable.nav_menu_item_color));

        // 设置侧滑菜单的菜单项文字颜色
        mNavigationView.setItemTextColor(resources.getColorStateList(R.drawable.nav_menu_item_color));

        // 设置侧滑菜单默认选中项
        // mNavigationView.setCheckedItem(R.id.nav_settings);

        mNavigationView.setNavigationItemSelectedListener(item ->
        {
            switch (item.getItemId()) {
                case R.id.nav_logout:
                    logout();
                    mDrawerLayout.closeDrawers();
                    break;

                case R.id.nav_settings:
                    SettingsActivity.actionStart(this);
                    mDrawerLayout.closeDrawers();
                    break;

                case R.id.nav_about:
                    mDrawerLayout.closeDrawers();
                    break;

                default:
                    break;
            }

            return true;
        });
    }


    /**
     * 执行登出操作
     */
    private void logout() {
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                EMClient.getInstance().logout(false, new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        // 关闭数据库
                        Model.getInstance().getContactAndInvitationDBManager().close();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, getResources().getString(R.string.logout_success), Toast.LENGTH_SHORT).show();

                                LoginActivity.actionStart(MainActivity.this);

                                finish();
                            }
                        });
                    }

                    @Override
                    public void onError(int i, String s) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, getResources().getString(R.string.login_failure) + s, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onProgress(int i, String s) {

                    }
                });
            }
        });
    }


    private void initBottomNavigationBar() {
        mBottomNavigationBar.clearAll();

        mBottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        mBottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        mBottomNavigationBar.setActiveColor(R.color.bnb_active_color);

        mBottomNavigationBar.addItem(new BottomNavigationItem(R.drawable.message, getResources().getString(R.string.message)))
                .addItem(new BottomNavigationItem(R.drawable.contacts, getResources().getString(R.string.contacts))).setFirstSelectedPosition(0).initialise();

        mBottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                mViewPager.setCurrentItem(position);
                mCurFragmentIndex = position;
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {

            }
        });

    }


    private void initViewpager() {
        mFragmentList = new ArrayList<>();
        mFragmentList.add(new ConversationListFragment());
        mFragmentList.add(new ContactListFragment());

        mViewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), mFragmentList));

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mBottomNavigationBar.selectTab(position);
                mCurFragmentIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mViewPager.setCurrentItem(0);
    }


    public static void actionStart(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    public static void actionStart(Context context, Bundle bundle) {
        context.startActivity(new Intent(context, MainActivity.class), bundle);
    }
}
