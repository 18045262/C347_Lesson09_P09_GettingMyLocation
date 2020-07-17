package webservices.rp.edu.p09_gettingmylocation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    Button btnStart, btnStop, btnCheck;
    TextView tv;
    String folderLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);


        btnCheck = findViewById(R.id.btnCheck);
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        tv = findViewById(R.id.tv);

//        folderLocation = getFilesDir().getAbsolutePath() + "/Lesson09";
//        final File targetFile = new File(folderLocation, "location.txt");
//        if (targetFile.exists() == false){
//            boolean result = targetFile.mkdir();
//            Log.d("File exists: " , "File is no exists");
//            if (result == true){
//                Log.d("File Read/Write", "Folder created");
//            }
//            else{
//                Log.d("File Read/Write", "Folder created failed");
//            }
//        }
//        else{
//            Log.d("File exists: " , "File is exists");
//        }

        folderLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/P09";
        final File targetFile = new File(folderLocation);
        if (targetFile.exists() == false){
            boolean result = targetFile.mkdir();
            if (result == true) {
                Log.d("File Read/Write", "Folder created");
            }
            else{
                Log.d("File Read/Write", "Folder created failed");
            }
        }




        if (checkPermission() == true) {
            Task<Location> location = client.getLastLocation();
            location.addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null){
                        String output = "Last known location when this Activity started\nLatitude : " + location.getLatitude() + "\n Longitude : " + location.getLongitude();
                        tv.setText(output);
                    } else {
                        tv.setText("No last location record found");
                        Toast.makeText(MainActivity.this, "No last location detected",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else{
            Toast.makeText(getApplicationContext(),"No permission for location",Toast.LENGTH_SHORT).show();
        }





        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, MyService.class);
                startService(i);

                if (checkPermission() == true){

                    final LocationRequest mLocationRequest = LocationRequest.create();
                    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    mLocationRequest.setInterval(10000);
                    mLocationRequest.setFastestInterval(5000);
                    mLocationRequest.setSmallestDisplacement(100);

                    final LocationCallback mLocationCallback = new LocationCallback(){
                        @Override
                        public void onLocationResult(LocationResult locationResult){
                            if (locationResult != null){
                                Location locData = locationResult.getLastLocation();
                                String msg = "New location detected\n" +
                                        "Latitude: " + locData.getLatitude() + "\n" +
                                        "Longitude: " + locData.getLongitude();

                                FileWriter writer = null;
                                try {
                                    writer = new FileWriter(targetFile,true);
                                    writer.write(msg + "\n");
                                    writer.flush();
                                    writer.close();
                                    Log.d("Write stated:","Success to write");

                                } catch (IOException e) {
                                    Log.d("Write stated:","Failed to write!");
                                    e.printStackTrace();
                                }
                                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    };


                }
                else{
                    Toast.makeText(MainActivity.this, "No last location",Toast.LENGTH_SHORT).show();
                }


            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, MyService.class);
                stopService(i);



            }
        });

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(targetFile.exists() == true){
                    String data = "";

                    try {
                        FileReader reader = new FileReader(targetFile);
                        BufferedReader br = new BufferedReader(reader);

                        String line = br.readLine();
                        while (line!= null) {
                            data += line + "\n";
                            line = br.readLine();

                        }
                        br.close();
                        reader.close();
                        tv.setText(data);

                        Toast.makeText(MainActivity.this, data, Toast.LENGTH_LONG).show();
                    } catch (FileNotFoundException e) {
                        Toast.makeText(MainActivity.this,"Failed to read!", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

    }

    private boolean checkPermission(){
        int permissionCheck_Coarse = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCheck_Fine = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck_Coarse == PermissionChecker.PERMISSION_GRANTED
                || permissionCheck_Fine == PermissionChecker.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

}
