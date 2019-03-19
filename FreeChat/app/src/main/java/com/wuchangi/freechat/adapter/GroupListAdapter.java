package com.wuchangi.freechat.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hyphenate.chat.EMGroup;
import com.wuchangi.freechat.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WuchangI on 2018/11/22.
 */

public class GroupListAdapter extends BaseAdapter
{
    private Context mContext;

    private List<EMGroup> mEMGroupList = new ArrayList<>();

    public GroupListAdapter(Context context)
    {
        mContext = context;
    }


    @Override
    public int getCount()
    {
        return mEMGroupList == null ? 0 : mEMGroupList.size();
    }


    @Override
    public Object getItem(int position)
    {
        return mEMGroupList.get(position);
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

            convertView = View.inflate(mContext, R.layout.item_group, null);

            viewHolder.mTvGroupName = convertView.findViewById(R.id.tv_group_name);

            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        EMGroup emGroup = mEMGroupList.get(position);

        viewHolder.mTvGroupName.setText(emGroup.getGroupName());

        return convertView;
    }


    /**
     * 刷新页面数据
     */
    public void refresh(List<EMGroup> EMGroupList)
    {
        if(EMGroupList != null && EMGroupList.size() >= 0)
        {
            mEMGroupList.clear();

            mEMGroupList.addAll(EMGroupList);

            notifyDataSetChanged();
        }
    }

    private class ViewHolder
    {
        private TextView mTvGroupName;
    }
}
