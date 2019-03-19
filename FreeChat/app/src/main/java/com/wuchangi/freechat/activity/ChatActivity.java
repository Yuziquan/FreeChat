package com.wuchangi.freechat.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hyphenate.easeui.widget.chatrow.EaseCustomChatRowProvider;
import com.wuchangi.freechat.R;
import com.wuchangi.freechat.constant.Constant;

public class ChatActivity extends FragmentActivity {

    /**
     * 这里的mUserEMId可能为用户的环信ID 或 群聊的环信ID
     */
    private String mUserEMId;
    private EaseChatFragment mEaseChatFragment;
    private LocalBroadcastManager mLBM;
    private int mChatType;

    private BroadcastReceiver mLeaveOrDestroyGroupReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mUserEMId.equals(intent.getStringExtra(Constant.GROUP_EM_ID))) {
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initView();

        initListener();
    }


    private void initView() {
        mUserEMId = getIntent().getStringExtra(EaseConstant.EXTRA_USER_ID);
        mChatType = getIntent().getExtras().getInt(EaseConstant.EXTRA_CHAT_TYPE);

        mEaseChatFragment = new EaseChatFragment();
        mEaseChatFragment.setArguments(getIntent().getExtras());

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fl_chat, mEaseChatFragment).commit();

        mLBM = LocalBroadcastManager.getInstance(this);
    }


    private void initListener() {
        mEaseChatFragment.setChatFragmentHelper(new EaseChatFragment.EaseChatFragmentHelper() {
            @Override
            public void onSetMessageAttributes(EMMessage message) {

            }

            // 用户点击群详情按钮
            @Override
            public void onEnterToChatDetails() {
                GroupDetailActivity.actionStart(ChatActivity.this, mUserEMId);
            }

            @Override
            public void onAvatarClick(String username) {

            }

            @Override
            public void onAvatarLongClick(String username) {

            }

            @Override
            public boolean onMessageBubbleClick(EMMessage message) {
                return false;
            }

            @Override
            public void onMessageBubbleLongClick(EMMessage message) {

            }

            @Override
            public boolean onExtendMenuItemClick(int itemId, View view) {
                return false;
            }

            @Override
            public EaseCustomChatRowProvider onSetCustomChatRowProvider() {
                return null;
            }
        });


        // 当前为群聊类型
        if (mChatType == EaseConstant.CHATTYPE_GROUP) {
            mLBM.registerReceiver(mLeaveOrDestroyGroupReceiver, new IntentFilter(Constant.LEAVE_GROUP));
        }

    }

    /**
     * 得到userEMId（conversation.conversationId()）后跳转到单聊界面或群聊界面
     *
     * @param context
     * @param userEMId
     * @param chatType 单聊类型或群聊类型
     */
    public static void actionStart(Context context, String userEMId, int chatType) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(EaseConstant.EXTRA_USER_ID, userEMId);
        intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, chatType);

        context.startActivity(intent);
    }


    /**
     * 默认情况下，得到userEMId（conversation.conversationId()）后跳转到单聊界面
     *
     * @param context
     * @param userEMId
     */
    public static void actionStart(Context context, String userEMId) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(EaseConstant.EXTRA_USER_ID, userEMId);

        context.startActivity(intent);
    }

}

