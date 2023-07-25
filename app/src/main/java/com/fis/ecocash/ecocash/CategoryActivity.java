package com.fis.ecocash.ecocash;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.fis.ecocash.ecocash.DataClasses.dbCategory;
import com.fis.ecocash.ecocash.DataClasses.dbOperations;
import com.fis.ecocash.ecocash.ListAdapters.CategoryListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    EditText fd_details,fd_title;
    TextView textFeedTitle,textFeed,textStatus, txtDate;
    String fdbk_details, fdbk_title;
    private dbOperations data;
    Button submit;
    final Context context = this;
    private Button button;
    private List<dbCategory> myDataset = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        data = new dbOperations(this);
        data.open();
        final ListView listview = (ListView) findViewById(R.id.listview);
        myDataset = data.getCat();

        CategoryListAdapter adapter = new CategoryListAdapter(this, myDataset);
        listview.setAdapter(adapter);
        // Click event for single list row
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final dbCategory item = (dbCategory) parent.getItemAtPosition(position);
                // custom dialog
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.cat_dialog_details);
                textFeedTitle = (TextView) dialog.findViewById(R.id.cat_title_input);
                textFeedTitle.setText(item.getCategory());
                dialog.show();
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.feed_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // custom dialog
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.cat_dialog);
                fd_title = (EditText) dialog.findViewById(R.id.cat_title_input);
                Button dialogButton = (Button) dialog.findViewById(R.id.btn_fd_submit);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dbCategory c = new dbCategory();
                        c.setCategory(fd_title.getText().toString());
                        data.addCat(c);
                        Intent i = new Intent(CategoryActivity.this, CategoryActivity.class);
                        startActivity(i);
                        dialog.dismiss();
                    }

                    });

                dialog.show();
                }
            });



        }
    }

