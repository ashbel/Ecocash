package com.fis.ecocash.ecocash;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.fis.ecocash.ecocash.DataClasses.dbMonths;
import com.fis.ecocash.ecocash.DataClasses.dbOperations;
import com.fis.ecocash.ecocash.DataClasses.dbSMS;
import com.fis.ecocash.ecocash.ListAdapters.MonthsListAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MonthsFragment extends Fragment {

    protected Typeface mTfRegular;
    ArrayList<dbSMS> sms_db = new ArrayList<dbSMS>();
    private dbOperations dboperation;

    public MonthsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_months, container, false);
        mTfRegular = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Regular.ttf");

        dboperation = new dbOperations(getContext());
        dboperation.open();

        List[] amount = getSmsLogs();

        TextView tv = (TextView) view.findViewById(R.id.imageView4);
        tv.setTypeface(mTfRegular,Typeface.BOLD);

        final List<dbMonths> mylist = getMonthData(amount[1],amount[0]);
        final ListView listview = (ListView) view.findViewById(R.id.listview4);

        MonthsListAdapter adapter = new MonthsListAdapter(getContext(), mylist);
        listview.setAdapter(adapter);

        // Click event for single list row
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final dbMonths item = (dbMonths) parent.getItemAtPosition(position);
                Intent intent = new Intent(getContext(),
                        MonthActivity.class);
                Bundle b = new Bundle();
                b.putString("month",item.getMonth());
                intent.putExtras(b);
                startActivity(intent);
            }
        });
        dboperation.close();
        return view;
    }

    private List[] getSmsLogs() {
        List[] amount = new List[2];
        ArrayList<String> dtmthns = new ArrayList<>();
        ArrayList<dbSMS> dbList = new ArrayList<>();
        List<dbSMS> transactions = dboperation.getAllTransactions();
        for(dbSMS dbsms : transactions) {
            if (!dtmthns.contains(dbsms.getTranMonth())) {
                dtmthns.add(dbsms.getTranMonth());
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
        smsList.size();
        Date date = new Date();
        for (String y: mylist) {
            Double in=0.0;
            Double out=0.0;
            Double balance = 0.00;
            dbMonths months = new dbMonths();
            int count =0;
            for (dbSMS h: smsList) {

                if(h.getTranMonth().contentEquals(y)){
                    if(h.getAmount()>0) {
                        in = in + h.getAmount();
                        long dateLong = Long.parseLong(h.getTrandate());
                        date = new Date(dateLong);
                        datestr = format.format(date);

                    }
                    else{
                        out = out +h.getAmount();
                            long dateLong = Long.parseLong(h.getTrandate());
                            date = new Date(dateLong);
                            datestr = format.format(date);

                    }
                       // Log.e("BAL",h.getName()+" : "+datestr + " : "+h.getTranBalance() + " : "+count);
                       if(count==0) {
                           balance = h.getTranBalance();
                       }

                        count++;
                }
                months.setOut(out);
                months.setIn(in);
                months.setMonth(y);
                months.setBalance(balance);


            }
//            Log.e("IN",y+" : "+months.getIn());
//            Log.e("OUT",y+" : "+months.getOut());
            c.add(months);
        }
        return c;
    }

}
