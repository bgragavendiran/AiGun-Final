package com.adiuvo.aigun_02;

import android.os.Bundle;
import android.os.Environment;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import android.widget.Spinner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class NotesEntry1 extends AppCompatActivity{
    final static String path_name= Environment.getExternalStorageDirectory() + "/AIGun_05";
    //final static String path_name= Environment.getExternalStorageDirectory() + "/AIGun_03" + Cowname1;

    Button submit1;
    EditText semenStraw;
    EditText batch;
    EditText Medications;
    EditText pdDate;
    EditText medications;
    ImageButton goNextbutton;


    String Cowname1;
    String Farmname1;

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_entry_1);
        Intent intent = getIntent();
        Cowname1 = intent.getStringExtra("cowname");
        Farmname1 = intent.getStringExtra("farmname");


        semenStraw= (EditText) findViewById(R.id.semenStraw);
        batch = (EditText) findViewById(R.id.batch);
        Medications = (EditText) findViewById(R.id.medications);
        pdDate = (EditText) findViewById(R.id.pdDate);
        medications = (EditText) findViewById(R.id.medications);
        submit1 = (Button) findViewById(R.id.submitButton);

        final Spinner dropdown1 = findViewById(R.id.spinner1);
        String[] items1 = new String[]{"Others"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items1);
        dropdown1.setAdapter(adapter1);

        final Spinner dropdown2 = findViewById(R.id.spinner2);
        String[] items2 = new String[]{"Others"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items2);
        dropdown2.setAdapter(adapter2);


        goNextbutton = (ImageButton) findViewById(R.id.gonext);
        goNextbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent MainActivityScreen2 = new Intent(getApplicationContext(), NotesEntry2.class);
                MainActivityScreen2.putExtra("cowname", Cowname1);
                MainActivityScreen2.putExtra("farmname",Farmname1);
                startActivity(MainActivityScreen2);
            }
        });

        submit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (saveFile(Farmname1, Cowname1,"Semen Straw Details :  " + semenStraw.getText().toString() + " \n" + "Batch :  " + batch.getText().toString() + " \n"
                        + "Oestrous Sign :  " + dropdown1.getSelectedItem().toString() +" \n" + "Proposed PD Date :  " + pdDate.getText().toString() + " \n"
                        + "Medications :  " + dropdown2.getSelectedItem().toString() )) {
                    Toast.makeText(NotesEntry1.this, "Saved to file", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(NotesEntry1.this,"Error save file!!!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
   public boolean saveFile(String Farmname1, String Cowname1,String data){

        int num = 0;
        Calendar c = Calendar.getInstance();
        System.out.println("Current time = &gt; " + c.getTime());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String Date = df.format(c.getTime());

        String txtfilename = Cowname1 +"_Form2-AI_" + Date + ".txt";
        try {
            File storeDirectory_1 = new File(path_name,Farmname1);
            File storeDirectory1 = new File( storeDirectory_1, Cowname1);
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



}







