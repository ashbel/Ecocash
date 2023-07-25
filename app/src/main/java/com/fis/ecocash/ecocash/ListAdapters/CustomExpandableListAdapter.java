package com.fis.ecocash.ecocash.ListAdapters;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;

import com.fis.ecocash.ecocash.DataClasses.dbSMS;
import com.fis.ecocash.ecocash.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static android.app.PendingIntent.getActivity;
import static java.security.AccessController.getContext;

/**
 * Created by ashbelh on 2/4/2018.
 */

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> expandableListTitle;
    private HashMap<String, List<dbSMS>> expandableListDetail;
    private RecyclerView.ViewHolder mViewHolder;
    private List<dbSMS> mylist;
    protected Typeface mTfRegular ;
    public AssetManager assetManager;


    public CustomExpandableListAdapter(Context context, List<String> expandableListTitle,
                                       HashMap<String, List<dbSMS>> expandableListDetail) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;

        mTfRegular = Typeface.createFromAsset(context.getAssets(), "OpenSans-Regular.ttf");
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final dbSMS expandedListText = (dbSMS) getChild(listPosition, expandedListPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item, null);
        }
        TextView expandedListTextView = (TextView) convertView
                .findViewById(R.id.expandedListItem);
        TextView expandedListTextView1 = (TextView) convertView
                .findViewById(R.id.expandedListItem1);
        TextView expandedListTextView2 = (TextView) convertView
                .findViewById(R.id.expandedListItem2);
        TextView tran_desc = (TextView) convertView
                .findViewById(R.id.tran_description);

        expandedListTextView.setText(expandedListText.getName().toUpperCase());
        expandedListTextView.setTypeface(mTfRegular, Typeface.BOLD);
        NumberFormat formatter = new DecimalFormat("#0.00");
        String amnt = formatter.format(expandedListText.getAmount());

        if(expandedListText.getAmount()<0) {
            expandedListTextView1.setTextColor(Color.parseColor("#ffff4444"));
            Double amount = expandedListText.getAmount()*-1;
            amnt = formatter.format(amount);
            expandedListTextView1.setTypeface(mTfRegular, Typeface.BOLD);
            expandedListTextView1.setText("-$"+amnt);
        }
        if(expandedListText.getAmount()>0) {
            expandedListTextView1.setTextColor(Color.parseColor("#ff669900"));
            expandedListTextView1.setText("$"+amnt);
            expandedListTextView1.setTypeface(mTfRegular, Typeface.BOLD);
        }
        tran_desc.setText(expandedListText.getTrantyp());
        tran_desc.setTypeface(mTfRegular, Typeface.BOLD);
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
        Date date = new Date();
        long dateLong = Long.parseLong(expandedListText.getTrandate());
        date = new Date(dateLong);
        String datestr = format.format(date);
        expandedListTextView2.setText(datestr);
        expandedListTextView2.setTypeface(mTfRegular);
        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.expandableListTitle.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.expandableListTitle.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String listTitle = (String) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_group, null);
        }
        TextView listTitleTextView = (TextView) convertView
                .findViewById(R.id.listTitle);
        Log.e("month",listTitle);
        listTitleTextView.setTypeface(mTfRegular, Typeface.BOLD);
        listTitleTextView.setText(listTitle);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }
}

