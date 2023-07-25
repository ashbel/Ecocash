package com.fis.ecocash.ecocash.ListAdapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fis.ecocash.ecocash.DataClasses.dbMonths;
import com.fis.ecocash.ecocash.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

/**
 * Created by ashbelh on 29/4/2018.
 */

public class MainCardAdapter  extends RecyclerView.Adapter<MainCardAdapter.MyViewHolder> {

    private Context mContext;
    private List<dbMonths> months;
    NumberFormat formatter = new DecimalFormat("#0.00");
    protected Typeface mTfRegular ;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count,amount;
        public ImageView thumbnail, overflow;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
           // count = (TextView) view.findViewById(R.id.count);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            overflow = (ImageView) view.findViewById(R.id.overflow);
            amount = (TextView) view.findViewById(R.id.amount);
        }
    }

    public MainCardAdapter(Context mContext, List<dbMonths> months) {
        this.mContext = mContext;
        this.months = months;
        mTfRegular = Typeface.createFromAsset(mContext.getAssets(), "OpenSans-Regular.ttf");
    }

    @Override
    public MainCardAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.overview_card, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MainCardAdapter.MyViewHolder holder, int position) {
        dbMonths month = months.get(position);
        holder.title.setText(month.getMonth());
       // holder.count.setText(formatter.format(month.getIn()));
        //holder.count.setTypeface(mTfRegular);
        holder.title.setTypeface(mTfRegular,Typeface.BOLD);
        holder.amount.setTypeface(mTfRegular,Typeface.BOLD);

        if(month.getIn()<0) {

            holder.amount.setTextColor(Color.parseColor("#ffff4444"));
            Double amount = month.getIn()*-1;
            String amnt = formatter.format(amount);
            holder.amount.setText("-$"+amnt);
        }
        else {
            holder.amount.setTextColor(Color.parseColor("#ff669900"));
            holder.amount.setText("$"+ formatter.format(month.getIn() ));
        }

    }

    @Override
    public int getItemCount() {
        return months.size();
    }

}
