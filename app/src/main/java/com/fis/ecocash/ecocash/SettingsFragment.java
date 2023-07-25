package com.fis.ecocash.ecocash;

import android.graphics.Typeface;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;


public class SettingsFragment extends Fragment {

    protected Typeface mTfRegular;

    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        mTfRegular = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Regular.ttf");
        TextView txtSettings = (TextView) view.findViewById(R.id.textSettings);
        TextView txtHeader = (TextView) view.findViewById(R.id.textView);
        TextView txt1 = (TextView) view.findViewById(R.id.textView3);
        TextView txt2 = (TextView) view.findViewById(R.id.textView4);
        TextView txt3 = (TextView) view.findViewById(R.id.textView5);
        txt1.setTypeface(mTfRegular);
        txt2.setTypeface(mTfRegular,Typeface.BOLD);
        txt3.setTypeface(mTfRegular);
        txtSettings.setTypeface(mTfRegular,Typeface.BOLD);
        txtHeader.setTypeface(mTfRegular);

        return view;
    }

}
