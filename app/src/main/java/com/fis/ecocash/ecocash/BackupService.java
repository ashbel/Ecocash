package com.fis.ecocash.ecocash;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fis.ecocash.ecocash.DataClasses.dbDatabaseHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
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

/**
 * Created by ashbelh on 14/4/2018.
 */

public class BackupService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private MediaPlayer mediaPlayer;
    private GoogleApiClient GAC  ;
    private Button btnSubmit;
    private static final int REQUEST_CODE_RESOLUTION = 3;
    private static final int MY_PERMISSIONS_READ_EXTERNAL_STORAGE = 0;
    SimpleDateFormat format = new SimpleDateFormat("dd_MM_yyyy HH:mm:ss");

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //Toast.makeText(getApplicationContext(), "SONG!!!!", Toast.LENGTH_SHORT).show();

        exportDB(getApplicationContext());

        if (GAC == null) {
            //(account);
            // Create the API client and bind it to an instance variable.
            // We use this instance as the callback for connection and connection
            // failures.
            // Since no account name is passed, the user is prompted to choose.
            GAC = new GoogleApiClient.Builder(getApplicationContext())
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    // Optionally, add additional APIs and scopes if required.
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }

        GAC.connect();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //On destory stop and release the media player
//        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
//            mediaPlayer.stop();
//            mediaPlayer.reset();
//            mediaPlayer.release();
//        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        saveFileToDrive();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        //Toast.makeText(getApplicationContext(), "Backup Failed. Please try again later", Toast.LENGTH_LONG).show();
        //Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
        if (!result.hasResolution()) {
            // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog((Activity) getApplicationContext(), result.getErrorCode(), 0).show();
            return;
        }
//        try {
//            //result.startResolutionForResult((Activity) getApplicationContext(), REQUEST_CODE_RESOLUTION);
//        } catch (IntentSender.SendIntentException e) {
//            //Log.e(TAG, "Exception while starting resolution activity", e);
//        }
    }

    private void saveFileToDrive() {
        //progressBar.setMessage("Sending ...");
        //Log.i("SAV", "Creating new contents.");
        Date date = new Date();
        final String   folder = "ziWallet";
        final String   titl = "ziWallet_"+ format.format(date) +"_Backup";
        final String mime = "application/x-sqlite3";
        final File image = getApplicationContext().getDatabasePath(dbDatabaseHelper.DATABASE_NAME);
        Drive.DriveApi.newDriveContents(GAC)
                .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {

                    @Override
                    public void onResult(DriveApi.DriveContentsResult result) {
                        // If the operation was not successful, we cannot do anything
                        // and must
                        // fail.
                        if (!result.getStatus().isSuccess()) {
                            //Log.i(TAG, "Failed to create new contents.");
                            return;
                        }
                        // Otherwise, we can write our data to the new contents.
                        //Log.i("SVC", "New contents created.");
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
                                                    Toast.makeText(getApplicationContext(), "Backup Completed Successful", Toast.LENGTH_LONG).show();
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

               // Toast.makeText(getApplicationContext(), "Backup Successful " , Toast.LENGTH_LONG).show();
            }
            else{
//                Log.e("BK","Cannot Write SD");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}