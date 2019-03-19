package com.wuchangi.freechat.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wuchangi.freechat.R;
import com.wuchangi.freechat.bean.UserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WuchangI on 2018/11/23.
 */

public class GroupDetailGridViewAdapter extends BaseAdapter
{
    private Context mContext;

    /**
     * 是否允许当前用户添加和删除群成员
     */
    private boolean mIsCanModify;

    /**
     * 删除模式
     */
    private boolean mIsDeleteMode;

    private List<UserInfo> mMembers = new ArrayList<>();

    private OnGroupDetailListener mOnGroupDetailListener;


    public GroupDetailGridViewAdapter(Context context, boolean isCanModify, OnGroupDetailListener onGroupDetailListener)
    {
        mContext = context;
        mIsCanModify = isCanModify;
        mOnGroupDetailListener = onGroupDetailListener;
    }

    public void refresh(List<UserInfo> members)
    {
        if (members != null && members.size() >= 0)
        {
            mMembers.clear();

            initPlusAndMinus();

            mMembers.addAll(0, members);
        }

        notifyDataSetChanged();
    }

    /**
     * 初始化加号和减号
     */
    private void initPlusAndMinus()
    {
        UserInfo plus = new UserInfo();
        UserInfo minus = new UserInfo();

        mMembers.add(minus);
        mMembers.add(0, plus);
    }

    @Override
    public int getCount()
    {
        return mMembers == null ? 0 : mMembers.size();
    }


    @Override
    public Object getItem(int position)
    {
        return mMembers.get(position);
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

            convertView = View.inflate(mContext, R.layout.item_group_detail, null);

            viewHolder.mIvPhotoDeleteMember = convertView.findViewById(R.id.iv_photo_delete_member);
            viewHolder.mIvDotDeleteMember = convertView.findViewById(R.id.iv_dot_delete_member);
            viewHolder.mTvMemberName = convertView.findViewById(R.id.tv_member_name);

            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        UserInfo member = mMembers.get(position);

        if (mIsCanModify)
        {
            // 减号的处理
            if (position == getCount() - 1)
            {
                if (mIsDeleteMode)
                {
                    convertView.setVisibility(View.GONE);
                }
                else
                {
                    convertView.setVisibility(View.VISIBLE);

                    viewHolder.mIvPhotoDeleteMember.setImageResource(R.drawable.minus_member);
                    viewHolder.mIvDotDeleteMember.setVisibility(View.GONE);
                    viewHolder.mTvMemberName.setVisibility(View.GONE);
                }
            }
            // 加号的处理
            else if (position == getCount() - 2)
            {
                if (mIsDeleteMode)
                {
                    convertView.setVisibility(View.GONE);
                }
                else
                {
                    convertView.setVisibility(View.VISIBLE);

                    viewHolder.mIvPhotoDeleteMember.setImageResource(R.drawable.plus_member);
                    viewHolder.mIvDotDeleteMember.setVisibility(View.GONE);
                    viewHolder.mTvMemberName.setVisibility(View.GONE);
                }
            }
            // 群成员的处理
            else
            {
                convertView.setVisibility(View.VISIBLE);

                viewHolder.mTvMemberName.setVisibility(View.VISIBLE);
                viewHolder.mTvMemberName.setText(member.getUserName());
                viewHolder.mIvPhotoDeleteMember.setImageResource(R.drawable.default_avatar);

                if (mIsDeleteMode)
                {
                    viewHolder.mIvDotDeleteMember.setVisibility(View.VISIBLE);
                }
                else
                {
                    viewHolder.mIvDotDeleteMember.setVisibility(View.GONE);

                }
            }


            // 减号的处理
            if (position == getCount() - 1)
            {
                viewHolder.mIvPhotoDeleteMember.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if(!mIsDeleteMode)
                        {
                            // 切换至删除模式
                            mIsDeleteMode = true;

                            notifyDataSetChanged();
                        }
                    }
                });
            }
            // 加号的处理
            else if (position == getCount() - 2)
            {
                viewHolder.mIvPhotoDeleteMember.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        mOnGroupDetailListener.onAddMembers();
                    }
                });
            }
            // 群成员的处理
            else
            {
                viewHolder.mIvDotDeleteMember.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        mOnGroupDetailListener.onDeleteMember(member);
                    }
                });
            }

        }
        else
        {
            if (position == getCount() - 1 || position == getCount() - 2)
            {
                convertView.setVisibility(View.GONE);
            }
            else
            {
                convertView.setVisibility(View.VISIBLE);

                viewHolder.mTvMemberName.setText(member.getUserName());

                viewHolder.mIvPhotoDeleteMember.setImageResource(R.drawable.default_avatar);

                viewHolder.mIvDotDeleteMember.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

    public boolean isIsDeleteMode()
    {
        return mIsDeleteMode;
    }

    public void setIsDeleteMode(boolean mIsDeleteMode)
    {
        this.mIsDeleteMode = mIsDeleteMode;
    }

    private class ViewHolder
    {
        private ImageView mIvPhotoDeleteMember;
        private ImageView mIvDotDeleteMember;
        private TextView mTvMemberName;
    }


    public interface OnGroupDetailListener
    {
        /**
         * 添加群成员
         */
        void onAddMembers();

        /**
         * 删除群成员
         */
        void onDeleteMember(UserInfo member);
    }

}
