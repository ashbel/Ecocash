package com.fis.ecocash.ecocash;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.ashokvarma.bottomnavigation.ShapeBadgeItem;
import com.ashokvarma.bottomnavigation.TextBadgeItem;
import com.fis.ecocash.ecocash.DataClasses.dbOperations;
import com.fis.ecocash.ecocash.DataClasses.dbSMS;
import com.fis.ecocash.ecocash.ListAdapters.CustomExpandableListAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HomeActivity extends AppCompatActivity {

    public static final String OTP_REGEX = "[0-9]{1,6}";
    ArrayList<dbSMS> sms_db = new ArrayList<dbSMS>();
    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    HashMap<String, List<dbSMS>> expandableListDetail;
    BottomNavigationBar bottomNavigationBar;
    int lastSelectedPosition = 0;
    private dbOperations dboperation;

    @Nullable
    TextBadgeItem numberBadgeItem;

    @Nullable
    ShapeBadgeItem shapeBadgeItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);



        Uri myMessage = Uri.parse("content://sms/");
        ContentResolver cr = this.getContentResolver();
        Cursor c = cr.query(myMessage, new String[]{"_id", "address", "date",
                "body", "read"}, "address = '+263164'", null,"date DESC");
        startManagingCursor(c);
        List[] amount = getSmsLogs(c, HomeActivity.this);


        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        expandableListDetail = getData(amount[1],amount[0]);
        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
        expandableListAdapter = new CustomExpandableListAdapter(this, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
            }
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {

            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                return false;
            }
        });
    }

    private void ReadSMS()
    {

    }


    private List[] getSmsLogs(Cursor c, Context con) {

        if (sms_db.size() > 0) {
            sms_db.clear();
        }
        List[] amount = new List[2];
        Double amountSent = 0.00;
        Double amountRcvd = 0.00;
        Double amountPaid = 0.00;
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
                    String date ="";
                    String datestr ="";
                    count++;
                    String Number = c.getString(c.getColumnIndexOrThrow("address")).toString();
                    String _id = c.getString(c.getColumnIndexOrThrow("_id")).toString();
                    String dat = c.getString(c.getColumnIndexOrThrow("date"));
                    String Body = c.getString(c.getColumnIndexOrThrow("body")).toString();

                    SimpleDateFormat format = new SimpleDateFormat("MMMM-yyyy");
                    try {
                        long dateLong = Long.parseLong(dat);
                        date_ = new Date(dateLong);
                        date =dat;
                        datestr = format.format(date);

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
                                dbsms.setName(matcher.group(1));
                            }
                            String[] o = myMessages[myMessages.length-1].split("\\s+");
                            double bal = Double.parseDouble(o[o.length-1].substring(0, o[o.length-1].length() - 1).replaceAll("[^\\d.]", ""));

                            dbsms.setTranBalance(bal);
                            dbsms.setTrantyp("Money Sent");
                            dbsms.setTrandate(date);
                            dbsms.setAmount(f*-1);
                            dbsms.setTranMonth(datestr);
                        } else if (myMessages[1].contains(" from ")) {
                            String[] myAmount = myMessages[1].split("\\s+");
                            double f = Double.parseDouble(myAmount[1]);
                            amountRcvd = amountRcvd + f;
                            Pattern pattern = Pattern.compile("from(.*?)Approval");
                            Matcher matcher = pattern.matcher(myMessages[1]);
                            while (matcher.find()) {
                                dbsms.setName(matcher.group(1));
                            }
                            String[] o = myMessages[myMessages.length-1].split("\\s+");
                            double bal = Double.parseDouble(o[o.length-1].substring(0, o[o.length-1].length() - 1).replaceAll("[^\\d.]", ""));

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

                        Pattern pattern = Pattern.compile("to(.*?)Merchant");
                        Matcher matcher = pattern.matcher(myMessages[0]);
                        while (matcher.find()) {
                            dbsms.setName(matcher.group(1));
                        }
                        String[] o = myMessages[myMessages.length-1].split("\\s+");
                        double bal = Double.parseDouble(o[o.length-1].substring(0, o[o.length-1].length() - 1).replaceAll("[^\\d.]", ""));
                        //Log.e("TAG", myMessages[0] +" - "+ datestr);
                        dbsms.setTranBalance(bal);
                        dbsms.setTrantyp("Merchant Payment");
                        dbsms.setTrandate(date);
                        dbsms.setAmount(f*-1);
                        dbsms.setTranMonth(datestr);
                    }
                    else if (Body.contains("Cash In Confirmation")) {
                        String[] myMessages = Body.split("\\s+");
                        //String[] myAmount = myMessages[4];
                        double f = Double.parseDouble(myMessages[4].replaceAll("[^\\d.]", ""));
                        amountSent = amountSent + f;
                        Pattern pattern = Pattern.compile("from(.*?)Approval");
                        Matcher matcher = pattern.matcher(Body);
                        while (matcher.find()) {
                            dbsms.setName(matcher.group(1));

                        }
                        String[] o = myMessages[myMessages.length-1].split("\\s+");
                        double bal = Double.parseDouble(o[o.length-1].substring(0, o[o.length-1].length() - 1).replaceAll("[^\\d.]", ""));

                        dbsms.setTranBalance(bal);
                        dbsms.setTrantyp("Cash In");
                        dbsms.setTrandate(date);
                        dbsms.setAmount(f);
                        dbsms.setTranMonth(datestr);
                    }
                    else if (Body.contains("bill payment")) {
                        String[] myMessages = Body.split("(?<=[.])\\s+");
                        String[] myAmount = myMessages[0].split("\\s+");
                        Pattern pattern = Pattern.compile("to(.*?)of");
                        Pattern pattern_amt = Pattern.compile("of(.*?)to");

                        Matcher matcher = pattern.matcher(myMessages[0]);
                        while (matcher.find()) {
                            //Log.e("TAG",  matcher.group(1));
                            dbsms.setName(matcher.group(1));
                        }
                        Matcher matcher_amt = pattern_amt.matcher(myMessages[0]);
                        while (matcher_amt.find()) {
                            double f = Double.parseDouble(matcher_amt.group(1).replaceAll("[^\\d.]", ""));
                            //Log.e("TAG", String.valueOf(f));
                            amountPaid = amountPaid + f;
                            dbsms.setAmount(f*-1);
                        }
                        String[] o = myMessages[myMessages.length-1].split("\\s+");
                        double bal = Double.parseDouble(o[o.length-1].substring(0, o[o.length-1].length() - 1).replaceAll("[^\\d.]", ""));

                        dbsms.setTranBalance(bal);
                        dbsms.setTrantyp("Bill Payment");
                        dbsms.setTrandate(date);
                        dbsms.setTranMonth(datestr);
                    }
                    if(dbsms.getName()!=null) {
                        dbList.add(dbsms);
                    }
                } while (c.moveToNext());
            }
            //c.close();

        } catch (Exception e) {
            Toast.makeText(HomeActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        amount[0] = dbList;
        amount[1] = dtmthns;
        return amount;

    }

    public static HashMap<String, List<dbSMS>> getData(List<String> mylist, List<dbSMS> smsList) {
        HashMap<String, List<dbSMS>> expandableListDetail = new HashMap<String, List<dbSMS>>();
        for (String y: mylist) {
            Double amount=0.0;
            List<dbSMS> cricket = new ArrayList<dbSMS>();
            for (dbSMS h: smsList) {

                if(h.getTranMonth().contentEquals(y)){
                    cricket.add(h);
                    amount = amount+ h.getAmount();
                }
            }
            if(cricket.size()>0) {

                expandableListDetail.put(y, cricket);
            }//Log.e("AMOUNT",y+" - "+ String.valueOf(amount));
        }
        return expandableListDetail;
    }

}
