package com.wuchangi.freechat.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.wuchangi.freechat.R;
import com.wuchangi.freechat.bean.PickContactInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WuchangI on 2018/11/22.
 */

public class PickContactListViewAdapter extends BaseAdapter
{
    private Context mContext;

    private List<PickContactInfo> mPickContactInfoList = new ArrayList<>();

    private List<String> mMembers = new ArrayList<>();

    public PickContactListViewAdapter(Context context, List<PickContactInfo> pickContactInfoList, List<String> members)
    {
        mContext = context;

        if(mPickContactInfoList != null && mPickContactInfoList.size() >= 0)
        {
            mPickContactInfoList.clear();
            mPickContactInfoList.addAll(pickContactInfoList);
        }

        mMembers.clear();
        mMembers.addAll(members);
    }


    @Override
    public int getCount()
    {
        return mPickContactInfoList == null ? 0 : mPickContactInfoList.size();
    }


    @Override
    public Object getItem(int position)
    {
        return mPickContactInfoList.get(position);
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

        if(convertView == null)
        {
            viewHolder = new ViewHolder();

            convertView = View.inflate(mContext, R.layout.item_pick, null);

            viewHolder.mCbPick = convertView.findViewById(R.id.cb_pick);
            viewHolder.mTvContactName = convertView.findViewById(R.id.tv_contact_name);

            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        PickContactInfo pickContactInfo = mPickContactInfoList.get(position);

        viewHolder.mCbPick.setChecked(pickContactInfo.isChecked());
        viewHolder.mTvContactName.setText(pickContactInfo.getUserInfo().getUserName());

        if(mMembers.contains(pickContactInfo.getUserInfo().getUserName()))
        {
            viewHolder.mCbPick.setChecked(true);
            pickContactInfo.setChecked(true);
        }

        return convertView;
    }

    public List<String> getPickContactList()
    {
        List<String> pickContactList = new ArrayList<>();

        for(PickContactInfo pickContactInfo: mPickContactInfoList)
        {
            if(pickContactInfo.isChecked())
            {
                pickContactList.add(pickContactInfo.getUserInfo().getUserName());
            }
        }

        return pickContactList;
    }


    private class ViewHolder
    {
        private CheckBox mCbPick;
        private TextView mTvContactName;
    }
}
