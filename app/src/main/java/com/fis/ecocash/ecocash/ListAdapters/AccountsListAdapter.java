package com.fis.ecocash.ecocash.ListAdapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ashbelh on 7/4/2018.
 */

public class AccountsListAdapter extends BaseExpandableListAdapter implements Filterable {

    private List<dbMonths> expandableListTitle;
    private List<dbMonths> filteredData;
    private HashMap<dbMonths, List<dbSMS>> expandableListDetail;
    private HashMap<dbMonths, List<dbSMS>> expandableListDetailFilter;
    public TextView account, balance,status,loan_amount;
    public ImageView img;
    private Context context;
    protected Typeface mTfRegular ;
    ValueFilter valueFilter;

    public AccountsListAdapter(Context context, List<dbMonths> expandableListTitle,
                               HashMap<dbMonths, List<dbSMS>> expandableListDetail) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
        filteredData = expandableListTitle;
        expandableListDetailFilter = expandableListDetail;
        mTfRegular = Typeface.createFromAsset(context.getAssets(), "OpenSans-Regular.ttf");
    }

    @Override
    public int getGroupCount() {
        return this.expandableListTitle.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return this.expandableListDetail.get(this.expandableListTitle.get(i))
                .size();
    }

    @Override
    public Object getGroup(int i) {
        return this.expandableListTitle.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return this.expandableListDetail.get(this.expandableListTitle.get(i))
                .get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        dbMonths loans = (dbMonths) getGroup(i);
        NumberFormat formatter = new DecimalFormat("#0.00");
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.row_layout_accounts, null);
        }
        account = (TextView) view.findViewById(R.id.textAccount);
        balance = (TextView) view.findViewById(R.id.textBalance);
        status = (TextView) view.findViewById(R.id.textStatus);
//        loan_amount = (TextView) view.findViewById(R.id.textAmount);
        img = (ImageView) view.findViewById(R.id.imageView13);

        account.setText(""+loans.getMonth());
        account.setTypeface(mTfRegular, Typeface.BOLD);
        balance.setText("Out : -$"+ formatter.format(loans.getOut()*-1 ));
        balance.setTypeface(mTfRegular, Typeface.BOLD);
        status.setText("In : $" +formatter.format(loans.getIn()));
        status.setTypeface(mTfRegular, Typeface.BOLD);
//        loan_amount.setText("Balance : $"+formatter.format(loans.getBalance()));
//        loan_amount.setTypeface(mTfRegular, Typeface.BOLD);

        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        final dbSMS expandedListText = (dbSMS) getChild(i, i1);
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.list_item, null);
        }
        TextView expandedListTextView = (TextView) view
                .findViewById(R.id.expandedListItem);
        TextView expandedListTextView1 = (TextView) view
                .findViewById(R.id.expandedListItem1);
        TextView expandedListTextView2 = (TextView) view
                .findViewById(R.id.expandedListItem2);
        TextView tran_desc = (TextView) view
                .findViewById(R.id.tran_description);

        expandedListTextView.setText(expandedListText.getName().toUpperCase());
        expandedListTextView.setTypeface(mTfRegular, Typeface.BOLD);
        NumberFormat formatter = new DecimalFormat("#0.00");
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
        Date date = new Date();
        long dateLong = Long.parseLong(expandedListText.getTrandate());
        date = new Date(dateLong);
        String datestr = format.format(date);
       // Log.e("AC",expandedListText.getName() + " AMT "+ expandedListText.getAmount() + "DATE" + datestr);
        String amnt = formatter.format(expandedListText.getAmount());

        if(expandedListText.getAmount()<0) {
            expandedListTextView1.setTextColor(Color.parseColor("#ffff4444"));
            Double amount = expandedListText.getAmount()*-1;
            amnt = formatter.format(amount);
            expandedListTextView1.setTypeface(mTfRegular, Typeface.BOLD);
            expandedListTextView1.setText("-$"+amnt);
        }
        if(expandedListText.getAmount()>=0) {
            expandedListTextView1.setTextColor(Color.parseColor("#ff669900"));
            expandedListTextView1.setText("$"+amnt);
            expandedListTextView1.setTypeface(mTfRegular, Typeface.BOLD);
        }
        tran_desc.setText(expandedListText.getTrantyp());
        tran_desc.setTypeface(mTfRegular, Typeface.BOLD);
        expandedListTextView2.setText(datestr);
        expandedListTextView2.setTypeface(mTfRegular);
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

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
                ArrayList<dbMonths> filterList = new ArrayList<dbMonths>();
                HashMap<dbMonths, List<dbSMS>> expandableListDet = new HashMap<>();
                for (int i = 0; i < filteredData.size(); i++) {
                    if ( (filteredData.get(i).getMonth().toUpperCase() )
                            .contains(constraint.toString().toUpperCase())) {
                        dbMonths c = new dbMonths();
                        c.setMonth(filteredData.get(i).getMonth());
                        c.setBalance(filteredData.get(i).getBalance());
                        c.setIn(filteredData.get(i).getIn());
                        c.setOut(filteredData.get(i).getOut());
                         //Log.e("FI", expandableListDetailFilter.get(filteredData.get(i)));
                        filterList.add(c);
                        //expandableListDetail.clear();
                        expandableListDet.put(c, expandableListDetailFilter.get(filteredData.get(i)));
                    }
                }
                results.count = expandableListDet.size();
                results.values = expandableListDet;

            } else {
                results.count = expandableListDetailFilter.size();
                results.values = expandableListDetailFilter;
            }
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            expandableListDetail = (HashMap<dbMonths, List<dbSMS>>) results.values;
            expandableListTitle = new ArrayList<dbMonths>(expandableListDetail.keySet()) ;
            notifyDataSetChanged();
        }

    }

}
