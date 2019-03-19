package com.wuchangi.freechat.fragment;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.EaseUI;
import com.hyphenate.easeui.ui.EaseConversationListFragment;
import com.wuchangi.freechat.activity.ChatActivity;

import java.util.List;

/**
 * Created by WuchangI on 2018/11/20.
 */

/**
 * 消息列表界面
 */
public class ConversationListFragment extends EaseConversationListFragment
{

    @Override
    protected void initView()
    {
        super.initView();

        setConversationListItemClickListener(conversation ->
        {
            // 为群聊类型
            if (conversation.getType() == EMConversation.EMConversationType.GroupChat)
            {
                ChatActivity.actionStart(getActivity(), conversation.conversationId(), EaseConstant.CHATTYPE_GROUP);
            }
            // 为单聊（为默认值，不用设置类型值）
            else
            {
                ChatActivity.actionStart(getActivity(), conversation.conversationId());
            }
        });

        conversationList.clear();

        EMClient.getInstance().chatManager().addMessageListener(mEMMessageListener);
    }

    @Override
    protected void setUpView()
    {
        super.setUpView();
    }


    private EMMessageListener mEMMessageListener = new EMMessageListener()
    {
        @Override
        public void onMessageReceived(List<EMMessage> list)
        {
            EaseUI.getInstance().getNotifier().notify(list);

            refresh();
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> list)
        {

        }

        @Override
        public void onMessageRead(List<EMMessage> list)
        {

        }

        @Override
        public void onMessageDelivered(List<EMMessage> list)
        {

        }

        @Override
        public void onMessageRecalled(List<EMMessage> list)
        {

        }

        @Override
        public void onMessageChanged(EMMessage emMessage, Object o)
        {

        }
    };
}
