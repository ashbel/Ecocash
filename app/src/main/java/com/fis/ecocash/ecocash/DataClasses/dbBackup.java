package com.fis.ecocash.ecocash.DataClasses;

/**
 * Created by ashbelh on 27/4/2018.
 */

public class dbBackup {
    private  long id;
    private  int google;
    private  int device;
    private  String backdate;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBackdate() {
        return backdate;
    }

    public void setBackdate(String backdate) {
        this.backdate = backdate;
    }

    public int getGoogle() {
        return google;
    }

    public void setGoogle(int google) {
        this.google = google;
    }

    public int getDevice() {
        return device;
    }

    public void setDevice(int device) {
        this.device = device;
    }

    @Override
    public String toString() {
        return "dbBackup{" +
                "id=" + id +
                ", backdate='" + backdate + '\'' +
                ", google=" + google +
                ", device=" + device +
                '}';
    }
}
