package com.fis.ecocash.ecocash.DataClasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ashbelh on 5/4/2018.
 */

public class dbOperations {
    public static final String LOGTAG = "UNTU_MOBI_SYS";

    SQLiteOpenHelper dbhandler;
    SQLiteDatabase database;

    private static final String[] allColumns = {
            dbDatabaseHelper.KEY_ID,
            dbDatabaseHelper.COLUMN_FIRST_NAME,
            dbDatabaseHelper.COLUMN_LAST_NAME,
            dbDatabaseHelper.COLUMN_GENDER,
            dbDatabaseHelper.COLUMN_PH_NUM,
            dbDatabaseHelper.COLUMN_ID_NUM,
            dbDatabaseHelper.COLUMN_PSWD,
            dbDatabaseHelper.COLUMN_APP_KEY,
            dbDatabaseHelper.COLUMN_STATUS,
            dbDatabaseHelper.COLUMN_MAMBU_ID,
            dbDatabaseHelper.KEY_DATE

    };

    private static final String[] smsColumns = {
            dbDatabaseHelper.KEY_ID,
            dbDatabaseHelper.KEY_NAME,
            dbDatabaseHelper.KEY_AMOUNT,
            dbDatabaseHelper.KEY_CHARGE,
            dbDatabaseHelper.KEY_TYPE,
            dbDatabaseHelper.KEY_DATE,
            dbDatabaseHelper.KEY_BALANCE,
            dbDatabaseHelper.KEY_TRANS_ID,
            dbDatabaseHelper.KEY_TEXT,
            dbDatabaseHelper.KEY_MONTH
    };

    private static final String[] backupColumns = {
            dbDatabaseHelper.KEY_ID,
            dbDatabaseHelper.KEY_GOOGLE,
            dbDatabaseHelper.KEY_DEVICE,
            dbDatabaseHelper.KEY_BACK_DATE
    };

    private static final String[] categoryColumns = {
            dbDatabaseHelper.KEY_ID,
            dbDatabaseHelper.KEY_CATEGORY
    };

    public dbOperations(Context context) {
        dbhandler = new dbDatabaseHelper(context);
    }

    public void open() {
        database = dbhandler.getWritableDatabase();
    }

    public void close() {
        dbhandler.close();

    }


//------------------------MOBILE MONEY----------------------------------------------------//

    // Add Transactions
    public dbSMS addSMS(dbSMS transactions) {
        ContentValues values = new ContentValues();
        values.put(dbDatabaseHelper.KEY_AMOUNT,transactions.getAmount());
        values.put(dbDatabaseHelper.KEY_CHARGE,transactions.getTranCharge());
        values.put(dbDatabaseHelper.KEY_BALANCE,transactions.getTranBalance());
        values.put(dbDatabaseHelper.KEY_TYPE,transactions.getTrantyp());
        values.put(dbDatabaseHelper.KEY_TRANS_ID,transactions.getTranId());
        values.put(dbDatabaseHelper.KEY_DATE, String.valueOf(transactions.getTrandate()));
        values.put(dbDatabaseHelper.KEY_NAME,transactions.getName());
        values.put(dbDatabaseHelper.KEY_TEXT,transactions.getTranTxt());
        values.put(dbDatabaseHelper.KEY_MONTH,transactions.getTranMonth());
        long insertid = database.insert(dbDatabaseHelper.TABLE_MOBILE_MONEY, null, values);
        transactions.setId(insertid);
      // Log.e("INDB",transactions.toString());
        return transactions;
    }

    public int updateSMS(dbSMS dbsms) {
        ContentValues values = new ContentValues();
        values.put(dbDatabaseHelper.KEY_CHARGE,dbsms.getTranCharge());
        // updating row
        return database.update(dbDatabaseHelper.TABLE_MOBILE_MONEY, values,
                dbDatabaseHelper.KEY_ID + "=?", new String[]{String.valueOf(dbsms.getId())});
    }

