package com.fis.ecocash.ecocash.ListAdapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.fis.ecocash.ecocash.DataClasses.dbMonths;
import com.fis.ecocash.ecocash.DataClasses.dbSMS;
import com.fis.ecocash.ecocash.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by ashbelh on 7/4/2018.
 */

public class TransactionListAdapter extends BaseAdapter implements Filterable {

    private final Context context;
    protected Typeface mTfRegular ;
    private List<dbSMS> myDataset;
    private List<dbSMS>filteredData;
    public TextView account, balance,status,loan_amount, amounttxt,txt_charge;
    public ImageView img;
    ValueFilter valueFilter;


    public TransactionListAdapter(Context context, List<dbSMS> myDataset) {
        //super(context, R.layout.list_item,myDataset);
        this.context = context;
        this.myDataset = myDataset;
        filteredData = myDataset;
        mTfRegular = Typeface.createFromAsset(context.getAssets(), "OpenSans-Regular.ttf");
    }

    public int getCount() {
        return myDataset.size();
    }

    @Override
    public Object getItem(int i) {
        return myDataset.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
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
        dbSMS expandedListText = myDataset.get(position);
       // Log.e("VIEW", expandedListText.toString());
        View rowView =null;
        NumberFormat formatter = new DecimalFormat("#0.00");
        if (convertView == null) {
            //Log.e("CNVRT", "Convert View is NULL");
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.list_item, parent, false);

            txt_charge = (TextView) rowView.findViewById(R.id.balanceCharge);
            account = (TextView) rowView.findViewById(R.id.expandedListItem);
            amounttxt = (TextView) rowView.findViewById(R.id.expandedListItem1);
            status = (TextView) rowView.findViewById(R.id.expandedListItem2);
            loan_amount = (TextView) rowView.findViewById(R.id.tran_description);
            img = (ImageView) rowView.findViewById(R.id.imageView13);
            balance = (TextView) rowView.findViewById(R.id.balanceText);

            account.setText(expandedListText.getName().toUpperCase());
            account.setTypeface(mTfRegular, Typeface.BOLD);
            String amnt ="";
            if(expandedListText.getAmount()!=null) {
                amnt = formatter.format(expandedListText.getAmount());

                if (expandedListText.getAmount() < 0) {
                    amounttxt.setTextColor(Color.parseColor("#ffff4444"));
                    Double amount = expandedListText.getAmount() * -1;
                    amnt = formatter.format(amount);
                    amounttxt.setTypeface(mTfRegular, Typeface.BOLD);
                    amounttxt.setText("-$" + amnt);
                }
                if (expandedListText.getAmount() > 0) {
                    amounttxt.setTextColor(Color.parseColor("#ff669900"));
                    amounttxt.setText("$" + amnt);
                    amounttxt.setTypeface(mTfRegular, Typeface.BOLD);
                }
            }

            //balance.setTextColor(Color.parseColor("#ff669900"));
            balance.setText("(Bal: $" + formatter.format(expandedListText.getTranBalance())+")");
            balance.setTypeface(mTfRegular);

            //Log.e("Charge",expandedListText.getTranCharge()+"");
            txt_charge.setText(" Chg : $"+ formatter.format(expandedListText.getTranCharge()));
            txt_charge.setTypeface(mTfRegular);

            loan_amount.setText(expandedListText.getTrantyp());
            loan_amount.setTypeface(mTfRegular, Typeface.BOLD);
            SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
            Date date = new Date();
            long dateLong = Long.parseLong(expandedListText.getTrandate());
            date = new Date(dateLong);
            String datestr = format.format(date);
            status.setText(datestr);
            status.setTypeface(mTfRegular);

            //convertView.setTag(viewHolder);
        } else {
           // Log.e("CNVRT", "Convert View is NOT  NULL");
            // View is being recycled, retrieve the viewHolder object from tag
            rowView = (View) convertView;
            txt_charge = (TextView) rowView.findViewById(R.id.balanceCharge);
            account = (TextView) rowView.findViewById(R.id.expandedListItem);
            amounttxt = (TextView) rowView.findViewById(R.id.expandedListItem1);
            status = (TextView) rowView.findViewById(R.id.expandedListItem2);
            loan_amount = (TextView) rowView.findViewById(R.id.tran_description);
            img = (ImageView) rowView.findViewById(R.id.imageView13);
            balance = (TextView) rowView.findViewById(R.id.balanceText);

            account.setText(expandedListText.getName().toUpperCase());
            account.setTypeface(mTfRegular, Typeface.BOLD);
            String amnt ="";
            if(expandedListText.getAmount()!=null) {
                amnt = formatter.format(expandedListText.getAmount());

                if (expandedListText.getAmount() < 0) {
                    amounttxt.setTextColor(Color.parseColor("#ffff4444"));
                    Double amount = expandedListText.getAmount() * -1;
                    amnt = formatter.format(amount);
                    amounttxt.setTypeface(mTfRegular, Typeface.BOLD);
                    amounttxt.setText("-$" + amnt);
                }
                if (expandedListText.getAmount() > 0) {
                    amounttxt.setTextColor(Color.parseColor("#ff669900"));
                    amounttxt.setText("$" + amnt);
                    amounttxt.setTypeface(mTfRegular, Typeface.BOLD);
                }
            }

            //balance.setTextColor(Color.parseColor("#ff669900"));
            balance.setText("(Bal: $" + formatter.format(expandedListText.getTranBalance())+ ")");
            balance.setTypeface(mTfRegular);


            txt_charge.setText(" Chg : $"+ formatter.format(expandedListText.getTranCharge()));
            txt_charge.setTypeface(mTfRegular);

            loan_amount.setText(expandedListText.getTrantyp());
            loan_amount.setTypeface(mTfRegular, Typeface.BOLD);
            SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
            Date date = new Date();
            long dateLong = Long.parseLong(expandedListText.getTrandate());
            date = new Date(dateLong);
            String datestr = format.format(date);
            status.setText(datestr);
            status.setTypeface(mTfRegular);
        }
        return rowView;
    }

    // put below code (method) in Adapter class
