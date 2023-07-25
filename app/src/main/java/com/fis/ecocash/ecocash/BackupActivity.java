package com.fis.ecocash.ecocash;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class BackupActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);

        viewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        Bundle b = getIntent().getExtras();
        BackupActivity.ViewPagerAdapter adapter = new BackupActivity.ViewPagerAdapter(getSupportFragmentManager());
        ManualFragment manual = new ManualFragment();
        RestoreFragment restore = new RestoreFragment();
        ScheduleFragment schedule = new ScheduleFragment();
        adapter.addFragment(manual, "MANUAL");
        adapter.addFragment(schedule, "SCHEDULE");
        adapter.addFragment(restore, "RESTORE");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {

        // TODO Auto-generated method stub
        calendar.set(Calendar.HOUR, i);
        calendar.set(Calendar.MINUTE, i1);
        //updateLabel();
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public void showDatePickerDialog(View v) {

       TimePickerDialog dialog = new TimePickerDialog(this, this, calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE),true);
       dialog.show();
    }
}

