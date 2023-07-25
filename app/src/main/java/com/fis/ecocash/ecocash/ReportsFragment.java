package com.fis.ecocash.ecocash;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.fis.ecocash.ecocash.DataClasses.dbOperations;
import com.fis.ecocash.ecocash.DataClasses.dbSMS;
import com.fis.ecocash.ecocash.ListAdapters.TransactionListAdapter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ReportsFragment extends Fragment implements  DatePickerDialog.OnDateSetListener,  View.OnFocusChangeListener,  View.OnClickListener, SearchView.OnQueryTextListener {

    private static final String TAG ="CSV" ;
    protected Typeface mTfRegular;
    private dbOperations dboperation;
    // Search EditText
    TextView inputSearch,startDate,endDate,filterBtn,exportBtn;
    TransactionListAdapter adapter;
    Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
    Calendar calendar_end = Calendar.getInstance(TimeZone.getDefault());
    public static final long HOUR = 3600*1000;
    static final int START_DATE = 1;
    static final int END_DATE = 2;
    int cur = 0;
    List<dbSMS> mylist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reports, container, false);
        TextView txtHeader = (TextView) view.findViewById(R.id.txtReport);
        txtHeader.setTypeface(mTfRegular,Typeface.BOLD);
        mTfRegular = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Regular.ttf");
        startDate = (TextView) view.findViewById(R.id.startDate);
        endDate = (TextView) view.findViewById(R.id.endDate);
        exportBtn = (TextView) view.findViewById(R.id.button2);
        filterBtn = (TextView) view.findViewById(R.id.btnFilter);
        dboperation = new dbOperations(getContext());
        dboperation.open();
        mylist = getSmsLogs();
        final ListView listview = (ListView) view.findViewById(R.id.listview4);

        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sdate = startDate.getText().toString();
                String edate = endDate.getText().toString();
                Long e_date_ = Long.valueOf(0);
                Long s_date_ = Long.valueOf(0);

                DateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
                try {
                    Date s_date = format.parse(sdate);
                    s_date_ = s_date.getTime();
                    Date e_date = format.parse(edate);
                    e_date_ = (e_date.getTime() + 24*HOUR)-1;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(edate.isEmpty()){e_date_ = new Date().getTime();}
                if(!sdate.isEmpty()) {
                    mylist = getSmsLogs(s_date_ + "", e_date_ + "");
                    adapter = new TransactionListAdapter(getContext(), mylist);
                    listview.setAdapter(adapter);
                }
                else if (sdate.isEmpty() && !edate.isEmpty()){
                    Toast.makeText(getContext(),"Please Enter Start Date",Toast.LENGTH_LONG).show();
                    startDate.requestFocus();
                }
                else
                {
                    mylist = getSmsLogs();
                    adapter = new TransactionListAdapter(getContext(), mylist);
                    listview.setAdapter(adapter);
                }
            }
        });

        exportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               export(mylist);
            }
        });
        startDate.setOnClickListener(this);
        endDate.setOnClickListener(this);
        adapter = new TransactionListAdapter(getContext(), mylist);
        listview.setAdapter(adapter);
        //dboperation.close();
        return view;
    }

    private List<dbSMS> getSmsLogs() {
        ArrayList<dbSMS> dbList = new ArrayList<>();
        List<dbSMS> transactions = dboperation.getAllTransactions();
        for(dbSMS dbsms : transactions) {
            dbList.add(dbsms);
        }
        return dbList;
    }

    private List<dbSMS> getSmsLogs(String sdate,String edate) {
        ArrayList<dbSMS> dbList = new ArrayList<>();
        List<dbSMS> transactions = dboperation.getAllTransactions(sdate,edate);
        for(dbSMS dbsms : transactions) {
            dbList.add(dbsms);
        }
        return dbList;
    }

    private void export(List<dbSMS> mylist) {
        //Log.d(TAG, "backupDatabaseCSV");
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
        NumberFormat formatter = new DecimalFormat("#0.00");
        final String   filename = ""+ format.format(date) +"_Export.csv";
        Boolean returnCode = false;
        int i = 0;
        String csvHeader = "";
        String csvValues = "";
        csvHeader = "TRANSACTION ID,DATE,NAME,AMOUNT,BALANCE,CHARGE \n";

        try {
            File outFile = new File(Environment.getExternalStorageDirectory(),"/ziWallet_");
            FileWriter fileWriter = new FileWriter(outFile+filename);
            Log.e(TAG,outFile+filename);
            BufferedWriter out = new BufferedWriter(fileWriter);
            out.write(csvHeader);
            for(dbSMS sms: mylist)
            {
                if(sms.getName().equals("Airtime")) {
                    csvValues =  " ,";
                } else {
                    csvValues = sms.getTranId() + ",";
                }
                csvValues +=  format.format(new Date(Long.parseLong(sms.getTrandate()))) + ",";
                csvValues += sms.getName().toUpperCase() + ",";
                csvValues += formatter.format(sms.getAmount()) + ",";
                csvValues += formatter.format(sms.getTranBalance()) + ",";
                csvValues += formatter.format(sms.getTranCharge())
                        + "\n";
                out.write(csvValues);
            }
            out.close();
            String imagePath = Environment.getExternalStorageDirectory()+"/ziWallet_"+ filename;

            File imageFileToShare = new File(Environment.getExternalStorageDirectory(),"/ziWallet_"+filename);
            Uri uri = Uri.fromFile(imageFileToShare);
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("*/*");
            Log.e(TAG,uri.getPath());
            sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
            returnCode = true;
        } catch (IOException e) {
            returnCode = false;
            Log.d(TAG, "IOException: " + e.getMessage());
        }

    }

    @Override
    public void onFocusChange(View view, boolean b) {
        switch (view.getId()) {
            case R.id.startDate:
                cur = START_DATE;
                DatePickerDialog dialog = new DatePickerDialog(getContext(), this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
                break;
            case R.id.endDate:
                cur = END_DATE;
                DatePickerDialog dialog2 = new DatePickerDialog(getContext(), this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                dialog2.show();
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.startDate:
                cur = START_DATE;
                DatePickerDialog dialog = new DatePickerDialog(getContext(), this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
                break;
            case R.id.endDate:
                cur = END_DATE;
                DatePickerDialog dialog2 = new DatePickerDialog(getContext(), this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                dialog2.show();
                break;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {
        // TODO Auto-generated method stub
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        if(cur == START_DATE) {
            updateStart();
        } else {
            updateEnd();
        }

    }

    private void updateStart() {
        String myFormat = "dd-MM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        startDate.setText(sdf.format(calendar.getTime()));
    }

    private void updateEnd() {
        String myFormat = "dd-MM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        endDate.setText(sdf.format(calendar.getTime()));
    }


    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        adapter.getFilter().filter(s);
        return  false;
    }
}
