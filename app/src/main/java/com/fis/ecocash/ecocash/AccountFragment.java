package com.fis.ecocash.ecocash;

import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.fis.ecocash.ecocash.DataClasses.dbMonths;
import com.fis.ecocash.ecocash.DataClasses.dbOperations;
import com.fis.ecocash.ecocash.DataClasses.dbSMS;
import com.fis.ecocash.ecocash.ListAdapters.AccountsListAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static java.util.Collections.sort;

public class AccountFragment extends Fragment {
    protected Typeface mTfRegular;
    ArrayList<dbSMS> sms_db = new ArrayList<dbSMS>();
    ExpandableListView expandableListView;
    AccountsListAdapter expandableListAdapter;
    List<dbMonths> expandableListTitle;
    HashMap<dbMonths, List<dbSMS>> expandableListDetail;
    EditText inputSearch;
    private dbOperations dboperation;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            View view = inflater.inflate(R.layout.fragment_account, container, false);
            mTfRegular = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Regular.ttf");
            dboperation = new dbOperations(getContext());
            dboperation.open();
            List[] amount = getSmsLogs();
            TextView tv = (TextView) view.findViewById(R.id.imageView4);
            tv.setTypeface(mTfRegular,Typeface.BOLD);
            inputSearch = (EditText) view.findViewById(R.id.inputSearch);
            expandableListView = (ExpandableListView) view.findViewById(R.id.listview4);
            expandableListDetail = getData(amount[1],amount[0]);
            expandableListTitle = new ArrayList<dbMonths>(expandableListDetail.keySet());
            Collections.sort(expandableListTitle, dbMonths.NameComparator);
            expandableListAdapter = new AccountsListAdapter(getContext(), expandableListTitle, expandableListDetail);

            inputSearch.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable arg0) {
                    // TODO Auto-generated method stub
                    String text = inputSearch.getText().toString().toLowerCase(Locale.getDefault());
                    expandableListAdapter.getFilter().filter(text);
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

            expandableListView.setAdapter(expandableListAdapter);
            dboperation.close();
            return view;
        }

        private List[] getSmsLogs() {

            List[] amount = new List[2];
            ArrayList<String> dtmthns = new ArrayList<>();
            ArrayList<dbSMS> dbList = new ArrayList<>();
            List<dbSMS> transactions = dboperation.getAllTransactions();
            for(dbSMS dbsms : transactions) {
                if (!dtmthns.contains(dbsms.getName())) {
                    dtmthns.add(dbsms.getName());
                }
                dbList.add(dbsms);
            }
            amount[0] = dbList;
            amount[1] = dtmthns;
            return amount;

        }
        public static HashMap<dbMonths, List<dbSMS>> getData(List<String> mylist, List<dbSMS> smsList) {
        HashMap<dbMonths, List<dbSMS>> expandableListDetail = new HashMap<dbMonths, List<dbSMS>>();
        for (String y: mylist) {
            Double amount=0.0;
            Double in=0.0;
            Double out=0.0;
            Double balance = 0.00;
            dbMonths months = new dbMonths();
            List<dbSMS> cricket = new ArrayList<dbSMS>();
            for (dbSMS h: smsList) {
                if (h.getName().trim().toUpperCase().contentEquals(y.toUpperCase())) {
                    if (h.getTrantyp().equals("Cash In") || (h.getTrantyp().equals("Money Received"))) {
                        in = in + h.getAmount();
                    } else {
                        if (h.getAmount() != null) {
                            out = out + h.getAmount();
                        }
                    }
                    cricket.add(h);
                }
                months.setOut(out);
                months.setIn(in);
                months.setMonth(y.toUpperCase());
                months.setBalance(balance);
            }
            if(cricket.size()>0) {

                expandableListDetail.put(months, cricket);
            }
        }
        return expandableListDetail;
    }

}
