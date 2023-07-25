package com.fis.ecocash.ecocash.ListAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fis.ecocash.ecocash.DataClasses.dbCategory;
import com.fis.ecocash.ecocash.R;

import java.util.List;

/**
 * Created by ashbelh on 27/4/2018.
 */

public class CategoryListAdapter extends ArrayAdapter<dbCategory> {
    private final Context context;
    private List<dbCategory> myDataset;
    public TextView account, balance,status,loan_amount;
    public ImageView img;

    public CategoryListAdapter(Context context, List<dbCategory> myDataset) {
        super(context, R.layout.row_layout_category,myDataset);
        this.context = context;
        this.myDataset = myDataset;
    }

    public int getCount() {
        return myDataset.size();
    }

    @Override
    public int getViewTypeCount() {
        if(myDataset.size()==0){
            return 1;
        }
        return myDataset.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        dbCategory feedback = myDataset.get(position);
        View rowView =null;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.row_layout_category, parent, false);
            account = (TextView) rowView.findViewById(R.id.textView6);
            //convertView.setTag(viewHolder);
            account.setText(feedback.getCategory());
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            rowView = (View) convertView;
        }
        return rowView;
    }
}

