package com.fis.ecocash.ecocash;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.ashokvarma.bottomnavigation.ShapeBadgeItem;
import com.ashokvarma.bottomnavigation.TextBadgeItem;
import com.fis.ecocash.ecocash.DataClasses.dbOperations;
import com.fis.ecocash.ecocash.DataClasses.dbSMS;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StartActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, BottomNavigationBar.OnTabSelectedListener, AdapterView.OnItemSelectedListener {

    BottomNavigationBar bottomNavigationBar;
    private static final int MY_PERMISSIONS_REQUEST_READ_SMS = 0;
    private static final int MY_PERMISSIONS_READ_EXTERNAL_STORAGE = 0;
    private static final int MY_PERMISSIONS_CALL_PHONE = 0;
    int lastSelectedPosition = 0;
    private static StartActivity activity;
    @Nullable
    TextBadgeItem numberBadgeItem;
    private dbOperations dboperation;
    @Nullable
    ShapeBadgeItem shapeBadgeItem;
    ArrayList<dbSMS> sms_db = new ArrayList<dbSMS>();
    private ProgressDialog progressBar;
    int PERMISSION_ALL = 1;


    public static StartActivity instance() {
        return activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        dboperation = new dbOperations(this);
        dboperation.open();

        bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
        bottomNavigationBar.setTabSelectedListener(this);
        String[] PERMISSIONS = {Manifest.permission.READ_SMS, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE};

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, MY_PERMISSIONS_REQUEST_READ_SMS);
        } else {
            // create a FragmentManager
            FragmentManager fm = getSupportFragmentManager();
            // create a FragmentTransaction to begin the transaction and replace the Fragment
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            // replace the FrameLayout with new Fragment
            if(dboperation.mobileCount()==0)
            {
                //Log.e("CNT", dboperation.mobileCount()+"" );

                saveSMS();
               // progressBar.dismiss();
            }
            else{
                //Log.e("CNT", dboperation.mobileCount()+"" );
                //saveSMS();
            }

            fragmentTransaction.replace(R.id.home_activity_frag_container, new HomeFragment());
            fragmentTransaction.commit(); // save the changes
        }

        refresh();
        //dboperation.close();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_SMS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    FragmentManager fm = getSupportFragmentManager();
                    // create a FragmentTransaction to begin the transaction and replace the Fragment
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    // replace the FrameLayout with new Fragment
                    if(dboperation.mobileCount()<1)
                    {
                        progressBar = new ProgressDialog(this);
                        progressBar.setCancelable(true);
                        progressBar.setMessage("Setting Up Please Wait ...");
                        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressBar.setProgress(0);
                        progressBar.setMax(100);
                        progressBar.show();
                        saveSMS();
                        progressBar.dismiss();
                    }
                    else{
                        saveSMS();
                    }
                    fragmentTransaction.replace(R.id.home_activity_frag_container, new HomeFragment());
                    fragmentTransaction.commit(); // save the changes

                } else {

                    Toast.makeText(this, "The App Requires this Permission and will now Close", Toast.LENGTH_LONG).show();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        this.finishAndRemoveTask();
                    } else {
                        this.finishAffinity();
                    }
                }
                return;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.home_activity_frag_container, new SettingsFragment()).commitAllowingStateLoss();
