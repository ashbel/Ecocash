package com.fis.ecocash.ecocash;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
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
import com.fis.ecocash.ecocash.ListAdapters.HomeListAdapter;
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


public class OverviewFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, OnChartValueSelectedListener {

    PieChart mChart;
    dbHome home_;
    protected Typeface mTfRegular;
    protected Typeface mTfLight;
    DecimalFormat df = new DecimalFormat("0.00");
    ArrayList<dbSMS> sms_db = new ArrayList<dbSMS>();
    String value="";
    private dbOperations dboperation;

    public OverviewFragment() {
        // Required empty public constructor
    }

    public static OverviewFragment newInstance(String param1, String param2) {
        OverviewFragment fragment = new OverviewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_overview, container, false);
        dboperation = new dbOperations(getContext());
        dboperation.open();

        Bundle b = this.getArguments();;
        if(b != null)
            value = b.getString("month");

        home_ = new dbHome();
        home_ = getSmsLogs();
        List[] amount = getSmsLog();
        dbMonths m = new dbMonths();

        final List<dbMonths> mylist = getMonthData(amount[1],amount[0]);
        m.setMonth("TRANSACTION CHARGES");
        m.setIn(home_.getCharge()*-1);
        m.setOut(0.00);
        mylist.add(m);


        TextView txtBal = (TextView) view.findViewById(R.id.balance);
        TextView txtDate = (TextView) view.findViewById(R.id.month);
        TextView txtIn = (TextView) view.findViewById(R.id.in);
        TextView txtOut = (TextView) view.findViewById(R.id.out);



        float amount_in = Float.parseFloat(String.valueOf(home_.getIn()));
        float amount_out = Float.parseFloat(String.valueOf(home_.getOut()));
        float amount_bill = Float.parseFloat(String.valueOf(home_.getBill()));
        float amount_merchant = Float.parseFloat(String.valueOf(home_.getMerchant()));
        float cash_out = Float.parseFloat(String.valueOf(home_.getCashOut()));
        String balance = "Balance : $" + df.format(home_.getBalance());
        String charges = "Transaction Charges : $" + df.format(home_.getCharge());
        String status = "Status : ";
        String in_str = "In : $" + df.format(home_.getTotalIn());
        String out_str = "Out : $" + df.format(home_.getTotalOut());

        txtBal.setTypeface(mTfRegular);
        txtIn.setTypeface(mTfRegular);
        txtOut.setTypeface(mTfRegular);
        txtDate.setTypeface(mTfRegular);
        txtBal.setText(balance);
        txtIn.setText(in_str);
        txtOut.setText(out_str);
        txtDate.setText("("+value+")");


        final ListView listview = (ListView) view.findViewById(R.id.listview4);

        HomeListAdapter adapter = new HomeListAdapter(getContext(), mylist);
        listview.setAdapter(adapter);
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
        Double total_in = 0.00;
        Double total_out = 0.00;
        Double airtime = 0.00;
        Double received = 0.00;
        Double charge =0.00;
        int count = 0;
        dbHome home = new dbHome();
        List<dbMonths> months = new ArrayList<>();


        List<dbSMS> transactions = dboperation.getAllTransactions(value);

        for(dbSMS dbsms : transactions)
        {
            charge = charge + dbsms.getTranCharge();
            if(dbsms.getAmount()>0)
            {
                total_in = total_in + dbsms.getAmount();
            }
            else {
                total_out = total_out + dbsms.getAmount();
            }
//                if (dbsms.getTrantyp().equals("Money Sent")) {
//                    out = out + dbsms.getAmount();
//                    total_out = total_out + dbsms.getAmount();
//                } else if (dbsms.getTrantyp().equals("Merchant Payment")) {
//                    merchant = merchant + dbsms.getAmount();
//                    total_out = total_out + dbsms.getAmount();
//                } else if (dbsms.getTrantyp().equals("Cash Out")) {
//                    cashOut = cashOut + dbsms.getAmount();
//                    total_out = total_out + dbsms.getAmount();
//                } else if (dbsms.getTrantyp().equals("Bill Payment")) {
//                    bill = bill + dbsms.getAmount();
//                    total_out = total_out + dbsms.getAmount();
//                } else if (dbsms.getTrantyp().equals("Airtime")) {
//                    airtime = airtime + dbsms.getAmount();
//                    total_out = total_out + dbsms.getAmount();
//                } else if (dbsms.getTrantyp().equals("Cash In")) {
//                    in = in + dbsms.getAmount();
//                    total_in = total_in + dbsms.getAmount();
//                } else if (dbsms.getTrantyp().equals("Money Received")) {
//                    received = received + dbsms.getAmount();
//                    total_in = total_in + dbsms.getAmount();
//                }
        }
        dbSMS d = transactions.get(0);

        home.setIn(in);
        home.setOut(out*-1);
        home.setBill(bill*-1);
        home.setMerchant(merchant*-1);
        home.setBalance(d.getTranBalance());
        home.setCashOut(cashOut*-1);
        home.setTotalIn(total_in);
        home.setAirtime(airtime);
        home.setTotalOut(total_out*-1);
        home.setReceived(received);
        home.setCharge(charge);
        return home;

    }

    private List[] getSmsLog() {

        List[] amount = new List[2];
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

    private SpannableString generateCenterSpannableText(String text, String text1) {

        if(text.contains("default")) {

            Calendar cal = Calendar.getInstance();
            String month = value;
            Log.e("MON", month);

            SpannableString s = new SpannableString(month + " \nOverview");
            s.setSpan(new RelativeSizeSpan(1.7f), 0, s.length(), 0);
            return s;
        }
        else
        {
            Double amount  = Double.parseDouble(text);

            SpannableString s = new SpannableString("$" + df.format(amount) + " \n" +text1);
            s.setSpan(new RelativeSizeSpan(1.7f), 0, s.length(), 0);
            return s;
        }

    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

        if (e == null)
            return;
        PieEntry pe = (PieEntry) e;
        mChart.setCenterText(generateCenterSpannableText(""+((PieEntry) e).getValue(),""+ ((PieEntry) e).getLabel()));
        Log.i("VAL SELECTED",
                "Value: " + ((PieEntry) e).getValue() + ", Label: " + ((PieEntry) e).getLabel()  + ", index: " + h.getX()
                        + ", DataSet index: " + h.getDataSetIndex());
    }

    @Override
    public void onNothingSelected() {
        mChart.setCenterText(generateCenterSpannableText("default","default"));
        Log.i("PieChart", "nothing selected");
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
