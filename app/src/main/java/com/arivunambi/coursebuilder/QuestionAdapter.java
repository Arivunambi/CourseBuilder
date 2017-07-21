package com.arivunambi.coursebuilder;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by arivu on 6/28/2016.
 */
public class QuestionAdapter extends BaseExpandableListAdapter {

    private Context ctx;
    private HashMap<String, List<String>> question_map;
    private List<String> choice_list;

    public QuestionAdapter(Context ctx, HashMap<String, List<String>> question_map, List<String> choice_list )
    {
        this.ctx = ctx;
        this.question_map = question_map;
        this.choice_list = choice_list;

    }

    @Override
    public int getGroupCount() {
        return question_map.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return question_map.get(choice_list.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return choice_list.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return question_map.get(choice_list.get(groupPosition)).get(childPosition);
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
        String group_title = (String) getGroup(groupPosition);
        LinearLayout parentLayout = new LinearLayout(this.ctx);
        TextView question_content = new TextView(this.ctx);
        parentLayout.addView(question_content);
        if(convertView == null)
        {
            convertView = parentLayout;
        }
        question_content.setTypeface(null, Typeface.BOLD);
        question_content.setText(group_title);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        {
            String child_title = (String) getChild(groupPosition, childPosition);
            LinearLayout childLayout = new LinearLayout(this.ctx);
            TextView choice_content = new TextView(this.ctx);
            childLayout.addView(choice_content);
            if (convertView == null) {
                convertView = childLayout;
            }
            choice_content.setText(child_title);

            return convertView;
        }
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
