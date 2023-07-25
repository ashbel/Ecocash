package com.fis.ecocash.ecocash;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.fis.ecocash.ecocash.DataClasses.dbHome;
import com.fis.ecocash.ecocash.DataClasses.dbMonths;
import com.fis.ecocash.ecocash.DataClasses.dbOperations;
import com.fis.ecocash.ecocash.DataClasses.dbSMS;
import com.fis.ecocash.ecocash.ListAdapters.MainListAdapter;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class HomeFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, OnChartValueSelectedListener {

    PieChart mChart;
    dbHome home_;
    protected Typeface mTfRegular;
    protected Typeface mTfLight;
    DecimalFormat df = new DecimalFormat("0.00");
    private dbOperations dboperation;
    View view;
    SimpleDateFormat format = new SimpleDateFormat("MMMM yyyy");
    public HomeFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Here, thisActivity is the current activity
        view = inflater.inflate(R.layout.fragment_home, container, false);


        //view = inflater.inflate(R.layout.fragment_home, container, false);
        dboperation = new dbOperations(getContext());
        dboperation.open();
        home_ = new dbHome();
        home_ = getSmsLogs();
        List[] amount = getSmsLog();
        dbMonths m = new dbMonths();

        final List<dbMonths> mylist = getMonthData(amount[1],amount[0]);
        m.setMonth("TRANSACTION CHARGES");
        m.setIn(home_.getCharge()*-1);
        m.setOut(0.00);
        mylist.add(m);


        final ListView listview = (ListView) view.findViewById(R.id.listview4);

        MainListAdapter adapter = new MainListAdapter(getContext(), mylist);
        listview.setAdapter(adapter);

        mTfRegular = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Regular.ttf");
        mTfLight = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Light.ttf");
        //Log.e("COUNT", "IM HERE");
        readSMS();
        int i = dboperation.mobileCount();
        //Log.e("COUNT", String.valueOf(i));
        dboperation.close();
        return view;
    }

    private dbHome getSmsLogs() {

        Double in = 0.00;
        Double out = 0.00;
        Double bill = 0.00;
        Double merchant = 0.00;
        Double balance = 0.00;
        Double cashOut = 0.00;
        Double airtime = 0.00;
        Double total_in = 0.00;
        Double total_out = 0.00;
        Double received = 0.00;
        Double charge =0.00;
        int count = 0;
        dbHome home = new dbHome();
        Date date = new Date();
        Date dates = new Date();

        List<dbSMS> transactions = dboperation.getAllTransactions();

        if(transactions.size()>0) {

            for (dbSMS dbsms : transactions) {
                long dateLong = Long.parseLong(dbsms.getTrandate());
                date = new Date(dateLong);
                //Log.e("BAL", date+"");
                if (date.getMonth() == dates.getMonth() && date.getYear() == dates.getYear()) {
                    charge = charge + dbsms.getTranCharge();
                    if(dbsms.getAmount()>0)
                    {
                        total_in = total_in + dbsms.getAmount();
                    }
                    else {
                        total_out = total_out + dbsms.getAmount();
                    }
//                    if (dbsms.getTrantyp().equals("Money Sent")) {
//                        out = out + dbsms.getAmount();
//                        total_out = total_out + dbsms.getAmount();
//                    } else if (dbsms.getTrantyp().equals("Merchant Payment")) {
//                        merchant = merchant + dbsms.getAmount();
//                        total_out = total_out + dbsms.getAmount();
//                    } else if (dbsms.getTrantyp().equals("Cash Out")) {
//                        cashOut = cashOut + dbsms.getAmount();
//                        total_out = total_out + dbsms.getAmount();
//                    } else if (dbsms.getTrantyp().equals("Bill Payment")) {
//                        bill = bill + dbsms.getAmount();
//                        total_out = total_out + dbsms.getAmount();
//                    } else if (dbsms.getTrantyp().equals("Airtime")) {
//                        airtime = airtime + dbsms.getAmount();
//                        total_out = total_out + dbsms.getAmount();
//                    } else if (dbsms.getTrantyp().equals("Cash In")) {
//                        in = in + dbsms.getAmount();
//                        total_in = total_in + dbsms.getAmount();
//                    } else if (dbsms.getTrantyp().equals("Money Received")) {
//                        received = received + dbsms.getAmount();
//                        total_in = total_in + dbsms.getAmount();
//                    }
                }
            }

            dbSMS d = transactions.get(0);


            home.setIn(in);
            home.setOut(out * -1);
            home.setBill(bill * -1);
            home.setMerchant(merchant * -1);
            home.setBalance(d.getTranBalance());
            home.setCashOut(cashOut * -1);
            home.setTotalIn(total_in);
            home.setAirtime(airtime);
            home.setTotalOut(total_out * -1);
            home.setReceived(received);
            home.setCharge(charge);
        }
        else{
            home.setIn(in);
            home.setOut(out * -1);
            home.setBill(bill * -1);
            home.setMerchant(merchant * -1);
            home.setBalance(0.00);
            home.setCashOut(cashOut * -1);
            home.setTotalIn(total_in);
            home.setAirtime(airtime);
            home.setTotalOut(total_out * -1);
            home.setReceived(received);
            home.setCharge(charge);
        }
        return home;

    }

    public void readSMS(){


        home_ = getSmsLogs();
        Date date = new Date();

        TextView txtBal = (TextView) view.findViewById(R.id.love_music);
        TextView txtIn = (TextView) view.findViewById(R.id.in);
        TextView txtOut = (TextView) view.findViewById(R.id.out);
        TextView txtMonth = (TextView) view.findViewById(R.id.month);


        float amount_in = Float.parseFloat(String.valueOf(home_.getIn()));
        float amount_out = Float.parseFloat(String.valueOf(home_.getOut()));
        float amount_bill = Float.parseFloat(String.valueOf(home_.getBill()));
        float amount_merchant = Float.parseFloat(String.valueOf(home_.getMerchant()));
        float cash_out = Float.parseFloat(String.valueOf(home_.getCashOut()));
        float received_amount = Float.parseFloat(String.valueOf(home_.getReceived()));

        String balance = "Balance : $" + df.format(home_.getBalance());
        String status = "Status : ";
        String in_str = "Total In : $" + df.format(home_.getTotalIn());
        String out_str = "Total Out : $" + df.format(home_.getTotalOut());

        txtMonth.setText(format.format(date));
        txtMonth.setTypeface(mTfRegular);

        txtBal.setTypeface(mTfRegular);
        txtIn.setTypeface(mTfRegular,Typeface.BOLD);
        txtOut.setTypeface(mTfRegular,Typeface.BOLD);
        txtBal.setText(balance);
        txtIn.setText(in_str);
//        txtIn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_arrow_downward_black_24dp, 0, 0, 0);
        txtOut.setText(out_str);
//        txtOut.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_arrow_upward_black_24dp, 0, 0, 0);

//        mChart = (PieChart) view.findViewById(R.id.piechart);
//        mChart.setUsePercentValues(false);
//        mChart.getDescription().setEnabled(false);
//        mChart.setExtraOffsets(5, 10, 5, 5);
//        mChart.setDragDecelerationFrictionCoef(0.95f);
//        mChart.setCenterTextTypeface(mTfLight);
//        mChart.setCenterText(generateCenterSpannableText("default","default"));
//        mChart.setDrawHoleEnabled(true);
//        mChart.setHoleColor(Color.WHITE);
//        mChart.setTransparentCircleColor(Color.WHITE);
//        mChart.setTransparentCircleAlpha(110);
//        mChart.setHoleRadius(58f);
//        mChart.setTransparentCircleRadius(61f);
//        mChart.setDrawCenterText(true);
//        mChart.setRotationAngle(0);
//        mChart.setRotationEnabled(true);
//        mChart.setHighlightPerTapEnabled(true);
//        mChart.animateY(100, Easing.EasingOption.EaseInOutQuad);
//        // add a selection listener
//        mChart.setOnChartValueSelectedListener(this);
//        Legend l = mChart.getLegend();
//        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
//        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
//        l.setOrientation(Legend.LegendOrientation.VERTICAL);
//        l.setDrawInside(false);
//        l.setXEntrySpace(7f);
//        l.setYEntrySpace(0f);
//        l.setYOffset(0f);
//        l.setEnabled(false);
//        mChart.setEntryLabelColor(Color.WHITE);
//        mChart.setEntryLabelTypeface(mTfRegular);
//        mChart.setEntryLabelTextSize(12f);
//        ArrayList<PieEntry> yValues = new ArrayList<>();
//        if(amount_in>0) {
//            yValues.add(new PieEntry(amount_in, "Cash In"));
//        }
//        if(received_amount>0) {
//            yValues.add(new PieEntry(received_amount, "Received"));
//        }
//        if(cash_out>0) {
//            yValues.add(new PieEntry(cash_out, "Cash Out"));
//        }
//        if(amount_out>0) {
//            yValues.add(new PieEntry(amount_out, "Sent"));
//        }
//        if(amount_bill>0) {
//            yValues.add(new PieEntry(amount_bill, "Bill Payment"));
//        }
//        if(amount_merchant>0) {
//            yValues.add(new PieEntry(amount_merchant, "Merchant Payment"));
//        }
//        PieDataSet dataSet = new PieDataSet(yValues, "Ecocash");
//        dataSet.setSliceSpace(3f);
//        dataSet.setSelectionShift(5f);
//        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
//        PieData data = new PieData(dataSet);
//        data.setValueTextColor(Color.BLACK);
//        data.setValueFormatter(new CurrencyFormatter());
//        data.setValueTextSize(15f);
//        data.setValueTypeface(mTfLight);
//
//
//        mChart.setData(data);

        //return home_;
    }

    private List[] getSmsLog() {
        Date date = new Date();
        List[] amount = new List[2];
        String value = format.format(date);
        ArrayList<String> dtmthns = new ArrayList<>();
        ArrayList<dbSMS> dbList = new ArrayList<>();
        List<dbSMS> transactions = dboperation.getAllTransactions(value);
        for(dbSMS dbsms : transactions) {
            if (!dtmthns.contains(dbsms.getTrantyp())) {
                dtmthns.add(dbsms.getTrantyp());
            }
            dbList.add(dbsms);
        }
        amount[0] = dbList;
        amount[1] = dtmthns;
        return amount;
    }

    public List<dbMonths> getMonthData(List<String> mylist, List<dbSMS> smsList) {
        List<dbMonths> c = new ArrayList<dbMonths>();
        SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy");
        String datestr = "";
        Collections.sort(mylist);
        Date date = new Date();
        for (String y: mylist) {
            Double in=0.0;
            Double out=0.0;
            Double balance = 0.00;
            dbMonths months = new dbMonths();
            for (dbSMS h: smsList) {
                if(h.getTrantyp().trim().toUpperCase().contentEquals(y.toUpperCase())){
                    if(h.getAmount()!=null){
                        in = in + h.getAmount();
                        // Log.e("IN",h.getTrantyp() + " : "+h.getAmount());
                    }
                }
                months.setIn(in);
                months.setMonth(y.toUpperCase());

            }
            c.add(months);
        }
        return c;
    }

    private SpannableString generateCenterSpannableText(String text,String text1) {

        if(text.contains("default")) {

            Calendar cal = Calendar.getInstance();
            String month = (new SimpleDateFormat("MMMM").format(cal.getTime()));
           // Log.e("MON", month);

            SpannableString s = new SpannableString(month + " \nOverview");
            s.setSpan(new RelativeSizeSpan(2.7f), 0, s.length(), 0);
            return s;
        }
        else
        {
            Double amount  = Double.parseDouble(text);

            SpannableString s = new SpannableString("$" + df.format(amount) + " \n" +text1);
            s.setSpan(new RelativeSizeSpan(2.7f), 0, s.length(), 0);
            return s;
        }

    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

        if (e == null)
            return;
        PieEntry pe = (PieEntry) e;
       // mChart.setCenterText(generateCenterSpannableText(""+((PieEntry) e).getValue(),""+ ((PieEntry) e).getLabel()));
//       // Log.i("VAL SELECTED",
//                "Value: " + ((PieEntry) e).getValue() + ", Label: " + ((PieEntry) e).getLabel()  + ", index: " + h.getX()
//                        + ", DataSet index: " + h.getDataSetIndex());
    }

    @Override
    public void onNothingSelected() {
        //mChart.setCenterText(generateCenterSpannableText("default","default"));
        //Log.i("PieChart", "nothing selected");
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

}
