package com.fis.ecocash.ecocash;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.fis.ecocash.ecocash.DataClasses.dbOperations;
import com.fis.ecocash.ecocash.DataClasses.dbSMS;
import com.fis.ecocash.ecocash.ListAdapters.TransactionListAdapter;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class TransactionFragment extends Fragment implements
        SearchView.OnQueryTextListener,AdapterView.OnItemSelectedListener {

    public static final String OTP_REGEX = "[0-9]{1,6}";
    ArrayList<dbSMS> sms_db = new ArrayList<dbSMS>();
    String value="";
    protected Typeface mTfRegular;
    private dbOperations dboperation;
    // Search EditText
    EditText inputSearch;
    TransactionListAdapter adapter;
    public TextView account, balance,status,txt_charge, amounttxt;
    public ImageView img;
    NumberFormat formatter = new DecimalFormat("#0.00");
    Spinner spinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_transaction, container, false);
        mTfRegular = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Regular.ttf");
        TextView txtBal = (TextView) view.findViewById(R.id.imageView4);
        inputSearch = (EditText) view.findViewById(R.id.inputSearch);

        txtBal.setTypeface(mTfRegular,Typeface.BOLD);
        dboperation = new dbOperations(getContext());
        dboperation.open();

        Bundle b = this.getArguments();;
        if(b != null)
            value = b.getString("month");
        TextView tv = (TextView) view.findViewById(R.id.imageView4);
        tv.setTypeface(mTfRegular,Typeface.BOLD);
         List<dbSMS> mylist = getSmsLogs();
         ListView listview = (ListView) view.findViewById(R.id.listview4);

        adapter = new TransactionListAdapter(getContext(), mylist);
        //listview.setAdapter(adapter);


        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                String text = inputSearch.getText().toString().toLowerCase(Locale.getDefault());
                adapter.getFilter().filter(text);
                //listview.noti;
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub
            }
        });

        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                dbSMS expandedListText = (dbSMS) parent.getItemAtPosition(position);
                Log.e("TG",position+"");
                // custom dialog
                final Dialog rowView = new Dialog(getContext());
                rowView.setContentView(R.layout.sms_dialog_details);
                account = (TextView) rowView.findViewById(R.id.textName);
                amounttxt = (TextView) rowView.findViewById(R.id.textAmount);
                status = (TextView) rowView.findViewById(R.id.textDate);
//                spinner = (Spinner) rowView.findViewById(R.id.spinner);
                List<String> lables = dboperation.getCatString();

                // Creating adapter for spinner
//                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item, lables);

                // Drop down layout style - list view with radio button
//                dataAdapter
//                        .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // attaching data adapter to spinner
//                spinner.setAdapter(dataAdapter);
//                loan_amount = (TextView) rowView.findViewById(R.id.tran_description);
//                img = (ImageView) rowView.findViewById(R.id.imageView13);
//                balance = (TextView) rowView.findViewById(R.id.balanceText);
                TextView sms = (TextView) rowView.findViewById(R.id.smstxt);

                account.setText(expandedListText.getName().toUpperCase());
                account.setTypeface(mTfRegular, Typeface.BOLD);
                sms.setTypeface(mTfRegular);
                sms.setText(expandedListText.getTranTxt());
                String amnt ="";
                if(expandedListText.getAmount()!=null) {
                    amnt = formatter.format(expandedListText.getAmount());

                    if (expandedListText.getAmount() < 0) {
                        amounttxt.setTextColor(Color.parseColor("#ffff4444"));
                        Double amount = expandedListText.getAmount() * -1;
                        amnt = formatter.format(amount);
                        amounttxt.setTypeface(mTfRegular, Typeface.BOLD);
                        amounttxt.setText("-$" + amnt);
                    }
                    if (expandedListText.getAmount() > 0) {
                        amounttxt.setTextColor(Color.parseColor("#ff669900"));
                        amounttxt.setText("$" + amnt);
                        amounttxt.setTypeface(mTfRegular, Typeface.BOLD);
                    }
                }
//
//                //balance.setTextColor(Color.parseColor("#ff669900"));
//                balance.setText("(Bal: $" + formatter.format(expandedListText.getTranBalance())+ " Charge : $"+ formatter.format(expandedListText.getTranCharge())+")");
//                balance.setTypeface(mTfRegular);
//
//                loan_amount.setText(expandedListText.getTrantyp());
//                loan_amount.setTypeface(mTfRegular, Typeface.BOLD);
                SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
                Date date = new Date();
                long dateLong = Long.parseLong(expandedListText.getTrandate());
                date = new Date(dateLong);
                String datestr = format.format(date);
                status.setText(datestr);
                status.setTypeface(mTfRegular);
               rowView.show();
            }
        });
       // dboperation.close();
        return  view;
    }


    private List<dbSMS> getSmsLogs() {
        ArrayList<dbSMS> dbList = new ArrayList<>();
        List<dbSMS> transactions = dboperation.getAllTransactions(value);
        for(dbSMS dbsms : transactions) {
            dbList.add(dbsms);
        }
        return dbList;
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
            }
            //Log.e("AMOUNT",y+" - "+ String.valueOf(amount));
        }
        return expandableListDetail;
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

    /**
     * Function to load the spinner data from SQLite database
     * */
    private void loadSpinnerData() {
        // database handler


        // Spinner Drop down elements

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long id) {
        // On selecting a spinner item
        String label = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "You selected: " + label,
                Toast.LENGTH_LONG).show();

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }
}
