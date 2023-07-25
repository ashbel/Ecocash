package com.fis.ecocash.ecocash;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.fis.ecocash.ecocash.DataClasses.dbDatabaseHelper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi.DriveContentsResult;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.MetadataChangeSet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ManualFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private ProgressDialog progressBar;
    private GoogleApiClient GAC  ;
    private RadioGroup radioGroup;
    private Button btnSubmit;
    private static final int REQUEST_CODE_RESOLUTION = 3;
    private static final int MY_PERMISSIONS_READ_EXTERNAL_STORAGE = 0;
    private static final String TAG = "drive-quickstart";
    SimpleDateFormat format = new SimpleDateFormat("dd_MM_yyyy HH:mm:ss");
    protected Typeface mTfRegular;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_manual, container, false);
        mTfRegular = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Regular.ttf");
        TextView tv = (TextView) view.findViewById(R.id.dest_text);
        TextView tv1 = (TextView) view.findViewById(R.id.dest_rec);
        TextView tv2 = (TextView) view.findViewById(R.id.textView2);
        tv.setTypeface(mTfRegular, Typeface.BOLD);
        tv1.setTypeface(mTfRegular, Typeface.BOLD);
        tv2.setTypeface(mTfRegular, Typeface.BOLD);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getActivity());
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();

            Log.e(TAG,  personEmail);
        }
        if (GAC == null) {
            //(account);
            // Create the API client and bind it to an instance variable.
            // We use this instance as the callback for connection and connection
            // failures.
            // Since no account name is passed, the user is prompted to choose.
            GAC = new GoogleApiClient.Builder(getContext())
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    // Optionally, add additional APIs and scopes if required.
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }


        radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);

        btnSubmit = (Button) view.findViewById(R.id.button);

        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // get selected radio button from radioGroup
                int selectedId = radioGroup.getCheckedRadioButtonId();

                switch(selectedId){
                    case R.id.radioDevice:
                        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_READ_EXTERNAL_STORAGE);
                        }
                        else {
                            exportDB(getContext());
                        }
                        break;
                    case R.id.radioDrive:
                        GAC.connect();
                        break;
                }
//0737804161
            }

        });

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    exportDB(getContext());
                } else {

                    Toast.makeText(getContext(), "Backup Requires this Permission", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    public void importDB(Context context) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            if (sd.canWrite()) {
                File backupDB = context.getDatabasePath(dbDatabaseHelper.DATABASE_NAME);
                String backupDBPath = String.format("%s.bak", dbDatabaseHelper.DATABASE_NAME);
                File currentDB = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();

                Toast.makeText(getContext(), "Import Successful", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void exportDB(Context context) {
        Date date = new Date();
        final String   titl = "ziWallet_"+ format.format(date) +"_Backup";
        try {
            File sd = new File(Environment.getExternalStorageDirectory(),"/ZWallet");
            if(!sd.exists()){
                sd.mkdir();
            }
            File data = Environment.getDataDirectory();
//            Log.e("BK","Start backup");
            if (sd.canWrite()) {

//                Log.e("BK","Can Write SD");
                String backupDBPath = titl;
                File currentDB = context.getDatabasePath(dbDatabaseHelper.DATABASE_NAME);
                File backupDB = new File(sd, backupDBPath);
//                Log.e("PTH",backupDBPath);
                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();

                Toast.makeText(getContext(), "Backup Successful " , Toast.LENGTH_LONG).show();
            }
            else{
//                Log.e("BK","Cannot Write SD");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static boolean file2Os(OutputStream os, File file) {
        boolean bOK = false;
        InputStream is = null;
        if (file != null && os != null) try {
            byte[] buf = new byte[4096];
            is = new FileInputStream(file);
            int c;
            while ((c = is.read(buf, 0, buf.length)) > 0)
                os.write(buf, 0, c);
            bOK = true;
        } catch (Exception e) {e.printStackTrace();}
        finally {
            try {
                os.flush(); os.close();
                if (is != null )is.close();
            } catch (Exception e) {e.printStackTrace();}
        }
        return  bOK;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

       //Log.i(TAG, "API client connected.");
       // progressBar.setMessage("Connected ...");
        saveFileToDrive();

    }

    @Override
    public void onConnectionSuspended(int i) {
       // Log.i(TAG, "GoogleApiClient connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {

        //Toast.makeText(getContext(), "Backup Failed. Please try again later", Toast.LENGTH_LONG).show();
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

    private void saveFileToDrive() {
        //progressBar.setMessage("Sending ...");
        //Log.i("SAV", "Creating new contents.");
        Date date = new Date();
        final String   folder = "ziWallet";
        final String   titl = "ziWallet_"+ format.format(date) +"_Backup";
        final String mime = "application/x-sqlite3";
        final File image = getContext().getDatabasePath(dbDatabaseHelper.DATABASE_NAME);
        Drive.DriveApi.newDriveContents(GAC)
                .setResultCallback(new ResultCallback<DriveContentsResult>() {

                    @Override
                    public void onResult(DriveContentsResult result) {
                        // If the operation was not successful, we cannot do anything
                        // and must
                        // fail.
                        if (!result.getStatus().isSuccess()) {
                            //Log.i(TAG, "Failed to create new contents.");
                            return;
                        }
                        // Otherwise, we can write our data to the new contents.
                        //Log.i(TAG, "New contents created.");
                        // Get an output stream for the contents.
                        if (result.getStatus().isSuccess()){
                            DriveContents cont = result.getDriveContents();
                            if (cont != null && file2Os(cont.getOutputStream(), image)) {
                                //Log.e("IN", "im in in in" );

                                MetadataChangeSet meta = new MetadataChangeSet.Builder().setTitle(titl).setMimeType(mime).build();
                                Drive.DriveApi.getRootFolder(GAC).createFile(GAC, meta, cont).setResultCallback(
                                        new ResultCallback<DriveFolder.DriveFileResult>() {
                                            @Override
                                            public void onResult(@NonNull DriveFolder.DriveFileResult fileRslt) {
                                                if (fileRslt.getStatus().isSuccess()) {
                                                    fileRslt.getDriveFile();   //BINGO !!!
                                                   // progressBar.setMessage("Successful ...");
                                                    Toast.makeText(getContext(), "Backup Completed Successful", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        }
                                );
                            }
                        }
                    }
                });
       // progressBar.dismiss();
    }


}
