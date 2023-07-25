package com.fis.ecocash.ecocash.DataClasses;

import android.os.Build;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.drive.DriveFile;

import java.util.Comparator;

/**
 * Created by ashbelh on 18/4/2018.
 */

public class dbFiles implements Comparable<dbFiles>{

    private  String filename;
    private  long filedate;
    private  String filepath;
    private  String filesource;
    private DriveFile drive;


    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public long getFiledate() {
        return filedate;
    }

    public void setFiledate(long filedate) {
        this.filedate = filedate;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getFilesource() {
        return filesource;
    }

    public void setFilesource(String filesource) {
        this.filesource = filesource;
    }

    public DriveFile getDrive() {
        return drive;
    }

    public void setDrive(DriveFile drive) {
        this.drive = drive;
    }

    public static Comparator<dbFiles> DateComparator = new Comparator<dbFiles>() {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        public int compare(dbFiles f1, dbFiles f2) {

            long date1 = f1.getFiledate();
            long date2 = f2.getFiledate();

            return Long.compare(date2, date1);

        }
    };

    @Override
    public String toString() {
        return "dbFiles{" +
                "filename='" + filename + '\'' +
                ", filedate='" + filedate + '\'' +
                ", filepath='" + filepath + '\'' +
                ", filesource='" + filesource + '\'' +
                '}';
    }

    @Override
    public int compareTo(@NonNull dbFiles dbFiles) {
        return Long.valueOf(dbFiles.filedate).compareTo(Long.valueOf(dbFiles.filedate));
    }
}
