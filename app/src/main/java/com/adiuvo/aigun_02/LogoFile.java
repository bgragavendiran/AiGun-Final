package com.adiuvo.aigun_02;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.SparseIntArray;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.content.Intent;
import java.util.Properties;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.ChannelExec;


import java.io.*;


public class LogoFile extends AppCompatActivity{


    Button goNextbutton1;
    ImageButton POWEROFF1;
    static String Command = "";

    String IPADDRESS_RPI="";


    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logo_file);


        goNextbutton1 = (Button) findViewById(R.id.goNext1);
               goNextbutton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(getApplicationContext(), "DataEntryFile -->  ", Toast.LENGTH_SHORT).show();
                Intent MainActivityScreen1 = new Intent(getApplicationContext(), PatientDataEntry.class);
                MainActivityScreen1.putExtra("IP_ADDRESS",IPADDRESS_RPI);
                startActivity(MainActivityScreen1);

            }

        });
        getClientList();
    }

    public static String executeRemoteCommand4 (String username, String password, String hostname,int port)
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
    public void getClientList() {
        int macCount = 0;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;

            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");
                if (splitted != null ) {
                    System.out.println("Line : " + line );
                    // Basic sanity check
                    String mac = splitted[3];
                    System.out.println("Mac : Outside If "+ mac );
                    if (mac.matches("b8:27:eb:df:1d:42")) {
                        //hotSpot.setBackgroundColor(Color.parseColor("#ff0097"));
                        macCount++;
                        System.out.println("Mac : "+ mac + " IP Address : "+splitted[0] );
                        IPADDRESS_RPI=splitted[0];


                    }
                }
            }
        } catch(Exception e) {

        }
    }

}