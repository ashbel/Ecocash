package com.fis.ecocash.ecocash.ListAdapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fis.ecocash.ecocash.DataClasses.dbMonths;
import com.fis.ecocash.ecocash.DataClasses.dbSMS;
import com.fis.ecocash.ecocash.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

/**
 * Created by ashbelh on 7/4/2018.
 */

public class MonthsListAdapter extends ArrayAdapter<dbMonths> {

    private final Context context;
    protected Typeface mTfRegular ;
    private List<dbMonths> myDataset;
    public TextView account, balance,status,loan_amount;
    public ImageView img;

    public MonthsListAdapter(Context context, List<dbMonths> myDataset) {
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
            rowView = inflater.inflate(R.layout.row_layout_months, parent, false);
            account = (TextView) rowView.findViewById(R.id.textAccount);
            balance = (TextView) rowView.findViewById(R.id.textBalance);
            status = (TextView) rowView.findViewById(R.id.textStatus);
            loan_amount = (TextView) rowView.findViewById(R.id.textAmount);
            img = (ImageView) rowView.findViewById(R.id.imageView13);


            account.setText(""+loans.getMonth());
            account.setTypeface(mTfRegular, Typeface.BOLD);
            balance.setText("Out : -$"+ formatter.format(loans.getOut()*-1 ));
            balance.setTypeface(mTfRegular, Typeface.BOLD);
            status.setText("In : $" +formatter.format(loans.getIn()));
            status.setTypeface(mTfRegular, Typeface.BOLD);
            loan_amount.setText("Balance : $"+formatter.format(loans.getBalance()));
            loan_amount.setTypeface(mTfRegular, Typeface.BOLD);

            //convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            rowView = (View) convertView;
        }
        return rowView;
    }
}
