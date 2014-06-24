
package com.fei_ke.chiphellclient.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import com.fei_ke.chiphellclient.bean.Plate;
import com.fei_ke.chiphellclient.bean.PlateGroup;
import com.fei_ke.chiphellclient.ui.customviews.PlateGroupView;
import com.fei_ke.chiphellclient.ui.customviews.PlateItemView;

import java.util.ArrayList;
import java.util.List;

/**
 * 版块列表适配器
 * 
 * @author 杨金阳
 * @2014-6-14
 */
public class PlateListAdapter extends BaseExpandableListAdapter {

    private List<PlateGroup> mPlateGroups;

    public PlateListAdapter(List<PlateGroup> mPlateGroups) {
        super();
        this.mPlateGroups = mPlateGroups;
    }

    @Override
    public int getGroupCount() {
        return mPlateGroups == null ? 0 : mPlateGroups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mPlateGroups == null ? 0 : mPlateGroups.get(groupPosition).getPlates().size();
    }

    @Override
    public PlateGroup getGroup(int groupPosition) {
        return mPlateGroups.get(groupPosition);
    }

    @Override
    public Plate getChild(int groupPosition, int childPosition) {
        return getGroup(groupPosition).getPlates().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        PlateGroup group = getGroup(groupPosition);
        PlateGroupView plateGroupView = null;
        if (convertView == null) {
            plateGroupView = PlateGroupView.getInstance(parent.getContext());
        } else {
            plateGroupView = (PlateGroupView) convertView;
        }
        plateGroupView.bindValue(group);
        return plateGroupView;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, final ViewGroup parent) {
        Plate plate = getChild(groupPosition, childPosition);
        PlateItemView plateItemView = null;
        if (convertView == null) {
            plateItemView = PlateItemView.getInstance(parent.getContext());
        } else {
            plateItemView = (PlateItemView) convertView;
        }
        plateItemView.bindValue(plate);
        return plateItemView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    /**
     * 更新列表
     * 
     * @param groups
     */
    public void updateDatas(List<PlateGroup> groups) {
        if (mPlateGroups == null) {
            this.mPlateGroups = new ArrayList<PlateGroup>();
        }
        this.mPlateGroups.clear();
        this.mPlateGroups.addAll(groups);
        this.notifyDataSetChanged();
    }

}
