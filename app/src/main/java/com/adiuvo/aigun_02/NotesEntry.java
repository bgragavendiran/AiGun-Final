package com.adiuvo.aigun_02;

import android.os.Bundle;
import android.os.Environment;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.MotionEvent;
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


public class NotesEntry extends AppCompatActivity{
    final static String path_name= Environment.getExternalStorageDirectory() + "/AIGun_05";
    //final static String path_name= Environment.getExternalStorageDirectory() + "/AIGun_03" + Cowname1;

    Button submit1;
    EditText cowOwner;
    EditText grazingPractice;
    EditText location;
    EditText state;
    EditText district;
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
        setContentView(R.layout.notes_entry);
        Intent intent = getIntent();
        Cowname1 = intent.getStringExtra("cowname");
        Farmname1 = intent.getStringExtra("farmname");
        cowOwner = (EditText) findViewById(R.id.cowOwner);
        grazingPractice = (EditText) findViewById(R.id.grazingPractice);
        location = (EditText) findViewById(R.id.location);
        //state = (EditText) findViewById(R.id.state);
        //district = (EditText) findViewById(R.id.district);
        submit1 = (Button) findViewById(R.id.submitButton);
        goNextbutton = (ImageButton) findViewById(R.id.gonext);
        goNextbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent MainActivityScreen1 = new Intent(getApplicationContext(), NotesEntry1.class);
                MainActivityScreen1.putExtra("cowname", Cowname1);
                MainActivityScreen1.putExtra("farmname", Farmname1);
                startActivity(MainActivityScreen1);
            }
       });



        final Spinner dropdown = findViewById(R.id.spinner1);
        String[] items = new String[]{"Summer","Winter","Spring","Autumn"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        final Spinner dropdown1 = findViewById(R.id.spinner2);
        String[] items1 = new String[]{"Organized","Backyard"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items1);
        dropdown1.setAdapter(adapter1);

        final Spinner dropdown2 = findViewById(R.id.spinner3);
        String[] items2 = new String[]{"Andra Pradesh","Arunachal Pradesh","Assam", "Bihar", "Chhattisgarh", "Goa", "Gujarat","Haryana","Himachal Pradesh","jharkand"
        ,"Karnataka", "Kerala", "Madhya Pradesh", "Maharashtra", "Manipur", "Meghalaya", "Mizoram", "Nagaland", "Odisha", "Punjab", "Rajasthan", "Sikkim", "TamilNadu", "Telangana",
                "Tripura", "Uttar Pradesh", "Uttarkhand", "West Bengal", "Andaman and Nicobar", "Chandigarh", "Dadra", "Jammu & Kashmir", "Ladakh"
        ,"Lakshadweep", "Delhi", "Puducherry"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items2);
        dropdown2.setAdapter(adapter2);

        dropdown2.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if  (dropdown2.getSelectedItem().toString() == "Andra Pradesh") {

                    Log.d("dropdown3" ,dropdown2.getSelectedItem().toString());
                    final Spinner dropdown3 = findViewById(R.id.spinner4);
                    String[] items3 = new String[]{"Anantapur", "Chittor", "East Godhavari", "Guntur", "Kadapa", "Krishna", "Kurnool",
                            "Prakasam", "Nellore", "Srikakulam", "Vishakapatinam", "Vizianagaram", "West Godavari"};
                    ArrayAdapter<String> adapter3 = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, items3);
                    dropdown3.setAdapter(adapter3);
                }
                //return false;
                else if (dropdown2.getSelectedItem().toString() == "Arunachal Pradesh")
                {
                    final Spinner dropdown3 = findViewById(R.id.spinner4);
                    String[] items3 = new String[]{"Anjaw", "Changlang", "East Kameng", "East Sieng", "Kamle", "kra Daadi", "Kurung Kumey"};
                    ArrayAdapter<String> adapter3 = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, items3);
                    dropdown3.setAdapter(adapter3);
                }
                else{
                    final Spinner dropdown3 = findViewById(R.id.spinner4);
                    String[] items3 = new String[]{" ", ""};
                    ArrayAdapter<String> adapter3 = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, items3);
                    dropdown3.setAdapter(adapter3);
                }
                return false;
            }
            });


        submit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                                if (saveFile(Farmname1,Cowname1,"Animal Owner :  " + cowOwner.getText().toString() + " \n" + "Farm size :  " + grazingPractice.getText().toString() + " \n"
                        + "Address :  " + location.getText().toString() +" \n" + "State :  " + dropdown2.getSelectedItem().toString() + " \n"
                        + "District :  " + dropdown2.getSelectedItem().toString() + " \n" + "Season :  " +dropdown.getSelectedItem().toString()+
                        " \n" +"Farming :  " +dropdown1.getSelectedItem().toString() )) {
                    Toast.makeText(NotesEntry.this, "Saved to file", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(NotesEntry.this,"Error save file!!!",Toast.LENGTH_SHORT).show();
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

        String txtfilename = Cowname1 +"_Form1-FD_" + Date + ".txt";
        try {
//            File storeDirectory1 = new File(path_name,Cowname1);
            File storeDirectory_1 = new File(path_name,  Farmname1);
            File storeDirectory1 = new File(storeDirectory_1,  Cowname1);
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








