package com.fis.ecocash.ecocash;

import android.os.DropBoxManager;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

/**
 * Created by ashbelh on 3/4/2018.
 */

public class CurrencyFormatter implements IValueFormatter {

    private DecimalFormat mFormat;

    public CurrencyFormatter() {
        mFormat = new DecimalFormat("###,###,##0.00"); // use one decimal
    }
    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        return " $" + mFormat.format(value); // e.g. append a dollar-sign
    }
}
