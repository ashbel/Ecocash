package com.fis.ecocash.ecocash.ListAdapters;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fis.ecocash.ecocash.DataClasses.dbFiles;
import com.fis.ecocash.ecocash.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by ashbelh on 17/4/2018.
 */

public class FilesListAdapter extends BaseAdapter {

    private final Context context;
    private final Typeface mTfRegular;
    private List<dbFiles> files;
    private TextView filename,filedate;
    private ImageView img;
    SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy HH:mm");
    Date date = new Date();

    public FilesListAdapter(Context context, List<dbFiles> files)
    {
        this.context = context;
        this.files = files;
        mTfRegular = Typeface.createFromAsset(context.getAssets(), "OpenSans-Regular.ttf");
    }

    @Override
    public int getCount() {
        return files.size();
    }

    @Override
    public Object getItem(int i) {
        return files.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
       dbFiles file = files.get(i);
        View rowView =null;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.list_files, viewGroup, false);

            filename = (TextView) rowView.findViewById(R.id.filename);
            filedate = (TextView) rowView.findViewById(R.id.filedate);
            img = (ImageView) rowView.findViewById(R.id.imageView15);

            filename.setText(file.getFilename());
            filename.setTypeface(mTfRegular, Typeface.BOLD);
            date = new Date(file.getFiledate());
            filedate.setText(format.format(date));
            filedate.setTypeface(mTfRegular);
            if(file.getFilesource().equals("Storage"))
            {
                img.setImageResource(R.drawable.ic_sd_card_black_24dp);
            }
            else if(file.getFilesource().equals("Cloud"))
            {
                img.setImageResource(R.drawable.ic_drive);
            }
            //convertView.setTag(viewHolder);
        } else {
            rowView =view;
            filename = (TextView) view.findViewById(R.id.filename);
            filedate = (TextView) view.findViewById(R.id.filedate);
            img = (ImageView) view.findViewById(R.id.imageView15);

            filename.setText(file.getFilename());
            filename.setTypeface(mTfRegular, Typeface.BOLD);
            date = new Date(file.getFiledate());
            filedate.setText(format.format(date));
            filedate.setTypeface(mTfRegular);
            if(file.getFilesource().equals("Storage"))
            {
                img.setImageResource(R.drawable.ic_sd_card_black_24dp);
            }
            else if(file.getFilesource().equals("Cloud"))
            {
                img.setImageResource(R.drawable.ic_drive);
            }
        }
        return rowView;
    }
}