    // Get Transactions
    public List<dbSMS> getAllTransactions(String account) {

        Cursor cursor = database.query(dbDatabaseHelper.TABLE_MOBILE_MONEY, smsColumns, dbDatabaseHelper.KEY_MONTH + "=?" , new String[]{account}, null, null,dbDatabaseHelper.KEY_DATE+ " DESC",null);

        List<dbSMS> transactions = new ArrayList<>();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                dbSMS transaction =  new dbSMS();
                transaction.setId(cursor.getLong(cursor.getColumnIndex(dbDatabaseHelper.KEY_ID)));
                transaction.setTranBalance(cursor.getDouble(cursor.getColumnIndex(dbDatabaseHelper.KEY_BALANCE)));
                transaction.setTranCharge(cursor.getDouble(cursor.getColumnIndex(dbDatabaseHelper.KEY_CHARGE)));
                transaction.setAmount(cursor.getDouble(cursor.getColumnIndex(dbDatabaseHelper.KEY_AMOUNT)));
                transaction.setName(cursor.getString(cursor.getColumnIndex(dbDatabaseHelper.KEY_NAME)));
                transaction.setTrandate(cursor.getString(cursor.getColumnIndex(dbDatabaseHelper.KEY_DATE)));;
                transaction.setTranId(cursor.getString(cursor.getColumnIndex(dbDatabaseHelper.KEY_TRANS_ID)));
                transaction.setTrantyp(cursor.getString(cursor.getColumnIndex(dbDatabaseHelper.KEY_TYPE)));
                transaction.setTranTxt(cursor.getString(cursor.getColumnIndex(dbDatabaseHelper.KEY_TEXT)));
                transaction.setTranMonth(cursor.getString(cursor.getColumnIndex(dbDatabaseHelper.KEY_MONTH)));
                transactions.add(transaction);
            }
        }

        return transactions;
    }

    public List<dbSMS> getAllTransactions(String sdate, String edate) {
       //Log.e("RPT","Start - "+sdate+ "End - "+edate);
        Cursor cursor = database.query(dbDatabaseHelper.TABLE_MOBILE_MONEY, smsColumns, dbDatabaseHelper.KEY_DATE + ">=?" + " AND "+ dbDatabaseHelper.KEY_DATE + "<=?" , new String[]{sdate,edate}, null, null,dbDatabaseHelper.KEY_DATE+ " DESC",null);
        //Log.e("RPT","Cursor Cnt - "+cursor.getCount()+ "");
        List<dbSMS> transactions = new ArrayList<>();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                dbSMS transaction =  new dbSMS();
                transaction.setId(cursor.getLong(cursor.getColumnIndex(dbDatabaseHelper.KEY_ID)));
                transaction.setTranBalance(cursor.getDouble(cursor.getColumnIndex(dbDatabaseHelper.KEY_BALANCE)));
                transaction.setTranCharge(cursor.getDouble(cursor.getColumnIndex(dbDatabaseHelper.KEY_CHARGE)));
                transaction.setAmount(cursor.getDouble(cursor.getColumnIndex(dbDatabaseHelper.KEY_AMOUNT)));
                transaction.setName(cursor.getString(cursor.getColumnIndex(dbDatabaseHelper.KEY_NAME)));
                transaction.setTrandate(cursor.getString(cursor.getColumnIndex(dbDatabaseHelper.KEY_DATE)));;
                transaction.setTranId(cursor.getString(cursor.getColumnIndex(dbDatabaseHelper.KEY_TRANS_ID)));
                transaction.setTrantyp(cursor.getString(cursor.getColumnIndex(dbDatabaseHelper.KEY_TYPE)));
                transaction.setTranTxt(cursor.getString(cursor.getColumnIndex(dbDatabaseHelper.KEY_TEXT)));
                transaction.setTranMonth(cursor.getString(cursor.getColumnIndex(dbDatabaseHelper.KEY_MONTH)));
                transactions.add(transaction);
            }
        }

        return transactions;
    }

    public List<dbSMS> getAllTransactions() {

        Cursor cursor = database.query(dbDatabaseHelper.TABLE_MOBILE_MONEY, smsColumns, null , null, null, null,dbDatabaseHelper.KEY_DATE+ " DESC",null);

        List<dbSMS> transactions = new ArrayList<>();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                dbSMS transaction =  new dbSMS();
                transaction.setId(cursor.getLong(cursor.getColumnIndex(dbDatabaseHelper.KEY_ID)));
                transaction.setTranBalance(cursor.getDouble(cursor.getColumnIndex(dbDatabaseHelper.KEY_BALANCE)));
                transaction.setTranCharge(cursor.getDouble(cursor.getColumnIndex(dbDatabaseHelper.KEY_CHARGE)));
                transaction.setAmount(cursor.getDouble(cursor.getColumnIndex(dbDatabaseHelper.KEY_AMOUNT)));
                transaction.setName(cursor.getString(cursor.getColumnIndex(dbDatabaseHelper.KEY_NAME)));
                transaction.setTrandate(cursor.getString(cursor.getColumnIndex(dbDatabaseHelper.KEY_DATE)));;
                transaction.setTranId(cursor.getString(cursor.getColumnIndex(dbDatabaseHelper.KEY_TRANS_ID)));
                transaction.setTrantyp(cursor.getString(cursor.getColumnIndex(dbDatabaseHelper.KEY_TYPE)));
                transaction.setTranTxt(cursor.getString(cursor.getColumnIndex(dbDatabaseHelper.KEY_TEXT)));
                transaction.setTranMonth(cursor.getString(cursor.getColumnIndex(dbDatabaseHelper.KEY_MONTH)));
                transactions.add(transaction);
            }
        }

        return transactions;
    }

    public List<dbSMS> getAllTransactionsAsc() {

        Cursor cursor = database.query(dbDatabaseHelper.TABLE_MOBILE_MONEY, smsColumns, null , null, null, null,dbDatabaseHelper.KEY_DATE+ " ASC",null);

        List<dbSMS> transactions = new ArrayList<>();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                dbSMS transaction =  new dbSMS();
                transaction.setId(cursor.getLong(cursor.getColumnIndex(dbDatabaseHelper.KEY_ID)));
                transaction.setTranBalance(cursor.getDouble(cursor.getColumnIndex(dbDatabaseHelper.KEY_BALANCE)));
                transaction.setTranCharge(cursor.getDouble(cursor.getColumnIndex(dbDatabaseHelper.KEY_CHARGE)));
                transaction.setAmount(cursor.getDouble(cursor.getColumnIndex(dbDatabaseHelper.KEY_AMOUNT)));
                transaction.setName(cursor.getString(cursor.getColumnIndex(dbDatabaseHelper.KEY_NAME)));
                transaction.setTrandate(cursor.getString(cursor.getColumnIndex(dbDatabaseHelper.KEY_DATE)));;
                transaction.setTranId(cursor.getString(cursor.getColumnIndex(dbDatabaseHelper.KEY_TRANS_ID)));
                transaction.setTrantyp(cursor.getString(cursor.getColumnIndex(dbDatabaseHelper.KEY_TYPE)));
                transaction.setTranTxt(cursor.getString(cursor.getColumnIndex(dbDatabaseHelper.KEY_TEXT)));
                transaction.setTranMonth(cursor.getString(cursor.getColumnIndex(dbDatabaseHelper.KEY_MONTH)));
                transactions.add(transaction);
            }
        }

        return transactions;
    }

    public int mobileCount() {
        Cursor cursor = database.query(dbDatabaseHelper.TABLE_MOBILE_MONEY, smsColumns, null, null, null, null, null);
        return cursor.getCount();
    }

    public boolean smsCount(String account) {
        //Log.e("TX", account);
        Cursor cursor = database.query(dbDatabaseHelper.TABLE_MOBILE_MONEY, smsColumns,dbDatabaseHelper.KEY_TRANS_ID + " = ?" , new String[]{account}, null, null, null);
        int i = cursor.getCount();
       //Log.e("CNT", String.valueOf(i));
        if (i > 0) {
            return true;
        }
        return false;
    }



    //------------------------BACKUP TABLE------------------------------------------------//

    public int backCount() {
        Cursor cursor = database.query(dbDatabaseHelper.TABLE_BACKUP, backupColumns, null, null, null, null, null);
        return cursor.getCount();
    }

    public dbBackup addBackup(dbBackup backup) {
        ContentValues values = new ContentValues();
        values.put(dbDatabaseHelper.KEY_GOOGLE,backup.getGoogle());
        values.put(dbDatabaseHelper.KEY_DEVICE,backup.getDevice());
        values.put(dbDatabaseHelper.KEY_BACK_DATE, String.valueOf(backup.getBackdate()));
        long insertid = database.insert(dbDatabaseHelper.TABLE_BACKUP, null, values);
        backup.setId(insertid);
        return backup;
    }

    // Updating dbDbUsers
    public int updateBackup(dbBackup backup) {
        ContentValues values = new ContentValues();
        values.put(dbDatabaseHelper.KEY_GOOGLE,backup.getGoogle());
        values.put(dbDatabaseHelper.KEY_DEVICE,backup.getDevice());
        values.put(dbDatabaseHelper.KEY_BACK_DATE, String.valueOf(backup.getBackdate()));

        // updating row
        return database.update(dbDatabaseHelper.TABLE_BACKUP, values,
                dbDatabaseHelper.KEY_ID + "=?", new String[]{String.valueOf(backup.getId())});
    }

    public dbBackup getBack() {
        Cursor cursor = database.query(dbDatabaseHelper.TABLE_BACKUP, backupColumns, null , null, null, null,null,null);
        dbBackup transaction =  new dbBackup();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                transaction.setId(cursor.getLong(cursor.getColumnIndex(dbDatabaseHelper.KEY_ID)));
                transaction.setGoogle(cursor.getInt(cursor.getColumnIndex(dbDatabaseHelper.KEY_GOOGLE)));
                transaction.setDevice(cursor.getInt(cursor.getColumnIndex(dbDatabaseHelper.KEY_DEVICE)));
                transaction.setBackdate(cursor.getString(cursor.getColumnIndex(dbDatabaseHelper.KEY_BACK_DATE)));
            }
        }
        return transaction;
    }

    //------------------------CATEGORY TABLE------------------------------------------------//

    public int catCount() {
        Cursor cursor = database.query(dbDatabaseHelper.TABLE_CATEGORY, categoryColumns, null, null, null, null, null);
        return cursor.getCount();
    }

    public dbCategory addCat(dbCategory category) {
        ContentValues values = new ContentValues();
        values.put(dbDatabaseHelper.KEY_CATEGORY,category.getCategory());
        long insertid = database.insert(dbDatabaseHelper.TABLE_CATEGORY, null, values);
        category.setId(insertid);
        return category;
    }

    public int updateCat(dbCategory category) {
        ContentValues values = new ContentValues();
        values.put(dbDatabaseHelper.KEY_CATEGORY,category.getCategory());
        // updating row
        return database.update(dbDatabaseHelper.TABLE_CATEGORY, values,
                dbDatabaseHelper.KEY_ID + "=?", new String[]{String.valueOf(category.getId())});
    }

    public List<dbCategory> getCat() {
        Cursor cursor = database.query(dbDatabaseHelper.TABLE_CATEGORY, categoryColumns, null , null, null, null,null,null);
        List<dbCategory> categories = new ArrayList<>();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                dbCategory category =  new dbCategory();
                category.setId(cursor.getLong(cursor.getColumnIndex(dbDatabaseHelper.KEY_ID)));
                category.setCategory(cursor.getString(cursor.getColumnIndex(dbDatabaseHelper.KEY_CATEGORY)));
                categories.add(category);
            }
        }
        return categories;
    }

    public List<String> getCatString() {
        Cursor cursor = database.query(dbDatabaseHelper.TABLE_CATEGORY, categoryColumns, null , null, null, null,null,null);
        List<String> categories = new ArrayList<>();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                dbCategory category =  new dbCategory();
                category.setId(cursor.getLong(cursor.getColumnIndex(dbDatabaseHelper.KEY_ID)));
                category.setCategory(cursor.getString(cursor.getColumnIndex(dbDatabaseHelper.KEY_CATEGORY)));
                categories.add(category.getCategory());
            }
        }
        return categories;
    }


    public void removeCat(dbCategory category) {
        database.delete(dbDatabaseHelper.TABLE_CATEGORY, dbDatabaseHelper.KEY_ID + "=" + category.getId(), null);
    }
}
