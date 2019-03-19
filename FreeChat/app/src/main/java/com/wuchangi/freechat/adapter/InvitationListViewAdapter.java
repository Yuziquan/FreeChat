package com.wuchangi.freechat.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.wuchangi.freechat.R;
import com.wuchangi.freechat.bean.GroupInfo;
import com.wuchangi.freechat.bean.InvitationInfo;
import com.wuchangi.freechat.bean.UserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WuchangI on 2018/11/21.
 */

public class InvitationListViewAdapter extends BaseAdapter
{
    private Context mContext;

    private OnInviteListener mOnInviteListener;

    private List<InvitationInfo> mInvitationInfoList = new ArrayList<>();

    public InvitationListViewAdapter(Context context, OnInviteListener onInviteListener)
    {
        mContext = context;
        mOnInviteListener = onInviteListener;
    }

    @Override
    public int getCount()
    {
        return mInvitationInfoList == null ? 0 : mInvitationInfoList.size();
    }


    @Override
    public Object getItem(int position)
    {
        return mInvitationInfoList.get(position);
    }


    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder = null;

        if (convertView == null)
        {
            viewHolder = new ViewHolder();

            convertView = View.inflate(mContext, R.layout.item_invite, null);

            viewHolder.mTvName = convertView.findViewById(R.id.tv_name);
            viewHolder.mTvReason = convertView.findViewById(R.id.tv_reason);

            viewHolder.mBtnAccept = convertView.findViewById(R.id.btn_accept);
            viewHolder.mBtnReject = convertView.findViewById(R.id.btn_reject);

            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        InvitationInfo invitationInfo = mInvitationInfoList.get(position);

        UserInfo userInfo = invitationInfo.getUserInfo();
        GroupInfo groupInfo = invitationInfo.getGroupInfo();

        // 为联系人的邀请信息
        if (userInfo != null)
        {
            viewHolder.mTvName.setText(userInfo.getUserName());

            viewHolder.mBtnAccept.setVisibility(View.INVISIBLE);
            viewHolder.mBtnReject.setVisibility(View.INVISIBLE);

            if (invitationInfo.getStatus() == InvitationInfo.InvitationStatus.NEW_INVITE)
            {
                if (invitationInfo.getReason() == null)
                {
                    viewHolder.mTvReason.setText(mContext.getResources().getString(R.string.add_a_friend));
                }
                else
                {
                    viewHolder.mTvReason.setText(invitationInfo.getReason());
                }

                viewHolder.mBtnAccept.setVisibility(View.VISIBLE);
                viewHolder.mBtnReject.setVisibility(View.VISIBLE);
            }
            else if (invitationInfo.getStatus() == InvitationInfo.InvitationStatus.INVITE_ACCEPT)
            {
                if (invitationInfo.getReason() == null)
                {
                    viewHolder.mTvReason.setText(mContext.getResources().getString(R.string.accept_invitation));
                }
                else
                {
                    viewHolder.mTvReason.setText(invitationInfo.getReason());
                }
            }
            else if (invitationInfo.getStatus() == InvitationInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER)
            {
                if (invitationInfo.getReason() == null)
                {
                    viewHolder.mTvReason.setText(mContext.getResources().getString(R.string.invitation_accepted));
                }
                else
                {
                    viewHolder.mTvReason.setText(invitationInfo.getReason());
                }
            }


            viewHolder.mBtnAccept.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mOnInviteListener.onAccept(invitationInfo);
                }
            });


            viewHolder.mBtnReject.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mOnInviteListener.onReject(invitationInfo);
                }
            });

        }
        // 为群组的邀请信息
        else
        {
            viewHolder.mTvName.setText(groupInfo.getInvitePerson());

            viewHolder.mBtnAccept.setVisibility(View.INVISIBLE);
            viewHolder.mBtnReject.setVisibility(View.INVISIBLE);

            switch (invitationInfo.getStatus())
            {
                // 您的群申请已经被接受
                case GROUP_APPLICATION_ACCEPTED:
                    viewHolder.mTvReason.setText(mContext.getResources().getString(R.string.group_application_accepted));

                    break;

                // 您的群邀请已经被接收
                case GROUP_INVITE_ACCEPTED:
                    viewHolder.mTvReason.setText(mContext.getResources().getString(R.string.group_invite_accepted));

                    break;

                // 你的群申请已经被拒绝
                case GROUP_APPLICATION_DECLINED:
                    viewHolder.mTvReason.setText(mContext.getResources().getString(R.string.group_application_declined));

                    break;

                // 您的群邀请已经被拒绝
                case GROUP_INVITE_DECLINED:
                    viewHolder.mTvReason.setText(mContext.getResources().getString(R.string.group_invite_declined));

                    break;

                // 您收到了群邀请
                case NEW_GROUP_INVITE:
                    viewHolder.mTvReason.setText(mContext.getResources().getString(R.string.new_group_invite));

                    viewHolder.mBtnAccept.setVisibility(View.VISIBLE);
                    viewHolder.mBtnReject.setVisibility(View.VISIBLE);

                    viewHolder.mBtnAccept.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            mOnInviteListener.onGroupInvitationAccept(invitationInfo);
                        }
                    });


                    viewHolder.mBtnReject.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            mOnInviteListener.onGroupInvitationReject(invitationInfo);
                        }
                    });

                    break;

                // 您收到了群申请
                case NEW_GROUP_APPLICATION:
                    viewHolder.mTvReason.setText(mContext.getResources().getString(R.string.new_group_application));

                    viewHolder.mBtnAccept.setVisibility(View.VISIBLE);
                    viewHolder.mBtnReject.setVisibility(View.VISIBLE);

                    viewHolder.mBtnAccept.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            mOnInviteListener.onGroupApplicationAccept(invitationInfo);
                        }
                    });


                    viewHolder.mBtnReject.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            mOnInviteListener.onGroupApplicationReject(invitationInfo);
                        }
                    });

                    break;

                // 你接受了群邀请
                case GROUP_ACCEPT_INVITE:
                    viewHolder.mTvReason.setText(mContext.getResources().getString(R.string.group_accept_invite));

                    break;

                // 您批准了群加入
                case GROUP_ACCEPT_APPLICATION:
                    viewHolder.mTvReason.setText(mContext.getResources().getString(R.string.group_accept_application));

                    break;
            }


        }


        return convertView;
    }


    /**
     * 刷新页面数据
     */
    public void refresh(List<InvitationInfo> invitationInfoList)
    {
        if (invitationInfoList != null && invitationInfoList.size() >= 0)
        {
            mInvitationInfoList.clear();

            mInvitationInfoList.addAll(invitationInfoList);

            notifyDataSetChanged();
        }
    }


    private class ViewHolder
    {
        private TextView mTvName;
        private TextView mTvReason;

        private Button mBtnAccept;
        private Button mBtnReject;
    }


    public interface OnInviteListener
    {
        /**
         * 接受邀请按钮的点击事件
         */
        void onAccept(InvitationInfo invitationInfo);

        /**
         * 拒绝邀请按钮的点击事件
         */
        void onReject(InvitationInfo invitationInfo);


        /**
         * 接受群邀请按钮的点击事件
         */
        void onGroupInvitationAccept(InvitationInfo invitationInfo);

        /**
         * 拒绝群邀请按钮的点击事件
         */
        void onGroupInvitationReject(InvitationInfo invitationInfo);


        /**
         * 接受群申请按钮的点击事件
         */
        void onGroupApplicationAccept(InvitationInfo invitationInfo);

        /**
         * 拒绝群申请按钮的点击事件
         */
        void onGroupApplicationReject(InvitationInfo invitationInfo);

    }


}