//                Intent intent1 = new Intent(this,CategoryActivity.class);
//                startActivity(intent1);
                break;
            case R.id.action_backup:
                Intent intent = new Intent(this,BackupActivity.class);
                startActivity(intent);
                break;
            case R.id.action_refresh:
                progressBar = new ProgressDialog(this);
                progressBar.setCancelable(true);
                progressBar.setMessage("Refreshing please wait ...");
                progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressBar.setProgress(0);
                progressBar.setMax(100);
                progressBar.show();
                dboperation.open();
                 saveSMS();
                 Calculate();
                dboperation.close();
                progressBar.dismiss();
                 Toast.makeText(this,"Transaction Import Complete",Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        refresh();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        refresh();
    }

    public void refresh() {

        bottomNavigationBar.clearAll();
//        bottomNavigationBar.setFab(fabHome, BottomNavigationBar.FAB_BEHAVIOUR_TRANSLATE_AND_STICK);
        //bottomNavigationBar.setFab(fabHome);

        //setScrollableText(lastSelectedPosition);

        numberBadgeItem = new TextBadgeItem()
                .setBorderWidth(4)
                .setBackgroundColorResource(R.color.seafoam)
                .setText("" + lastSelectedPosition)
                .setHideOnSelect(false);

        shapeBadgeItem = new ShapeBadgeItem()
                .setShape(0)
                .setShapeColorResource(R.color.pink)
                .setGravity(Gravity.TOP | Gravity.END)
                .setHideOnSelect(false);

        bottomNavigationBar.setMode(1);
        bottomNavigationBar.setBackgroundStyle(2);

        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.ic_home_white_24dp, "Home").setActiveColorResource(R.color.colorPrimaryDark))
                .addItem(new BottomNavigationItem(R.drawable.ic_list_white_24dp, "Transactions").setActiveColorResource(R.color.colorPrimaryDark))
                .addItem(new BottomNavigationItem(R.drawable.ic_account_box_white_24dp, "Accounts").setActiveColorResource(R.color.colorPrimaryDark))
                .addItem(new BottomNavigationItem(R.drawable.ic_assessment_white_24dp, "Reports").setActiveColorResource(R.color.colorPrimaryDark))
                .addItem(new BottomNavigationItem(R.drawable.ic_payment_white_24dp, "Payments").setActiveColorResource(R.color.colorPrimaryDark))
                .setFirstSelectedPosition(lastSelectedPosition)
                .initialise();
    }

    @Override
    public void onTabSelected(int position) {
        lastSelectedPosition = position;
        setMessageText(position + " Tab Selected");
        if (numberBadgeItem != null) {
            numberBadgeItem.setText(Integer.toString(position));
        }
        setScrollableText(position);
    }

    @Override
    public void onTabUnselected(int position) {
    }

    @Override
    public void onTabReselected(int position) {
        setMessageText(position + " Tab Reselected");
    }

    private void setMessageText(String messageText) {
        //message.setText(messageText);
    }

    private void setScrollableText(int position) {
        switch (position) {
            case 0:
                getSupportFragmentManager().beginTransaction().replace(R.id.home_activity_frag_container, new HomeFragment()).commitAllowingStateLoss();
                break;
            case 1:
                getSupportFragmentManager().beginTransaction().replace(R.id.home_activity_frag_container, new MonthsFragment()).commitAllowingStateLoss();
                break;
            case 2:
                getSupportFragmentManager().beginTransaction().replace(R.id.home_activity_frag_container, new AccountFragment()).commitAllowingStateLoss();
                break;
            case 3:
                getSupportFragmentManager().beginTransaction().replace(R.id.home_activity_frag_container, new ReportsFragment()).commitAllowingStateLoss();
                break;
            case 4:
                getSupportFragmentManager().beginTransaction().replace(R.id.home_activity_frag_container, new PaymentFragment()).commitAllowingStateLoss();
                break;
            default:
                getSupportFragmentManager().beginTransaction().replace(R.id.home_activity_frag_container, new SettingsFragment()).commitAllowingStateLoss();
                break;
        }
    }

    private void saveSMS() {


        Uri myMessage = Uri.parse("content://sms/");
        ContentResolver cr = this.getContentResolver();
        Cursor c = cr.query(myMessage, new String[]{"_id", "address", "date",
                "body", "read"}, "address = '+263164'", null, "date ASC");
        startManagingCursor(c);

        if (sms_db.size() > 0) {
            sms_db.clear();
        }
        List[] amount = new List[2];
        Double amountSent = 0.00;
        Double amountRcvd = 0.00;
        Double amountPaid = 0.00;
        Double balance =0.00;
        Double pre_balance =0.00;
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
                    pre_balance = balance;
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
                   // Log.e("BD", Body );
                    if (Body.contains("Transfer Confirmation")) {
                        String[] myMessages = Body.split("(?<=[.])\\s+");
                        if (myMessages[1].contains(" to ")) {
                            double f =0.00;
                            double bal = 0.00;
                            String[] myAmount = myMessages[1].split("\\s+");
                             f = Double.parseDouble(myAmount[1]);
                            amountSent = amountSent + f;
                            Pattern pattern = Pattern.compile("to(.*?)Approval");
                            Matcher matcher = pattern.matcher(myMessages[1]);
                            while (matcher.find()) {
                                dbsms.setName(matcher.group(1).trim());
                            }
                            String[] o = myMessages[myMessages.length - 1].split("\\s+");
                             bal = Double.parseDouble(o[o.length - 1].substring(0, o[o.length - 1].length() - 1).replaceAll("[^\\d.]", ""));

                            dbsms.setTranBalance(bal);
                            dbsms.setTrantyp("Money Sent");
                            dbsms.setTrandate(date);
                            dbsms.setAmount(f * -1);
                            dbsms.setTranMonth(datestr);
                        } else if (myMessages[1].contains(" from ")) {
                            double f =0.00;
                            double bal = 0.00;
                            String[] myAmount = myMessages[1].split("\\s+");
                             f = Double.parseDouble(myAmount[1]);
                            amountRcvd = amountRcvd + f;
                            Pattern pattern = Pattern.compile("from(.*?)Approval");
                            Matcher matcher = pattern.matcher(myMessages[1]);
                            while (matcher.find()) {
                                dbsms.setName(matcher.group(1).trim());
                            }
                            String[] o = myMessages[myMessages.length - 1].split("\\s+");
                             bal = Double.parseDouble(o[o.length - 1].substring(0, o[o.length - 1].length() - 1).replaceAll("[^\\d.]", ""));
                            dbsms.setTranBalance(bal);
                            dbsms.setTrantyp("Money Received");
                            dbsms.setTrandate(date);
                            dbsms.setAmount(f);
                            dbsms.setTranMonth(datestr);
                        }
                    } else if (Body.contains("successfully paid")) {
                        double f =0.00;
                        double bal = 0.00;
                        String[] myMessages = Body.split("(?<=[.])\\s+");
                        String[] myAmount = myMessages[0].split("\\s+");
                         f = Double.parseDouble(myAmount[4].replaceAll("[^\\d.]", ""));
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
                         bal = Double.parseDouble(o[o.length - 1].substring(0, o[o.length - 1].length() - 1).replaceAll("[^\\d.]", ""));
                        dbsms.setName(name.trim());
                        dbsms.setTranBalance(bal);
                        dbsms.setTrantyp("Merchant Payment");
                        dbsms.setTrandate(date);
                        dbsms.setAmount(f * -1);
                        dbsms.setTranMonth(datestr);
                    } else if (Body.contains("Cash In Confirmation")) {
                        double f =0.00;
                        double bal = 0.00;
                        String[] myMessages = Body.split("\\s+");
                        //String[] myAmount = myMessages[4];
                         f = Double.parseDouble(myMessages[4].replaceAll("[^\\d.]", ""));
                        amountSent = amountSent + f;
                        Pattern pattern = Pattern.compile("from(.*?)Approval");
                        Matcher matcher = pattern.matcher(Body);
                        while (matcher.find()) {
                            dbsms.setName(matcher.group(1).trim());
                        }
                        String[] o = myMessages[myMessages.length - 1].split("\\s+");
                         bal = Double.parseDouble(o[o.length - 1].substring(0, o[o.length - 1].length() - 1).replaceAll("[^\\d.]", ""));

                        dbsms.setTranBalance(bal);
                        dbsms.setTrantyp("Cash In");
                        dbsms.setTrandate(date);
                        dbsms.setAmount(f);
                        dbsms.setTranMonth(datestr);
                    } else if (Body.contains("CashOut Confirmation")) {
                        double f =0.00;
                        double bal = 0.00;
                        String[] myMessages = Body.split("\\s+");
                        //Log.e("TAG",  myMessages[2]);
                         f = Double.parseDouble(myMessages[2].replaceAll("[^\\d.]", ""));
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
                        double f =0.00;
                        double bal = 0.00;
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
                             f = Double.parseDouble(matcher_amt.group(1).replaceAll("[^\\d.]", ""));
                            amountPaid = amountPaid + f;
                            dbsms.setAmount(f * -1);
                        }
                        String[] o = myMessages[myMessages.length - 1].split("\\s+");
                         bal = Double.parseDouble(o[o.length - 1].substring(0, o[o.length - 1].length() - 1).replaceAll("[^\\d.]", ""));

                        dbsms.setTranBalance(bal);
                        dbsms.setTrantyp("Bill Payment");
                        dbsms.setTrandate(date);
                        dbsms.setTranMonth(datestr);
                    } else if (Body.contains("Your bank account")) {
                        double f =0.00;
                        double bal = 0.00;
                        String[] myMessages = Body.split("(?<=[.])\\s+");
                        //Log.e("BNK",myMessages[0]);
                        Pattern pattern = Pattern.compile("of(.*?)was");
                        Matcher matcher = pattern.matcher(myMessages[0]);
                        while (matcher.find()) {
                             f = Double.parseDouble(matcher.group(1).replaceAll("[^\\d.]", ""));
                            dbsms.setAmount(f);
                        }

                        dbsms.setName("Bank To Wallet");
                        dbsms.setTrantyp("Bank To Wallet");
                        dbsms.setTrandate(date);
                        dbsms.setTranMonth(datestr);
                    }
                    else if (Body.contains("Your wallet to bank")) {
                        double f =0.00;
                        double bal = 0.00;
                        String[] myMessages = Body.split("(?<=[.])\\s+");
                        //Log.e("BNK",myMessages[0]);
                        Pattern pattern = Pattern.compile("of(.*?)was");
                        Matcher matcher = pattern.matcher(myMessages[0]);
                        while (matcher.find()) {
                            f = Double.parseDouble(matcher.group(1).replaceAll("[^\\d.]", ""));
                            dbsms.setAmount(f*-1);
                        }

                        dbsms.setName("Wallet To Bank");
                        dbsms.setTrantyp("Wallet To Bank");
                        dbsms.setTrandate(date);
                        dbsms.setTranMonth(datestr);
                    }
                    else if (Body.contains("EcoCash: Club Deposit Confirmation:")) {
                        double f =0.00;
                        double bal = 0.00;
                        String[] myMessages = Body.split("(?<=[.])\\s+");
                        //Log.e("BNK",myMessages[0]);
                        String[] myAmount = myMessages[0].split("\\s+");

                       // Log.e("BNK",myAmount[10]);
                        f = Double.parseDouble(myAmount[5].replaceAll("[^\\d.]", ""));
                       // Log.e("BNK",f+"");
                        dbsms.setAmount(f*-1);
                        dbsms.setTranId(date);
                        dbsms.setName(myAmount[10]);
                        dbsms.setTrantyp("Club Deposit");
                        dbsms.setTrandate(date);
                        dbsms.setTranMonth(datestr);
                    }
                    else if (Body.contains("You have bought")) {
                        String[] myMessages = Body.split("(?<=[.])\\s+");
                        double f =0.00;
                        if(myMessages[0].contains("Airtime")){
                            Pattern pattern = Pattern.compile("bought(.*?)Airtime");
                            Matcher matcher = pattern.matcher(myMessages[0]);
                            while (matcher.find()) {
                                f = Double.parseDouble(matcher.group(1).replaceAll("[^\\d.]", ""));
                            }
                            dbsms.setName("Airtime");
                            dbsms.setTrantyp("Airtime");
                        }
                        else if(myMessages[0].contains("Whatsapp")) {
                            Pattern pattern1 = Pattern.compile("bought(.*?)Whatsapp");
                            Matcher matcher1 = pattern1.matcher(myMessages[0]);
                            while (matcher1.find()) {
                                f = Double.parseDouble(matcher1.group(1).replaceAll("[^\\d.]", ""));
                            }
                            dbsms.setName("Whatsapp Bundles");
                            dbsms.setTrantyp("Airtime");
                        }
                        else if(myMessages[0].contains("Broadband")) {
                            Pattern pattern2 = Pattern.compile("bought(.*?)Broadband");
                            Matcher matcher2 = pattern2.matcher(myMessages[0]);
                            while (matcher2.find()) {
                                f = Double.parseDouble(matcher2.group(1).replaceAll("[^\\d.]", ""));
                            }
                            dbsms.setName("Data Bundles");
                            dbsms.setTrantyp("Airtime");
                        }
                        dbsms.setAmount(f*-1);
                        dbsms.setTrandate(date);
                        dbsms.setTranId(date);
                        dbsms.setTranMonth(datestr);
                    }
                    if(Body.contains("EcoCash: Club Deposit Confirmation:") || Body.contains("Your wallet to bank") || Body.contains("bill payment")|| Body.contains("Your bank account") || Body.contains("Cash In Confirmation") || Body.contains("successfully paid") || Body.contains("successfully paid") || Body.contains("Transfer Confirmation")|| Body.contains("CashOut Confirmation")|| Body.contains("You have bought"))
                    {
                        String[] myMessages = Body.split("(?<=[.])\\s+");
                        String[] o = myMessages[myMessages.length - 1].split("\\s+");
                       // Log.e("TG",myMessages[myMessages.length - 1]);
                        boolean isDigit = Character.isDigit(o[o.length - 1].charAt(o[o.length - 1].length() - 1));
                        try {
                            if (!isDigit) {
                                balance = Double.parseDouble(o[o.length - 1].substring(0, o[o.length - 1].length() - 1).replaceAll("[^\\d.]", ""));
                            } else {
                                balance = Double.parseDouble(o[o.length - 1].replaceAll("[^\\d.]", ""));
                            }
                        }catch (Exception e){
                           // Log.e("ERR",e.getMessage());
                                balance = 0.0;
                        }
                        //Log.e("BAL",balance+"");
                        dbsms.setTranBalance(balance);
                        dbsms.setTranTxt(Body);
                        dbsms.setTrandate(dat);
                        dbsms.setTranMonth(datestr);

                    }
                    if (Body.contains("Your wallet to bank") || Body.contains("Your bank account") || Body.contains("bill payment") || Body.contains("Cash In Confirmation") || Body.contains("successfully paid") || Body.contains("successfully paid") || Body.contains("Transfer Confirmation") || Body.contains("CashOut Confirmation") || Body.contains("You have bought")) {
                        String trans_id = "";
                        String[] myMessages = Body.split("(?<=[.])\\s+");
                        String[] o = myMessages[myMessages.length - 2].split("\\s+");
                        //Log.e("MSG",myMessages[myMessages.length - 2]);

                        boolean isAP = myMessages[myMessages.length - 2].contains("Approval Code:");
                        boolean isTxn = myMessages[myMessages.length - 2].contains("TxnID");
                        boolean isTxn_Id = myMessages[myMessages.length - 2].contains("Txn ID");
                        boolean isTransaction = myMessages[myMessages.length-2].contains("Transaction ID:");
                        boolean isTX_ID = myMessages[myMessages.length-2].contains("TX ID");

                        if (isAP) {

                            String[] parts = myMessages[myMessages.length - 2].split("Approval Code:");
                            trans_id = parts[1];
                            dbsms.setTranId(trans_id.trim());
                        }
                        if (isTxn) {

                            String[] parts = myMessages[myMessages.length - 2].split("TxnID");
                            trans_id = parts[1];
                            dbsms.setTranId(trans_id.trim());
                        }
                        if (isTxn_Id) {

                            String[] parts = myMessages[myMessages.length - 2].split("Txn ID");
                            trans_id = parts[1];
                            dbsms.setTranId(trans_id.trim());
                        }
                        if (isTransaction) {

                            String[] parts = myMessages[myMessages.length - 2].split("Transaction ID:");
                            trans_id = parts[1];
                            dbsms.setTranId(trans_id.trim());
                        }
                        if (isTX_ID) {
                            String[] parts = myMessages[myMessages.length - 2].split("TX ID");
                            trans_id = parts[1];
                            dbsms.setTranId(trans_id.trim());
                        }

                    }



                    if (dbsms.getName() != null) {
                        if(dbsms.getAmount() <0)
                        {
                            dbsms.setTranCharge((pre_balance+dbsms.getAmount())-balance);
                        }
                        else {
                            dbsms.setTranCharge(0.00);
                        }
                        if(!dboperation.smsCount(dbsms.getTranId())){
                            dboperation.addSMS(dbsms);
                        }
                    }

                } while (c.moveToNext());
            }

        } catch (Exception e) {
            //Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
          // Log.e("Error", e.toString() );
        }
    }

    private void Calculate(){

        List<dbSMS> transactions = dboperation.getAllTransactionsAsc();
        Double balance = 0.00;
        Double pre_balance = 0.00;

        for(dbSMS dbsms : transactions)
        {
                if(dbsms.getAmount() <0)
                {
                    //Log.e("DBMS", "PreBalance - "+pre_balance+" AMT "+dbsms.getAmount() + " Balance "+dbsms.getTranBalance() + " Charge "+(pre_balance+dbsms.getAmount()-dbsms.getTranBalance())+"");
                    dbsms.setTranCharge((pre_balance+dbsms.getAmount())-dbsms.getTranBalance());
                }
               // Log.e("DBMS", dbsms.toString());

                dboperation.updateSMS(dbsms);
                pre_balance = dbsms.getTranBalance();
        }

    }

}





