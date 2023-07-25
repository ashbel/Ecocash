package com.fis.ecocash.ecocash.DataClasses;

import java.util.Date;

/**
 * Created by ashbelh on 2/4/2018.
 */

public class dbSMS {

    private long id;
    private String name;
    private Double amount;
    private String trantyp;
    private String trandate;
    private String tranMonth;
    private Double tranBalance;
    private String tranId;
    private String tranTxt;
    private Double tranCharge;
    private String tranCat;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTranId() {
        return tranId;
    }

    public void setTranId(String tranId) {
        this.tranId = tranId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getTrantyp() {
        return trantyp;
    }

    public void setTrantyp(String trantyp) {
        this.trantyp = trantyp;
    }

    public String getTrandate() {
        return trandate;
    }

    public void setTrandate(String trandate) {
        this.trandate = trandate;
    }

    public String getTranMonth() {
        return tranMonth;
    }

    public void setTranMonth(String tranMonth) {
        this.tranMonth = tranMonth;
    }

    public Double getTranBalance() {
        return tranBalance;
    }

    public void setTranBalance(Double tranBalance) {
        this.tranBalance = tranBalance;
    }

    public String getTranTxt() {
        return tranTxt;
    }

    public void setTranTxt(String tranTxt) {
        this.tranTxt = tranTxt;
    }

    public Double getTranCharge() {
        return tranCharge;
    }

    public void setTranCharge(Double tranCharge) {
        this.tranCharge = tranCharge;
    }

    public String getTranCat() {
        return tranCat;
    }

    public void setTranCat(String tranCat) {
        this.tranCat = tranCat;
    }

    @Override
    public String toString() {
        return "dbSMS{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", amount=" + amount +
                ", trantyp='" + trantyp + '\'' +
                ", trandate='" + trandate + '\'' +
                ", tranMonth='" + tranMonth + '\'' +
                ", tranBalance=" + tranBalance +
                ", tranId='" + tranId + '\'' +
                ", tranTxt='" + tranTxt + '\'' +
                ", tranCharge=" + tranCharge +
                ", tranCat='" + tranCat + '\'' +
                '}';
    }
}

