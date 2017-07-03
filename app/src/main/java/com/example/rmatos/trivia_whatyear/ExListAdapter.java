package com.example.rmatos.trivia_whatyear;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import java.util.List;
import java.util.Map;

/**
 * Created by RMatos on 03/07/2017.
 */

public class ExListAdapter extends BaseExpandableListAdapter {


    private Context context;
    private List<String> themes;
    private Map<String, List<String>> categories;
    private boolean[][] cbStates;


    public ExListAdapter(Context context, List<String> themes, Map<String, List<String>> categories) {
        this.context = context;
        this.themes = themes;
        this.categories = categories;

        cbStates = new boolean[9][13];
        tempMethod();                                                                               //TODO: REMOVE
    }

    private void tempMethod() {

        for (int i = 0; i <= 8; i++) {
            for (int j = 0; j <= 12; j++) {
                cbStates[i][j] = false;
            }
        }
    }

    //Number of parents (group == parent)
    @Override
    public int getGroupCount() {
        return themes.size();
    }

    //Number of children
    @Override
    public int getChildrenCount(int groupPosition) {
        return categories.get(themes.get(groupPosition)).size();
    }

    //Gets name of group
    @Override
    public Object getGroup(int groupPosition) {
        return themes.get(groupPosition);
    }

    //Gets name of child
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return categories.get(themes.get(groupPosition)).get(childPosition);
    }

    //Gets group id (keep as position)
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    //Gets child id (keep as position)
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }


    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        String group = (String) getGroup(groupPosition);

        //If object isnt already created
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_parent, null);
        }

        TextView twParent = (TextView) convertView.findViewById(R.id.categories_list_parent);
        twParent.setText(group);

        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        String category = (String) getChild(groupPosition, childPosition);

        //If object isnt already created
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_child, null);
        }

        TextView twChild = (TextView) convertView.findViewById(R.id.categories_list_child);
        twChild.setText(category);

        CheckBox cb = (CheckBox) convertView.findViewById(R.id.categories_list_child_checkbox);

        //Sets checkboxes to current state
        cb.setChecked(cbStates[groupPosition][childPosition] == true);

        //EventListener for checkboxes
        cb.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                final CheckBox cb = (CheckBox) view;
                cbStates[groupPosition][childPosition] = cb.isChecked();
            }
        });

        return convertView;
    }



}
