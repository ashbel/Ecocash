package com.fis.ecocash.ecocash;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import com.fis.ecocash.ecocash.DataClasses.dbOperations;
import com.fis.ecocash.ecocash.DataClasses.dbSMS;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ashbelh on 12/4/2018.
 */

public class SMSReceiver extends BroadcastReceiver {
    // SmsManager class is responsible for all SMS related actions
    final SmsManager sms = SmsManager.getDefault();
    private dbOperations dboperation;
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e("LIST","Listening");
        dboperation = new dbOperations(context);
        dboperation.open();
        // Get the SMS message received
        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                // A PDU is a "protocol data unit". This is the industrial standard for SMS message
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (int i = 0; i < pdusObj.length; i++) {
                    // This will create an SmsMessage object from the received pdu
                    SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdusObj[i]);

                    if(sms.getDisplayOriginatingAddress().equals("+263164"))
                    // Get sender phone number
                    {
                        //Log.e("LIST","Listening in 164");
                        long date = sms.getTimestampMillis();
                        String message = sms.getMessageBody();

                        //Log.e("MSG",message);
                        saveSMS(date,message);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void saveSMS(long dateLong, String Body) {

        Double amountSent = 0.00;
        Double amountRcvd = 0.00;
        Double amountPaid = 0.00;
        Double balance = 0.00;
        dbSMS dbsms = new dbSMS();
        Date date_ = new Date();
        String date = "";
        String datestr = "";
        ArrayList<String> dtmthns = new ArrayList<>();


        SimpleDateFormat format = new SimpleDateFormat("MMMM yyyy");
        try {
            //long dateLong = Long.parseLong(dat);
            date_ = new Date(dateLong);
            date = String.valueOf(dateLong);
            datestr = format.format(date_);

            if (!dtmthns.contains(datestr)) {
                dtmthns.add(datestr);
            }
        } catch (Exception e) {
        }
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
            dbsms.setTrandate(date);
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
            dbsms.setTranCharge(0.00);
            if (!dboperation.smsCount(dbsms.getTranId())) {
                dboperation.addSMS(dbsms);
            }

        }
    }
}
