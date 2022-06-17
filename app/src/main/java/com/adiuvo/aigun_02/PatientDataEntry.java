package com.adiuvo.aigun_02;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
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


public class PatientDataEntry extends AppCompatActivity{

    final static String path_name= Environment.getExternalStorageDirectory() + "/AIGun_05";
    ImageButton goNextbutton, hotSpot;
    Button submit;
    EditText cow_name, farm_name, doctor_name,date,breed_type, Owner_MobNo, Doctor_MobNo;
    String IPADDRESS_RPI="";
    int count = 0;
    static String Command = "";
    String IP_address;

    Button selectDate;
    TextView calDate;
    DatePickerDialog datePickerDialog;
    int year;
    int month;
    int dayOfMonth;
    Calendar calendar;



    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patientdataentry);
        Intent intent4 = getIntent();
        cow_name = (EditText) findViewById(R.id.cowName);
        System.out.print(cow_name.getText().toString());
        farm_name = (EditText) findViewById(R.id.farmName);
        doctor_name = (EditText) findViewById(R.id.doctorName);
        Owner_MobNo = (EditText) findViewById(R.id.OwnMobNo);
        Doctor_MobNo = (EditText) findViewById(R.id.DocMobNo);
        breed_type = (EditText) findViewById(R.id.breedType);
        goNextbutton = (ImageButton) findViewById(R.id.gonext);
        selectDate = findViewById(R.id.btnDate);
        calDate = findViewById(R.id.tvSelectedDate);

        goNextbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PatientDataEntry.this);
                builder.setTitle("Select your answer");
                builder.setMessage("AI was done in last 10 months?");
                builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent MainActivityScreen = new Intent(getApplicationContext(), NotesEntry.class);
                        String cowName = cow_name.getText().toString();
                        MainActivityScreen.putExtra("cowname", cowName);
                        String farmName = farm_name.getText().toString();
                        MainActivityScreen.putExtra("farmname", farmName);
                        startActivity(MainActivityScreen);
                    }
                });
                builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do something when No button clicked
                        //Toast.makeText(getApplicationContext(), "Go back", Toast.LENGTH_SHORT).show();
                        inseminationinfo();
                    }
                });
                AlertDialog dialog = builder.create();
                // Display the alert dialog on interface
                dialog.show();
            }

        });

        final Spinner dropdown1 = findViewById(R.id.spinner2);
        String[] items1 = new String[]{"1", "2", "3","4","5","6","7","8","9","10","11", "12", "13","14","15","16","17","18","19","20","21","22"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items1);
        dropdown1.setAdapter(adapter1);


        final Spinner dropdown2 = findViewById(R.id.spinner3);
        String[] items2 = new String[]{"0","1", "2", "3", "4","5", "6", "7", "8","9","10", "11","12"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items2);
        dropdown2.setAdapter(adapter2);

        final Spinner dropdown3 = findViewById(R.id.spinner4);
        String[] items3 = new String[]{"Insemination", "Imaging"};
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items3);
        dropdown3.setAdapter(adapter3);

       // final Spinner dropdown4 = findViewById(R.id.spinner5);
        //String[] items4 = new String[]{"Female", "Male"};
        //ArrayAdapter<String> adapter4 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items4);
        //dropdown4.setAdapter(adapter4);

        submit = (Button) findViewById(R.id.submitButton);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (saveToFile(farm_name.getText().toString(), cow_name.getText().toString(), "Animal name :  " + cow_name.getText().toString() + " \n" + "Farm name :  " + farm_name.getText().toString() + " \n" + "Owner MOB No :  " + Owner_MobNo.getText().toString() + " \n" + "Doctor name :  " + doctor_name.getText().toString() + "\n" +"Doc MOB No :  " + Doctor_MobNo.getText().toString() + "\n" + "Breed Type :  " + breed_type.getText().toString() +" \n" +
                        "Date :  " + calDate.getText().toString() + "\n" + "Purpose :  " + dropdown3.getSelectedItem().toString()+ "\n" + "Age : Years - " + dropdown1.getSelectedItem().toString() + ' ' + "Months - " + dropdown2.getSelectedItem().toString()
                        + "\n" )){
                    Toast.makeText(PatientDataEntry.this,"Saved to file",Toast.LENGTH_SHORT).show();
               }else{
                    Toast.makeText(PatientDataEntry.this,"Error save file!!!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(PatientDataEntry.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                calDate.setText(day + "/" + (month + 1) + "/" + year);
                            }
                        }, year, month, dayOfMonth);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });
    }
    public boolean saveToFile(String farmName, String cowName, String data){
        Calendar c = Calendar.getInstance();
        System.out.println("Current time = &gt; " + c.getTime());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String Date = df.format(c.getTime());
        String txtfilename =  cowName +"_Info_" + Date + ".txt";
        try {
          //  File storeDirectory1 = new File(path_name,  cowName);
            File storeDirectory_1 = new File(path_name , farmName);
            File storeDirectory1 = new File(storeDirectory_1 , cowName);
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

    private void inseminationinfo(){
        AlertDialog.Builder builder = new AlertDialog.Builder(PatientDataEntry.this);
        builder.setTitle("Select your answer");
        builder.setMessage("Already inseminated by same device?");
        builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent MainActivityScreen = new Intent(getApplicationContext(), NotesEntry.class);
                String cowName = cow_name.getText().toString();
                MainActivityScreen.putExtra("cowname", cowName);
                String farmName = farm_name.getText().toString();
                MainActivityScreen.putExtra("farmname", farmName);
                startActivity(MainActivityScreen);
            }
        });
        builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Get patient ID", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog dialog = builder.create();
        // Display the alert dialog on interface
        dialog.show();
    }

}

