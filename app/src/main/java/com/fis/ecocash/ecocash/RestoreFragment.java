package com.fis.ecocash.ecocash;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.IntentSender;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fis.ecocash.ecocash.DataClasses.dbDatabaseHelper;
import com.fis.ecocash.ecocash.DataClasses.dbFiles;
import com.fis.ecocash.ecocash.ListAdapters.FilesListAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.MetadataBuffer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.google.android.gms.drive.Drive.DriveApi;


public class RestoreFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = "drive-quickstart";
    private GoogleApiClient GAC  ;
    private ProgressDialog progressBar;
    private static final int REQUEST_CODE_RESOLUTION = 3;
    protected Typeface mTfRegular;
    FilesListAdapter adapter;
    private CheckBox storage,cloud;
    ListView listview;
    private ProgressDialog pDialog ;
    private ProgressDialog rDialog ;
    public static final int progress_bar_type = 0;
    final List<dbFiles> fileArray = new ArrayList<>();

    //01:53:A3:C9:A0:DC:0F:DB:C6:E5:73:B9:53:AF:2E:16:4F:3C:50:A1 Debug Key
    //20:CA:1A:2A:A1:56:B9:BD:AA:83:67:25:49:35:7F:6B:4F:F1:2F:5E Live Key
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_restore, container, false);
        mTfRegular = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Regular.ttf");
        listview = (ListView) view.findViewById(R.id.listview4);
        TextView tv = (TextView) view.findViewById(R.id.imageView4);
        storage = (CheckBox) view.findViewById(R.id.checkBox);
        cloud = (CheckBox) view.findViewById(R.id.checkBox2);
        tv.setTypeface(mTfRegular, Typeface.BOLD);
        storage.setTypeface(mTfRegular, Typeface.BOLD);
        cloud.setTypeface(mTfRegular, Typeface.BOLD);
        storage = (CheckBox)view.findViewById(R.id.checkBox);
        storage.setChecked(false);
        cloud = (CheckBox)view.findViewById(R.id.checkBox2);
        cloud.setChecked(false);


        if (GAC == null) {
            GAC = new GoogleApiClient.Builder(getContext())
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    // Optionally, add additional APIs and scopes if required.
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }

        storage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        DriveChecked();
            }
        });
        cloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    DriveChecked();
            }
        });
         GAC.disconnect();
        return view;
    }

    public  void DriveChecked() {

        fileArray.clear();
        if (storage.isChecked() && cloud.isChecked()) {
            List<dbFiles> storage_list = getFilesFromStorage();
            fileArray.addAll(storage_list);
            if (GAC.isConnected()) {
                new GoogleDrive().execute("Hello");
            }
            else {
                GAC.connect();
            }
        }

        if (storage.isChecked() && !cloud.isChecked()) {
            List<dbFiles> storage_list = getFilesFromStorage();
            fileArray.addAll(storage_list);
            UpdateUI(fileArray);
        }

        if (cloud.isChecked() && !storage.isChecked()) {
            if (GAC.isConnected()) {
                new GoogleDrive().execute("Hello");
            }
            else {
                GAC.connect();
            }
        }

        if (!cloud.isChecked() && !storage.isChecked()) {
                fileArray.clear();
                UpdateUI(fileArray);
            }

    }

    public void UpdateUI(List<dbFiles> array){
        Collections.sort(array, dbFiles.DateComparator);
        adapter = new FilesListAdapter(getContext(),array);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final dbFiles item = (dbFiles) parent.getItemAtPosition(position);
                importDB(getContext(),item);
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
         DriveChecked();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        // Called whenever the API client fails to connect.
        //Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
        if (!result.hasResolution()) {
            // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), result.getErrorCode(), 0).show();
            return;
        }

        try {
            result.startResolutionForResult(getActivity(), REQUEST_CODE_RESOLUTION);
        } catch (IntentSender.SendIntentException e) {
            //Log.e(TAG, "Exception while starting resolution activity", e);
        }
    }

    private List<dbFiles> getFilesFromStorage(){
        List<dbFiles> fileArray = new ArrayList<>();
        String path = Environment.getExternalStorageDirectory().toString()+"/ZWallet";
        File directory = new File(path);
        File[] files = directory.listFiles();
        for (int i = 0; i < files.length; i++)
        {
            dbFiles file = new dbFiles();
             file.setFiledate(files[i].lastModified());
             file.setFilename(files[i].getName());
             file.setFilepath(files[i].getPath());
             file.setFilesource("Storage");
             fileArray.add(file);
        }
        return fileArray;
    }

    public void importDB(Context context,dbFiles file) {
        if (file.getFilesource().equals("Storage")) {
            try {
                File sd = new File(Environment.getExternalStorageDirectory(), "/ZWallet");
                if (sd.canWrite()) {
                    File backupDB = new File(sd, file.getFilename());
                    File currentDB = context.getDatabasePath(dbDatabaseHelper.DATABASE_NAME);

                    FileChannel src = new FileInputStream(backupDB).getChannel();
                    FileChannel dst = new FileOutputStream(currentDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                    Toast.makeText(getContext(), "Import Successful", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            readFromGooDrive(file.getDrive());
        }

    }

    class GoogleDrive extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Downloading. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setMax(100);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setCancelable(true);
            pDialog.show();
            getActivity().showDialog(progress_bar_type);
        }

        @Override
        protected String doInBackground(String... f_url) {
            MetadataBuffer metadataBuffer = DriveApi.getRootFolder(GAC).listChildren(GAC).await().getMetadataBuffer();
            int iCount = metadataBuffer.getCount();
            if (iCount>=1) {

                for (int i = 0; i < iCount; i++) {
                    final dbFiles files = new dbFiles();
                    DriveId myFileId = metadataBuffer.get(i).getDriveId();
                    final DriveFile file = myFileId.asDriveFile();

                    DriveResource.MetadataResult mdRslt =  file.getMetadata(GAC).await();
                    if (mdRslt != null && mdRslt.getStatus().isSuccess()) {
                        String link = mdRslt.getMetadata().getWebContentLink();
                        String name = mdRslt.getMetadata().getTitle();
                        Date date = mdRslt.getMetadata().getCreatedDate();
                        files.setFiledate(date.getTime());
                        files.setFilename(name);
                        files.setDrive(file);
                        files.setFilepath(link);
                        files.setFilesource("Cloud");
                        fileArray.add(files);
                    }
                    publishProgress("" + (int) ((i+1 * 100) / iCount));
                }
            }
            metadataBuffer.release();
            return "SUCCESS";
        }

        protected void onProgressUpdate(String... progress) {
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            UpdateUI(fileArray);
        }

    }

    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            rDialog = new ProgressDialog(getActivity());
            rDialog.setMessage("Downloading Database. Please wait...");
            rDialog.setIndeterminate(false);
            rDialog.setMax(100);
            rDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            rDialog.setCancelable(true);
            rDialog.show();
            //showDialog(progress_bar_type);
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();

                int contentLength = conection.getContentLength();
                //Log.e(TAG, "Content Length " + contentLength);
                File currentDB = getActivity().getDatabasePath(dbDatabaseHelper.DATABASE_NAME);

                DataInputStream stream = new DataInputStream(url.openStream());

                byte[] buffer = new byte[1024];
                stream.readFully(buffer);
                stream.close();
                rDialog.setMessage("Restoring Database. Please wait...");
                DataOutputStream fos = new DataOutputStream(new FileOutputStream(currentDB));
                fos.write(buffer);
                fos.flush();
                fos.close();




            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
           // pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(String file_url) {
             rDialog.dismiss();
            //boolean deleted = file.delete();
            Toast.makeText(getContext(), "Import Successful", Toast.LENGTH_LONG).show();
        }

    }


    void readFromGooDrive( DriveFile file) {
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Downloading. Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setMax(100);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setCancelable(true);
        pDialog.show();
        byte[] buf = null;
        if (GAC != null && GAC.isConnected()) try {
            file.open(GAC, DriveFile.MODE_READ_ONLY, null)
                    .setResultCallback(new ResultCallback<com.google.android.gms.drive.DriveApi.DriveContentsResult>() {
                        @Override
                        public void onResult(com.google.android.gms.drive.DriveApi.DriveContentsResult driveContentsResult) {
                            if ((driveContentsResult != null) && driveContentsResult.getStatus().isSuccess()) {
                                DriveContents contents = driveContentsResult.getDriveContents();
                                InputStream input = contents.getInputStream();

                        try {
                            File out_file = getContext().getDatabasePath(dbDatabaseHelper.DATABASE_NAME);
                            OutputStream output = new FileOutputStream(out_file);
                            try {
                                try {
                                    byte[] buffer = new byte[4 * 1024]; // or other buffer size
                                    int read;

                                    while ((read = input.read(buffer)) != -1) {
                                        output.write(buffer, 0, read);
                                    }
                                    output.flush();
                                } finally {
                                    output.close();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                input.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
pDialog.dismiss();
                        Toast.makeText(getContext(), "Restore Complete", Toast.LENGTH_LONG).show();
                                contents.discard(GAC);
                            }
                        }
                    });
        } catch (Exception e) { e.printStackTrace(); }
    }
//
//    private void downloadFromDrive(DriveFile file) {
//        file.open(mGoogleApiClient, DriveFile.MODE_READ_ONLY, null)
//                .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
//                    @Override
//                    public void onResult(DriveApi.DriveContentsResult result) {
//                        if (!result.getStatus().isSuccess()) {
//                            showErrorDialog();
//                            return;
//                        }
//
//                        // DriveContents object contains pointers
//                        // to the actual byte stream
//                        DriveContents contents = result.getDriveContents();
//                        InputStream input = contents.getInputStream();
//
//                        try {
//                            File file = new File(realm.getPath());
//                            OutputStream output = new FileOutputStream(file);
//                            try {
//                                try {
//                                    byte[] buffer = new byte[4 * 1024]; // or other buffer size
//                                    int read;
//
//                                    while ((read = input.read(buffer)) != -1) {
//                                        output.write(buffer, 0, read);
//                                    }
//                                    output.flush();
//                                } finally {
//                                    output.close();
//                                }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        } catch (FileNotFoundException e) {
//                            e.printStackTrace();
//                        } finally {
//                            try {
//                                input.close();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                        Toast.makeText(getApplicationContext(), R.string.activity_backup_drive_message_restart, Toast.LENGTH_LONG).show();
//
//                        // Reboot app
//                        Intent mStartActivity = new Intent(getApplicationContext(), MainActivity.class);
//                        int mPendingIntentId = 123456;
//                        PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
//                        AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
//                        System.exit(0);
//                    }
//                });
//    }

}
