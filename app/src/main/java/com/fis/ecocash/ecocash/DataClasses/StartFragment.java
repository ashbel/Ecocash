package com.fis.ecocash.ecocash.DataClasses;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ashokvarma.bottomnavigation.ShapeBadgeItem;
import com.ashokvarma.bottomnavigation.TextBadgeItem;
import com.fis.ecocash.ecocash.R;
import com.fis.ecocash.ecocash.StartActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StartFragment extends Fragment {
    private ProgressDialog progressBar;
    private static final int MY_PERMISSIONS_REQUEST_READ_SMS = 0;
    int lastSelectedPosition = 0;
    private static StartActivity activity;
    @Nullable
    TextBadgeItem numberBadgeItem;
    private dbOperations dboperation;
    @Nullable
    ShapeBadgeItem shapeBadgeItem;
    ArrayList<dbSMS> sms_db = new ArrayList<dbSMS>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_start, container, false);

        progressBar = new ProgressDialog(getContext());
        progressBar.setCancelable(true);
        progressBar.setMessage("Setting Up Please Wait ...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setProgress(0);
        progressBar.setMax(100);
        progressBar.show();
        saveSMS();
        progressBar.dismiss();

        return view;
    }

    private void saveSMS() {



        Uri myMessage = Uri.parse("content://sms/");
        ContentResolver cr =getActivity().getContentResolver();
        Cursor c = cr.query(myMessage, new String[]{"_id", "address", "date",
                "body", "read"}, "address = '+263164'", null, "date ASC");
        getActivity().startManagingCursor(c);

        if (sms_db.size() > 0) {
            sms_db.clear();
        }
        List[] amount = new List[2];
        Double amountSent = 0.00;
        Double amountRcvd = 0.00;
        Double amountPaid = 0.00;
        Double balance =0.00;
        int count = 0;
        ArrayList<String> dtmthns = new ArrayList<>();
        ArrayList<dbSMS> dbList = new ArrayList<>();


        try {
            if (c.moveToFirst()) {
                do {
                    if (c.getString(c.getColumnIndexOrThrow("address")) == null) {
                        c.moveToNext();
                        continue;
                    }
                    dbSMS dbsms = new dbSMS();
                    Date date_ = new Date();
                    String date="";
                    String datestr ="";
                    count++;
                    String Number = c.getString(c.getColumnIndexOrThrow("address")).toString();
                    String _id = c.getString(c.getColumnIndexOrThrow("_id")).toString();
                    String dat = c.getString(c.getColumnIndexOrThrow("date"));
                    String Body = c.getString(c.getColumnIndexOrThrow("body")).toString();

                    SimpleDateFormat format = new SimpleDateFormat("MMMM yyyy");
                    try {
                        long dateLong = Long.parseLong(dat);
                        date_ = new Date(dateLong);
                        date = dat;
                        datestr = format.format(date_);

                        if(!dtmthns.contains(datestr)) {
                            dtmthns.add(datestr);
                        }
                    } catch (Exception e) {
                    }
                    if (Body.contains("Transfer Confirmation")) {
                        String[] myMessages = Body.split("(?<=[.])\\s+");
                        if (myMessages[1].contains(" to ")) {
                            String[] myAmount = myMessages[1].split("\\s+");
                            double f = Double.parseDouble(myAmount[1]);
                            amountSent = amountSent + f;
                            Pattern pattern = Pattern.compile("to(.*?)Approval");
                            Matcher matcher = pattern.matcher(myMessages[1]);
                            while (matcher.find()) {
                                dbsms.setName(matcher.group(1).trim());
                            }
                            String[] o = myMessages[myMessages.length - 1].split("\\s+");
                            double bal = Double.parseDouble(o[o.length - 1].substring(0, o[o.length - 1].length() - 1).replaceAll("[^\\d.]", ""));

                            dbsms.setTranBalance(bal);
                            dbsms.setTrantyp("Money Sent");
                            dbsms.setTrandate(date);
                            dbsms.setAmount(f * -1);
                            dbsms.setTranMonth(datestr);
                        } else if (myMessages[1].contains(" from ")) {
                            String[] myAmount = myMessages[1].split("\\s+");
                            double f = Double.parseDouble(myAmount[1]);
                            amountRcvd = amountRcvd + f;
                            Pattern pattern = Pattern.compile("from(.*?)Approval");
                            Matcher matcher = pattern.matcher(myMessages[1]);
                            while (matcher.find()) {
                                dbsms.setName(matcher.group(1).trim());
                            }
                            String[] o = myMessages[myMessages.length - 1].split("\\s+");
                            double bal = Double.parseDouble(o[o.length - 1].substring(0, o[o.length - 1].length() - 1).replaceAll("[^\\d.]", ""));
                            dbsms.setTranBalance(bal);
                            dbsms.setTrantyp("Money Received");
                            dbsms.setTrandate(date);
                            dbsms.setAmount(f);
                            dbsms.setTranMonth(datestr);
                        }
                    } else if (Body.contains("successfully paid")) {
                        String[] myMessages = Body.split("(?<=[.])\\s+");
                        String[] myAmount = myMessages[0].split("\\s+");
                        double f = Double.parseDouble(myAmount[4].replaceAll("[^\\d.]", ""));
                        amountPaid = amountPaid + f;
                        String name = "";
                        Pattern pattern = Pattern.compile("to(.*?)Merchant");
                        Matcher matcher = pattern.matcher(myMessages[0]);
                        while (matcher.find()) {
                            name = matcher.group(1);
                        }
                        if (name.isEmpty()) {
                            Pattern pattern_ = Pattern.compile("to(.*?),");
                            Matcher matcher_ = pattern_.matcher(myMessages[0]);
                            while (matcher_.find()) {
                                name = matcher_.group(1);
                            }
                        }
                        String[] o = myMessages[myMessages.length - 1].split("\\s+");
                        double bal = Double.parseDouble(o[o.length - 1].substring(0, o[o.length - 1].length() - 1).replaceAll("[^\\d.]", ""));
                        dbsms.setName(name.trim());
                        dbsms.setTranBalance(bal);
                        dbsms.setTrantyp("Merchant Payment");
                        dbsms.setTrandate(date);
                        dbsms.setAmount(f * -1);
                        dbsms.setTranMonth(datestr);
                    } else if (Body.contains("Cash In Confirmation")) {
                        String[] myMessages = Body.split("\\s+");
                        //String[] myAmount = myMessages[4];
                        double f = Double.parseDouble(myMessages[4].replaceAll("[^\\d.]", ""));
                        amountSent = amountSent + f;
                        Pattern pattern = Pattern.compile("from(.*?)Approval");
                        Matcher matcher = pattern.matcher(Body);
                        while (matcher.find()) {
                            dbsms.setName(matcher.group(1).trim());
                        }
                        String[] o = myMessages[myMessages.length - 1].split("\\s+");
                        double bal = Double.parseDouble(o[o.length - 1].substring(0, o[o.length - 1].length() - 1).replaceAll("[^\\d.]", ""));

                        dbsms.setTranBalance(bal);
                        dbsms.setTrantyp("Cash In");
                        dbsms.setTrandate(date);
                        dbsms.setAmount(f);
                        dbsms.setTranMonth(datestr);
                    } else if (Body.contains("CashOut Confirmation")) {
                        String[] myMessages = Body.split("\\s+");
                        //Log.e("TAG",  myMessages[2]);
                        double f = Double.parseDouble(myMessages[2].replaceAll("[^\\d.]", ""));
                        amountSent = amountSent + f;
                        Pattern pattern = Pattern.compile("from(.*?)Approval");
                        Matcher matcher = pattern.matcher(Body);
                        while (matcher.find()) {
                            dbsms.setName(matcher.group(1).trim());
                            //  Log.e("TAG",  matcher.group(1));
                        }
                        String[] o = myMessages[myMessages.length - 1].split("\\s+");
                        dbsms.setTrantyp("Cash Out");
                        dbsms.setTrandate(date);
                        dbsms.setAmount(f * -1);
                        dbsms.setTranMonth(datestr);
                    } else if (Body.contains("bill payment")) {
                        String[] myMessages = Body.split("(?<=[.])\\s+");
                        String[] myAmount = myMessages[0].split("\\s+");
                        Pattern pattern = Pattern.compile("to(.*?)of");
                        Pattern pattern_amt = Pattern.compile("of(.*?)to");

                        Matcher matcher = pattern.matcher(myMessages[0]);
                        while (matcher.find()) {
                            dbsms.setName(matcher.group(1).trim());
                        }
                        Matcher matcher_amt = pattern_amt.matcher(myMessages[0]);
                        while (matcher_amt.find()) {
                            double f = Double.parseDouble(matcher_amt.group(1).replaceAll("[^\\d.]", ""));
                            amountPaid = amountPaid + f;
                            dbsms.setAmount(f * -1);
                        }
                        String[] o = myMessages[myMessages.length - 1].split("\\s+");
                        double bal = Double.parseDouble(o[o.length - 1].substring(0, o[o.length - 1].length() - 1).replaceAll("[^\\d.]", ""));

                        dbsms.setTranBalance(bal);
                        dbsms.setTrantyp("Bill Payment");
                        dbsms.setTrandate(date);
                        dbsms.setTranMonth(datestr);
                    } else if (Body.contains("Your bank account")) {
                        String[] myMessages = Body.split("(?<=[.])\\s+");
                        //Log.e("BNK",myMessages[0]);
                        Pattern pattern = Pattern.compile("of(.*?)was");
                        Matcher matcher = pattern.matcher(myMessages[0]);
                        while (matcher.find()) {
                            double f = Double.parseDouble(matcher.group(1).replaceAll("[^\\d.]", ""));
                            dbsms.setAmount(f);
                        }

                        dbsms.setName("Bank To Wallet");
                        dbsms.setTrantyp("Cash In");
                        dbsms.setTrandate(date);
                        dbsms.setTranMonth(datestr);
                    } else if (Body.contains("You have bought")) {
                        String[] myMessages = Body.split("(?<=[.])\\s+");
                        //Log.e("BNK",myMessages[0]);
                        Pattern pattern = Pattern.compile("bought(.*?)Airtime");
                        Matcher matcher = pattern.matcher(myMessages[0]);
                        while (matcher.find()) {
                            double f = Double.parseDouble(matcher.group(1).replaceAll("[^\\d.]", ""));
                            dbsms.setAmount(f*-1);
                        }

                        dbsms.setName("Airtime");
                        dbsms.setTrantyp("Airtime");
                        dbsms.setTrandate(date);
                        dbsms.setTranMonth(datestr);
                    }
                    if(Body.contains("bill payment")|| Body.contains("Your bank account") || Body.contains("Cash In Confirmation") || Body.contains("successfully paid") || Body.contains("successfully paid") || Body.contains("Transfer Confirmation")|| Body.contains("CashOut Confirmation")|| Body.contains("You have bought"))
                    {
                        String[] myMessages = Body.split("(?<=[.])\\s+");
                        String[] o = myMessages[myMessages.length - 1].split("\\s+");
                        boolean isDigit = Character.isDigit(o[o.length - 1].charAt(o[o.length - 1].length() - 1));
                        if(!isDigit) {
                            balance = Double.parseDouble(o[o.length - 1].substring(0, o[o.length - 1].length() - 1).replaceAll("[^\\d.]", ""));
                        }
                        else
                        {
                            balance = Double.parseDouble(o[o.length - 1].replaceAll("[^\\d.]", ""));
                        }
                        dbsms.setTranBalance(balance);
                        dbsms.setTranTxt(Body);
                        dbsms.setTrandate(dat);
                        dbsms.setTranMonth(datestr);

                    }
                    if (Body.contains("bill payment") || Body.contains("Cash In Confirmation") || Body.contains("successfully paid") || Body.contains("successfully paid") || Body.contains("Transfer Confirmation") || Body.contains("CashOut Confirmation") || Body.contains("You have bought")) {
                        String trans_id = "";
                        String[] myMessages = Body.split("(?<=[.])\\s+");
                        String[] o = myMessages[myMessages.length - 2].split("\\s+");
                        // Log.e("MSG",myMessages[myMessages.length - 2]);

                        boolean isAP = myMessages[myMessages.length - 2].contains("Approval Code:");
                        boolean isTxn = myMessages[myMessages.length - 2].contains("TxnID");
                        boolean isTxn_Id = myMessages[myMessages.length - 2].contains("Txn ID");

                        if (isAP) {

                            String[] parts = myMessages[myMessages.length - 2].split("Approval Code:");
                            trans_id = parts[1];
                        }
                        if (isTxn) {

                            String[] parts = myMessages[myMessages.length - 2].split("TxnID");
                            trans_id = parts[1];
                        }
                        if (isTxn_Id) {

                            String[] parts = myMessages[myMessages.length - 2].split("Txn ID");
                            trans_id = parts[1];
                        }

                        dbsms.setTranId(trans_id.trim());

                    }
                    if (dbsms.getName() != null) {
                        if(!dboperation.smsCount(dbsms.getTranId())){
                            dboperation.addSMS(dbsms);
                        }

                    }

                } while (c.moveToNext());
            }

        } catch (Exception e) {
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
