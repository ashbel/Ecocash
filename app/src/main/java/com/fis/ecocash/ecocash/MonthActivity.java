package com.fis.ecocash.ecocash;

import android.os.Bundle;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.github.mikephil.charting.charts.PieChart;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MonthActivity extends AppCompatActivity {

    PieChart mChart;
//    private dbOperations dboperation;
//    private dbLoans myDataset;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public long value;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month);

        viewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        Bundle b = getIntent().getExtras();
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        OverviewFragment overview = new OverviewFragment();
        TransactionFragment transaction = new TransactionFragment();
        ScheduleFragment schedule = new ScheduleFragment();
        schedule.setArguments(b);
        transaction.setArguments(b);
        overview.setArguments(b);
        adapter.addFragment(overview, "OVERVIEW");
        adapter.addFragment(transaction, "TRANSACTIONS");
//        adapter.addFragment(schedule, "OTHER");
        viewPager.setAdapter(adapter);
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
}
