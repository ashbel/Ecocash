package com.fis.ecocash.ecocash.ListAdapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fis.ecocash.ecocash.DataClasses.dbMonths;
import com.fis.ecocash.ecocash.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

/**
 * Created by ashbelh on 27/4/2018.
 */

public class MainListAdapter extends ArrayAdapter<dbMonths> {

private final Context context;
protected Typeface mTfRegular ;
private List<dbMonths> myDataset;
public TextView account, balance,status,loan_amount;
public ImageView img;

public MainListAdapter(Context context, List<dbMonths> myDataset) {
        super(context, R.layout.row_layout_months,myDataset);
        this.context = context;
        this.myDataset = myDataset;

        mTfRegular = Typeface.createFromAsset(context.getAssets(), "OpenSans-Regular.ttf");
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
        dbMonths loans = myDataset.get(position);
        View rowView =null;
        NumberFormat formatter = new DecimalFormat("#0.00");
        if (convertView == null) {
        LayoutInflater inflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rowView = inflater.inflate(R.layout.row_layout_main, parent, false);
        account = (TextView) rowView.findViewById(R.id.textAccount);
        balance = (TextView) rowView.findViewById(R.id.textBalance);
        img = (ImageView) rowView.findViewById(R.id.imageView13);
        account.setText(""+loans.getMonth());
        account.setTypeface(mTfRegular, Typeface.BOLD);


        if(loans.getIn()<0) {
        balance.setTextColor(Color.parseColor("#ffff4444"));
        Double amount = loans.getIn()*-1;
        String amnt = formatter.format(amount);
        balance.setTypeface(mTfRegular, Typeface.BOLD);
        balance.setText("-$"+amnt);
        }
        else {
        balance.setTextColor(Color.parseColor("#ff669900"));
        balance.setText("$"+ formatter.format(loans.getIn() ));
        balance.setTypeface(mTfRegular, Typeface.BOLD);
        }


        } else {
        rowView = (View) convertView;
        }
        return rowView;
        }
        }