//    public void filter(String charText) {
//        charText = charText.toLowerCase(Locale.getDefault());
//        myDataset.clear();
//        if (charText.length() == 0) {
//            myDataset.addAll(filteredData);
//        }
//        else
//        {
//            for (dbSMS wp : filteredData) {
//                if (wp.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
//                    myDataset.add(wp);
//                }
//            }
//        }
//        notifyDataSetChanged();
//    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            //myDataset.clear();
            if (constraint != null && constraint.length() > 0) {
                ArrayList<dbSMS> filterList = new ArrayList<dbSMS>();
                for (int i = 0; i < filteredData.size(); i++) {
                    if ( (filteredData.get(i).getName().toUpperCase() )
                            .contains(constraint.toString().toUpperCase())) {

                        dbSMS c = new dbSMS();
                        c.setName(filteredData.get(i).getName());
                        c.setTranId(filteredData.get(i).getTranId());
                        c.setTranMonth(filteredData.get(i).getTranMonth());
                        c.setTrandate(filteredData.get(i).getTrandate());
                        c.setTranTxt(filteredData.get(i).getTranTxt());
                        c.setTranBalance(filteredData.get(i).getTranBalance());
                        c.setTrantyp(filteredData.get(i).getTrantyp());
                        c.setAmount(filteredData.get(i).getAmount());
                        c.setId(filteredData.get(i).getId());
                        c.setTranCharge(filteredData.get(i).getTranCharge());
                       // Log.e("FI", c.toString());
                        filterList.add(c);
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = filteredData.size();
                results.values = filteredData;
            }
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {

            myDataset = (ArrayList<dbSMS>) results.values;
            notifyDataSetChanged();
        }

    }

    public void filter(long sdate, long edate) {
        if (edate != 0 && sdate!= 0) {
            ArrayList<dbSMS> filterList = new ArrayList<dbSMS>();
            for (int i = 0; i < filteredData.size(); i++) {
                if ( Long.valueOf(filteredData.get(i).getTrandate()) >=(sdate) && Long.valueOf(filteredData.get(i).getTrandate()) <= (edate) ) {
                    dbSMS c = new dbSMS();
                    c.setName(filteredData.get(i).getName());
                    c.setTranId(filteredData.get(i).getTranId());
                    c.setTranMonth(filteredData.get(i).getTranMonth());
                    c.setTrandate(filteredData.get(i).getTrandate());
                    c.setTranTxt(filteredData.get(i).getTranTxt());
                    c.setTranBalance(filteredData.get(i).getTranBalance());
                    c.setTrantyp(filteredData.get(i).getTrantyp());
                    c.setAmount(filteredData.get(i).getAmount());
                    c.setId(filteredData.get(i).getId());
                    filterList.add(c);
                }
            }
            filteredData = filterList;
        } else {
        }
        notifyDataSetChanged();
    }
}
