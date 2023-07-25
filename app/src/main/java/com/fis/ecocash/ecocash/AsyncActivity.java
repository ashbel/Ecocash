package com.fis.ecocash.ecocash;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fis.ecocash.ecocash.DataClasses.dbFiles;
import com.fis.ecocash.ecocash.ListAdapters.FilesListAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import static com.google.android.gms.drive.Drive.DriveApi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AsyncActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "LOG" ;
    // button to show progress dialog
    Button btnShowProgress;

    // Progress Dialog
    private ProgressDialog pDialog;
    ImageView my_image;
    // Progress dialog type (0 - for Horizontal progress bar)
    public static final int progress_bar_type = 0;
    private GoogleApiClient GAC ;
    final List<dbFiles> fileArray = new ArrayList<>();
    FilesListAdapter adapter;
    ListView listview;
    private static final int REQUEST_CODE_RESOLUTION = 3;

    // File url to download
    private static String file_url = "https://api.androidhive.info/progressdialog/hive.jpg";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async);

        // show progress bar button
        btnShowProgress = (Button) findViewById(R.id.btn_bill_submit);
        listview = (ListView) findViewById(R.id.listview4);
        // Image view to show image after downloading
        my_image = (ImageView) findViewById(R.id.my_image);

        if (GAC == null) {
            GAC = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    // Optionally, add additional APIs and scopes if required.
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        /**
         * Show Progress bar click event
         * */
        btnShowProgress.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // starting new Async Task
                GAC.connect();
            }
        });
    }

    /**
     * Showing Dialog
     * */
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type: // we set this to 0
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Downloading file. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pDialog.setCancelable(true);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "API client connected.");
        Log.e("TAG", GAC+"");
        new DownloadFileFromURL().execute(file_url);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "GoogleApiClient connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        // Called whenever the API client fails to connect.
        Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
        if (!result.hasResolution()) {
            // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog(this, result.getErrorCode(), 0).show();
            return;
        }

        try {
            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
        }

    }

    /**
     * Background Async Task to download file
     * */
    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);
            Log.e("TAG", "Pre Execute");
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
                MetadataBuffer metadataBuffer = DriveApi.getRootFolder(GAC).listChildren(GAC).await().getMetadataBuffer();
                int iCount = metadataBuffer.getCount();
            if (iCount>=1) {

                for (int i = 0; i < iCount; i++) {
                    final dbFiles files = new dbFiles();
                    DriveId myFileId = metadataBuffer.get(i).getDriveId();
                    final DriveFile file = myFileId.asDriveFile();
                    //file.getMetadata(GAC);
                    DriveResource.MetadataResult mdRslt =  file.getMetadata(GAC).await();
                    if (mdRslt != null && mdRslt.getStatus().isSuccess()) {
                        String link = mdRslt.getMetadata().getWebContentLink();
                        String name = mdRslt.getMetadata().getTitle();
                        Date date = mdRslt.getMetadata().getCreatedDate();
                        files.setFiledate(date.getTime());
                        files.setFilename(name);
                        files.setFilepath(link);
                        files.setFilesource("Cloud");
                        fileArray.add(files);

                    }
                    publishProgress("" + (int) ((i+1 * 100) / iCount));
                }
            }
            Log.e("TAG", fileArray.size()+"");
            return "SUCCESS";
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
            Log.e("TAG", "Progress Update");
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            Log.e("TAG", "POST Execute");
            Log.e("TAG", fileArray.size()+"");
            // dismiss the dialog after the file was downloaded
            dismissDialog(progress_bar_type);

            adapter = new FilesListAdapter(getBaseContext(),fileArray);
            listview.setAdapter(adapter);;
        }

    }
}
