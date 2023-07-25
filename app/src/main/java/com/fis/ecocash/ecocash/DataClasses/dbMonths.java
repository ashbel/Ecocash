package com.fis.ecocash.ecocash.DataClasses;

import androidx.annotation.NonNull;

import java.util.Comparator;
import java.util.Date;

/**
 * Created by ashbelh on 2/4/2018.
 */

public class dbMonths implements Comparable<dbMonths>{

    private String month;
    private Double in;
    private Double out;
    private Double balance;

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public Double getIn() {
        return in;
    }

    public void setIn(Double in) {
        this.in = in;
    }

    public Double getOut() {
        return out;
    }

    public void setOut(Double out) {
        this.out = out;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public static Comparator<dbMonths> NameComparator = new Comparator<dbMonths>() {

        public int compare(dbMonths fruit1, dbMonths fruit2) {

            String fruitName1 = fruit1.getMonth().toUpperCase();
            String fruitName2 = fruit2.getMonth().toUpperCase();

            //ascending order
            return fruitName1.compareTo(fruitName2);

            //descending order
//            return fruitName2.compareTo(fruitName1);
        }
    };

    @Override
    public int compareTo(@NonNull dbMonths dbMonths) {
        return 0;
    }
}
