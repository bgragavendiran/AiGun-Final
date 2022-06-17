package com.adiuvo.aigun_02;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.content.Intent;
import java.util.Properties;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
//import com.google.firebase.appcheck.FirebaseAppCheck;
//import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.ChannelExec;

import java.util.Scanner;
import java.util.UUID;
import android.content.Intent;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import java.io.*;
import java.net.*;


public class NotesEntry2 extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 200;
    final static String path_name= Environment.getExternalStorageDirectory() + "/AIGun_05";
    //final static String path_name= Environment.getExternalStorageDirectory() + "/AIGun_03" + Cowname1;
    String TAG="NotesEntry2.java";
    Button submit1;
    EditText nF;
    String Cowname1;
    String Farmname1;
    ImageButton goNextButton;
    TextView textView;
    String src;
    String src1;
    String dst;
    FirebaseStorage storage=FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    ProgressDialog progressDialog;



    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_entry_2);
        // use ActionBar utility methods
//        ActionBar actionBar = getSupportActionBar();
//
//         providing title for the ActionBar
//        actionBar.setTitle("AI Gun");
//        actionBar.setSubtitle("Form3-Pregnancy and General details");
//         providing subtitle for the ActionBar
//
//         adding icon in the ActionBar
//        actionBar.setIcon(R.mipmap.ic_launcher_foreground);
//
//         methods to display the icon in the ActionBar
//        actionBar.setDisplayUseLogoEnabled(true);
//        actionBar.setDisplayShowHomeEnabled(true);
        Intent intent = getIntent();
        Cowname1 = intent.getStringExtra("cowname");
        Farmname1 = intent.getStringExtra("farmname");
        nF = (EditText) findViewById(R.id.nF);
        submit1 = (Button) findViewById(R.id.submitButton);


        String src = Environment.getExternalStorageDirectory().getPath()
                + "/Depstech/Video";
        String src1 = Environment.getExternalStorageDirectory().getPath()
                + "/Depstech/Picture";
        String dst = Environment.getExternalStorageDirectory().getPath()
                + "/AIGun_05";

        goNextButton = findViewById(R.id.goNextbutton);
        goNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepareDepstechDirectories();
                launchDepstech();
            }
        });

        // textView = findViewById(R.id.goNextTextview);
        //textView.setOnClickListener(new View.OnClickListener() {
        //  @Override
        //  public void onClick(View v) {

        try {
            copy(src, dst);
            copy1(src1, dst);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //  }
        //});


        final Spinner dropdown1 = findViewById(R.id.spinner2);
        String[] items1 = new String[]{"Positive","Negative"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items1);
        dropdown1.setAdapter(adapter1);

        submit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (saveFile(Farmname1, Cowname1,"Pregnancy Diagnosis :  " + dropdown1.getSelectedItem().toString()+ " \n" + "Notes and Feedbacks :  " + nF.getText().toString() + " \n")) {
                    Toast.makeText(NotesEntry2.this, "Saved to file", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onClick: Start Aggregation");
                    aggregateAllFiles(Farmname1,Cowname1);
                }
                else{
                    Toast.makeText(NotesEntry2.this,"Error save file!!!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {

                boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                if (locationAccepted && cameraAccepted) {
                    Log.d(TAG, "onRequestPermissionsResult: Got all sorta auth Homie");
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    private void prepareDepstechDirectories() {
        File depthstechRootDir = new File(Environment.getExternalStorageDirectory() + File.separator + "Depstech" + File.separator + "Video");
        File depthstechRootDir1 = new File(Environment.getExternalStorageDirectory() + File.separator + "Depstech" + File.separator + "Picture");
        try {
            deleteAllDirectories(depthstechRootDir);
            deleteAllDirectories1(depthstechRootDir1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        createNewDepstechDirectory();
    }

    /**
     * Creates necessary depstech dir with sub dir
     */
    private void createNewDepstechDirectory() {
        File depthstechRootDir = new File(Environment.getExternalStorageDirectory() + File.separator + "Depstech" + File.separator + "Video");
        File depthstechRootDir1 = new File(Environment.getExternalStorageDirectory() + File.separator + "Depstech" + File.separator + "Picture");
        if(!depthstechRootDir.exists()){
            depthstechRootDir.mkdirs();
        }
        if(!depthstechRootDir1.exists()){
            depthstechRootDir1.mkdirs();
        }
    }

    /**
     *
     */
    private void launchDepstech() {
        Intent intent = new Intent();
        intent.setClassName("com.ipotensic.depstech", "com.logan.idepstech.CameraActivity");
        if(getPackageManager().resolveActivity(intent, 0) != null) {
            startActivity(intent);
        } else {
            startDownloadOfAPK(this);
            Toast.makeText(getApplicationContext(), "No app installed that can perform this action", Toast.LENGTH_SHORT).show();

        }
    }

    private void startDownloadOfAPK(final Activity activity) {
        progressDialog=  new ProgressDialog(activity);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage("Downloading AIgun Camera Plugin");
        progressDialog.show();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://aigun-734fd.appspot.com/global_data/plugin_aigun_camera_2_0.apk");
        //StorageReference pathReference = storageRef.child("global_data/plugin_aigun_camera.apk");
        try {

            final File localFile = new File(String.valueOf(Environment.getExternalStorageDirectory())+ File.separator+"AIGUN_05" + File.separator).createTempFile("aigunPlugin", "apk");

            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Local temp file has been created
                    Log.d(TAG, "onSuccess: Downloaded Apk");
                    killProg();
                    Intent install = new Intent(Intent.ACTION_VIEW);
                    install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    Uri apkURI = FileProvider.getUriForFile(
                            getApplicationContext(),
                            activity.getApplicationContext()
                                    .getPackageName() + ".provider", localFile);
                    install.setDataAndType(apkURI, "application/vnd.android.package-archive");
                    install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(install);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    Log.d(TAG, "onFailed: "+exception);
                }
            }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    //calculating progress percentage
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    //displaying percentage in progress dialog
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    setProg(((int) progress));
                    Log.d(TAG, "onProgress: "+progress);
                }
            });;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void killProg() {
        progressDialog.dismiss();
    }

    private void setProg(int progress) {
        progressDialog.setMessage("Downloading Ai Gun Camera Plugin App");
        progressDialog.setProgress(progress);
    }

    /**
     * Deletes all sub dir with dir
     * @param f
     * @throws IOException
     */
    private void deleteAllDirectories(File f) throws IOException {
        if (f.isDirectory()) {
            Log.d(TAG, "deleteAllDirectories: "+f.length());
            if(f.listFiles()!=null){
                for (File c : f.listFiles())
                    c.delete();
            }
        }
        f.delete();
        if (!f.delete())
            throw new FileNotFoundException("Failed to delete file: " + f);
    }

    private void deleteAllDirectories1(File f) throws IOException {
        if (f.isDirectory()) {
            for (File c : f.listFiles())
                c.delete();
        }
        f.delete();
        if (!f.delete())
            throw new FileNotFoundException("Failed to delete file: " + f);
    }

    public static void copy(String src, String dst) throws IOException {
        InputStream in = new FileInputStream(src);
        try {
            OutputStream out = new FileOutputStream(dst);
            try {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }

    public static void copy1(String src1, String dst) throws IOException {
        InputStream in = new FileInputStream(src1);
        try {
            OutputStream out = new FileOutputStream(dst);
            try {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }
    public boolean aggregateAllFiles(String Farmname1, String Cowname1){
        try {
            File storeDirectory_1 = new File(path_name,Farmname1);
            File storeDirectory1 = new File(storeDirectory_1, Cowname1);
            File[] files = storeDirectory1.listFiles();
            if (!storeDirectory1.exists()) {
                storeDirectory1.mkdirs();
            }
            File aggregator=new File(path_name,"aggregate.csv");

            if (!aggregator.exists()) {
                String CSVfileHeader="";
                String CSVfileData="";
                aggregator.createNewFile();
                for(File x:files){
                    Scanner myReader = new Scanner(x);
                    while (myReader.hasNextLine()) {
                        String arr[]=new String[50];
                        String data = myReader.nextLine();
                        data = data.replaceAll("^ | $|\\n ", "");
                        arr = data.split(":");
                        if(arr.length>1){
                            String temp="";
                            Log.d(TAG, "aggregateAllFiles: "+arr[1]);
                            temp=arr[0].replaceAll("^ | $|\\n ", "");
                            CSVfileHeader+=temp+",";
                            temp=arr[1].replaceAll("^ | $|\\n ", "");
                            CSVfileData+=temp+",";
                        }

                    }
                    myReader.close();
                }
                FileWriter fw = new FileWriter(aggregator.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(CSVfileHeader.substring(0,CSVfileHeader.length()-1)+"\n");
                bw.write(CSVfileData.substring(0,CSVfileData.length()-1)+"\n");
                bw.close();
            }else{
                String CSVfileData="";
                for(File x:files){
                    Scanner myReader = new Scanner(x);
                    while (myReader.hasNextLine()) {
                        String arr[]=new String[50];
                        String data = myReader.nextLine();
                        data = data.replaceAll("^ | $|\\n ", "");
                        arr = data.split(":");
                        if(arr.length>1){
                            String temp="";

                            Log.d(TAG, "aggregateAllFiles: "+arr[1]);
                            temp=arr[1].replaceAll("^ | $|\\n ", "");
                            CSVfileData+=temp+",";

                        }

                    }
                    myReader.close();
                }
                FileWriter fw = new FileWriter(aggregator.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(CSVfileData.substring(0,CSVfileData.length()-1)+"\n");
                bw.close();
            }


            if (files != null)
            {
                System.out.println("Files.length" +files.length);
//                FileOutputStream fileOutputStream = new FileOutputStream(new File(storeDirectory1, (files.length + "."+ txtfilename)));
//                fileOutputStream.write((data + System.getProperty("line.separator")).getBytes());
            }

            return true;
        }  catch(FileNotFoundException ex) {
            Log.d("saveToFile", ex.getMessage());
        }  catch(IOException ex) {
            Log.d("saveToFile", ex.getMessage());
        }
        return false;
    }



    public boolean saveFile(String Farmname1, String Cowname1,String data){
        int num = 0;
        Calendar c = Calendar.getInstance();
        System.out.println("Current time = &gt; " + c.getTime());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String Date = df.format(c.getTime());

        String txtfilename = Cowname1 +"_Form3-PD_" + Date + ".txt";
        try {
            File storeDirectory_1 = new File(path_name,Farmname1);
            File storeDirectory1 = new File(storeDirectory_1, Cowname1);
            if (!storeDirectory1.exists()) {
                storeDirectory1.mkdirs();
            }

            File[] files = storeDirectory1.listFiles();

            if (files != null)
            {
                System.out.println("Files.length" +files.length);
                FileOutputStream fileOutputStream = new FileOutputStream(new File(storeDirectory1, (files.length + "."+ txtfilename)));
                fileOutputStream.write((data + System.getProperty("line.separator")).getBytes());
            }

            return true;
        }  catch(FileNotFoundException ex) {
            Log.d("saveToFile", ex.getMessage());
        }  catch(IOException ex) {
            Log.d("saveToFile", ex.getMessage());
        }
        return  false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        moveDataFromDepstechToAIGun();
    }

    /**
     * Copies and renames data from Depstech folder to AI Gun folder
     * Files are renamed based on cow name
     * Depstech folder is deleted post the above operations
     */
    private void moveDataFromDepstechToAIGun() {
        File aiGunRootDir = new File(Environment.getExternalStorageDirectory() + File.separator + "AIGUN_05" + File.separator +  Farmname1 + File.separator +Cowname1);
        File depthstechRootDir = new File(Environment.getExternalStorageDirectory() + File.separator + "Depstech" + File.separator + "Video");
        File depthstechRootDir1 = new File(Environment.getExternalStorageDirectory() + File.separator + "Depstech" + File.separator + "Picture");

        if(!aiGunRootDir.exists()){
            aiGunRootDir.mkdirs();
        }
        if(depthstechRootDir.exists()){
            File[] files = depthstechRootDir.listFiles();
            if(files!=null){
                for(File f : files){
                    File destFile =new File(aiGunRootDir + File.separator + Cowname1  +"_" + f.getName());
                    try {
                        copy(f.getAbsolutePath(), destFile.getAbsolutePath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    deleteAllDirectories(depthstechRootDir);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                File depthstechRoot = new File(Environment.getExternalStorageDirectory() + File.separator + "Depstech");
                depthstechRoot.delete();
            }

        }
        if(depthstechRootDir1.exists()){
            File[] files = depthstechRootDir1.listFiles();
            if(files!=null){
                for(File f : files){
                    File destFile =new File(aiGunRootDir + File.separator + Cowname1  +"_" + f.getName());
                    try {
                        copy(f.getAbsolutePath(), destFile.getAbsolutePath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    deleteAllDirectories(depthstechRootDir1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                File depthstechRoot = new File(Environment.getExternalStorageDirectory() + File.separator + "Depstech");
                depthstechRoot.delete();
            }

        }
    }
}







