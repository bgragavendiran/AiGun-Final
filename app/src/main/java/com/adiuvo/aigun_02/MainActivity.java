package com.adiuvo.aigun_02;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.content.Intent;
import java.util.Properties;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.ChannelExec;


import java.io.File;
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

public class MainActivity extends AppCompatActivity {
    //final static String txtfilename = "Data.txt";
    ProgressDialog mDialog;
    WebView mWebView;
    Button btnPlayPause;
    Button Play;

    ImageButton REBOOT;
    ImageButton HOTSPOT;
    Button CAPTURE;
    Button PAUSE;
    Button PLAY;
    BluetoothAdapter BA;
    BluetoothDevice thedevice = null;
    BluetoothSocket thesocket = null;
    ImageView NotesEntry;
    int Date = 0;
    OutputStream out = null;
    int main = 1;
    String Cowname;
    String IP_address;
    String IP_address1;
    private bgDownloadAsync task;


    //private String videoURL = "http://192.168.43.29:8081/";
    private String videoURL = "";
    public boolean isPlaying = false;
    static String Command = "";

   // URLUtil.isValidUrl(url)

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        Cowname = intent.getStringExtra("cowname");
        Intent intent1 = getIntent();
        IP_address = intent1.getStringExtra("IP_ADDRESS");
        Intent intent2 = getIntent();
        IP_address1 = intent2.getStringExtra("IP_ADDRESS1");
        videoURL="http://"+ IP_address +":8081/";

        System.out.println("Ip_address:" +IP_address);
        System.out.println("Ip_address1:" +IP_address1);
        System.out.println("videoURL:" +videoURL);

        //WEbview
        mWebView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        //Webview performance

        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        mWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);

        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        if (Build.VERSION.SDK_INT >= 19) {
            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        else {
            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);

        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setSupportMultipleWindows(true);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setBuiltInZoomControls(true);


        NotesEntry = (ImageView) findViewById(R.id.notesEntry);
        PAUSE = (Button) findViewById(R.id.Pause);
        PLAY = (Button)findViewById(R.id.Play);
        PLAY.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AsyncTask<Integer, Void, Void>() {
                        @Override
                        protected Void doInBackground(Integer... params) {
                            try {
                                Command = "sudo motion";
                                executeRemoteCommand5("pi", "raspberry", IP_address, 22);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return null;
                        }
                    }.execute(1);
                    Toast.makeText(getApplicationContext(), "will be played", Toast.LENGTH_SHORT).show();
                    if (!isPlaying) {
                       try {
                            if (mWebView != null) {
                                mWebView.loadUrl(videoURL);
  //                              mWebView.onResume();
                                isPlaying = true;
                                Calendar c = Calendar.getInstance();
                                System.out.println("Current time = &gt; " + c.getTime());
                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String Date = df.format(c.getTime());
                                String name = Cowname + "_Video_" + Date + ".mp4";
                                String Video_DIRECTORY = Environment.getExternalStorageDirectory() + "/AIGun_05";
                                File storeDirectory = new File(Video_DIRECTORY, Cowname);
                                try {
                                    if (!storeDirectory.exists()) {
                                        storeDirectory.mkdirs();
                                    }
                                    bgDownloadAsync task = new bgDownloadAsync();
                                    task.execute("");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                mWebView.onPause();
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            Toast.makeText(getApplicationContext(), "exception " + ex.toString(), Toast.LENGTH_SHORT).show();
                        }

                    } else {
                    }
                }
        });


        PAUSE.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // isPlaying = false;
                // mWebView.destroy();
                new AsyncTask<Integer, Void, Void>() {
                    @Override
                    protected Void doInBackground(Integer... params) {
                        try {
                            Command = "sudo pkill motion";
                            executeRemoteCommand5("pi", "raspberry", IP_address, 22);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                }.execute(1);
                Toast.makeText(getApplicationContext(), "will be paused", Toast.LENGTH_SHORT).show();
                if (isPlaying) {
                    try {

                            mWebView.destroy();
                            isPlaying=false;
                            //mWebView.onPause();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Toast.makeText(getApplicationContext(), "exception " + ex.toString(), Toast.LENGTH_SHORT).show();
                    }

                } else {
                }


            }
        });
       NotesEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent MainActivityScreen2 = new Intent(getApplicationContext(), NotesEntry.class);
                MainActivityScreen2.putExtra("cowname", Cowname);
                startActivity(MainActivityScreen2);
                }
        });


        }
    public static String executeRemoteCommand5 (String username, String password, String hostname,int port)
            throws Exception {
        JSch jsch = new JSch();
        Session session = jsch.getSession(username, hostname, port);
        session.setPassword(password);

        // Avoid asking for key confirmation
        Properties prop = new Properties();
        prop.put("StrictHostKeyChecking", "no");
        session.setConfig(prop);
        session.connect();
        System.out.println("session connected.....");

        // SSH Channel
        ChannelExec channelssh = (ChannelExec)
                session.openChannel("exec");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        channelssh.setOutputStream(baos);

        // Execute command
        channelssh.setCommand(Command);
        channelssh.connect();
        channelssh.disconnect();
        return baos.toString();
    }

       private class bgDownloadAsync extends AsyncTask<String, String, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }
            @Override
            protected String doInBackground(String... strings) {
              downloadfile(videoURL);
                return null;
            }
            }
        private void downloadfile (String vidurl){
            int num = 0;
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = sd.format(new Date());
            String name =  Cowname + "_Video_" + date + ".mp4";
            String txtfilename = "Data.txt";
            String Video_DIRECTORY = Environment.getExternalStorageDirectory() + "/AIGun_05";
            File storeDirectory = new File(Video_DIRECTORY, Cowname);
            try {
                Log.d("AIGUN", "trying to write in downloadFile method " + "start");
                URL url = new URL(vidurl);
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("GET");
                c.setDoOutput(true);
                c.connect();
                File[] files = storeDirectory.listFiles();
                if (files != null)
                {
                    System.out.println("Files.length" +files.length);
                    FileOutputStream f = new FileOutputStream(new File(storeDirectory,(files.length + "."+name)));
                    InputStream in = c.getInputStream();
                    byte[] buffer = new byte[1024];
                    int len1 = 0;

                    while ((len1 = in.read(buffer)) > 0 && (isPlaying)){
                            Log.d("AIGUN", "writing in downloadFile method " + buffer);
                            f.write(buffer, 0, len1);
                        }
                    f.flush();
                    f.close();
                }
            } catch (IOException e) {
                Log.d("Error....", e.toString());
            }
        }

   }
