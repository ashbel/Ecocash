package com.fis.ecocash.ecocash;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.fis.ecocash.ecocash.DataClasses.dbBackup;
import com.fis.ecocash.ecocash.DataClasses.dbOperations;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class ScheduleFragment extends Fragment implements TimePickerDialog.OnTimeSetListener, View.OnClickListener {

    //Pending intent instance
    private PendingIntent pendingIntent;
    private dbOperations dboperation;
    //Alarm Request Code
    private static final int ALARM_REQUEST_CODE = 133;
    protected Typeface mTfRegular;
    Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
    EditText sec_date;
    TimePicker timePicker;
    TextView timetext;
    Button btnSave;
    dbBackup backup;
    private CheckBox storage,cloud;
    DateFormat format = new SimpleDateFormat("HH:mm");


    public ScheduleFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        dboperation = new dbOperations(getContext());
        dboperation.open();
        //getting the timepicker object
       // timePicker = (TimePicker) view.findViewById(R.id.timePicker);
        mTfRegular = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Regular.ttf");
        TextView tv = (TextView) view.findViewById(R.id.backLabel);
        timetext = (TextView) view.findViewById(R.id.backupTime);
        btnSave = (Button) view.findViewById(R.id.start_alarm_button);
        tv.setTypeface(mTfRegular, Typeface.BOLD);
        storage = (CheckBox) view.findViewById(R.id.checkBox);
        cloud = (CheckBox) view.findViewById(R.id.checkBox2);
        storage.setTypeface(mTfRegular, Typeface.BOLD);
        cloud.setTypeface(mTfRegular, Typeface.BOLD);

        final int count = dboperation.backCount();

        if(count ==1){
            backup = dboperation.getBack();
            long t = Long.valueOf(backup.getBackdate());
            Date date = new Date(t);
            DateFormat formatter = new SimpleDateFormat("HH:mm");
            timetext.setText(formatter.format(date));
            if(backup.getDevice() == 1){
                storage.setChecked(true);
            }
            if(backup.getGoogle() == 1){
                cloud.setChecked(true);
            }
        }
        else {
            backup = new dbBackup();
        }

        /* Retrieve a PendingIntent that will perform a broadcast */
        Intent alarmIntent = new Intent(getContext(), AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(getContext(), ALARM_REQUEST_CODE, alarmIntent, 0);

        //Set On CLick over start alarm button
        timetext.setOnClickListener(this);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar current_calendar = Calendar.getInstance();
                if(!timetext.getText().toString().isEmpty()) {


                   if(storage.isChecked()){
                       backup.setDevice(1);
                   }
                   else{
                       backup.setDevice(0);
                   }

                    if(cloud.isChecked()){
                        backup.setGoogle(1);
                    }
                    else{
                        backup.setGoogle(0);
                    }
                    if(count == 0)
                    {
                        backup.setBackdate(calendar.getTimeInMillis()+"");
                        dboperation.addBackup(backup);
                    }
                    else {
                        String tym = timetext.getText().toString();

                        try {
                            calendar.setTime(format.parse(tym));// all done
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        backup.setBackdate(calendar.getTimeInMillis()+"");
                        dboperation.updateBackup(backup);
                    }
                    if (calendar.getTimeInMillis() >= current_calendar.getTimeInMillis()) {
                        triggerAlarmManager(calendar.getTimeInMillis());
                    } else {
                        calendar.add(Calendar.DAY_OF_MONTH, 1);
                        triggerAlarmManager(calendar.getTimeInMillis());
                    }
                }
                else{
                    Toast.makeText(getContext(),"Please Enter Time",Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }

    //Trigger alarm manager with entered time interval
    public void triggerAlarmManager(long time) {

        Date date = new Date(time);
        DateFormat formatter = new SimpleDateFormat("HH:mm");
        String dateFormatted = formatter.format(date);


        AlarmManager manager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);//get instance of alarm manager

        manager.setRepeating(AlarmManager.RTC, time,AlarmManager.INTERVAL_DAY ,pendingIntent);//set alarm manager with entered timer by converting into milliseconds

        Toast.makeText(getContext(), "Backup Set for " + dateFormatted + " Daily.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        TimePickerDialog dialog = new TimePickerDialog(getContext(), this, calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE),false);
        dialog.show();
    }
    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        calendar.set(Calendar.HOUR, i);
        calendar.set(Calendar.MINUTE, i1);
        updateLabel(calendar);
    }

    private void updateLabel(Calendar c) {
        long t = calendar.getTimeInMillis();
        Date date = new Date(t);
        DateFormat formatter = new SimpleDateFormat("HH:mm");
        String dateFormatted = formatter.format(date);
        timetext.setText(dateFormatted);
    }
}
