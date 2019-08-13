package com.example.passengermodule;

import android.Manifest;

import java.util.*;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.design.widget.*;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.admin.mythemes.Models.*;

import com.firebase.client.Firebase;
import com.firebase.client.Query;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {
    TextView name;
    TextView designation;
    TextView userid;
    String provider;
    Button process, process2;
    public static Integer i = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        name = (TextView) findViewById(R.id.Text1);
        designation = (TextView) findViewById(R.id.Text2);
        userid = (TextView) findViewById(R.id.Text3);
        process2 = (Button) findViewById(R.id.Button2);
        process = (Button) findViewById(R.id.Button1);


        process.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent  intent = new  Intent(MainActivity.this, Login.class);
                startActivity(intent);

            }

        });

        process2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent  intent = new  Intent(MainActivity.this, Registration.class);
                startActivity(intent);
            }
        });

    }

    @SuppressLint("MissingPermission")
    public void getRequiredLocation(){




        Toast.makeText(MainActivity.this,"Exit",Toast.LENGTH_SHORT).show();



    }




    // locationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDsER, 0,0,locationlistener);
                   /* mfusedloactionproviderclient.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);

                                SharedPreferences.Editor edit = sharedPreferences.edit();

                                // userid.setText("I am executing");

                               // latlong.addElement(location.getLatitude());
                               // latlong.addElement(location.getLongitude());
                                edit.clear();
                                edit.putString("Latitude", Double.toString(location.getLatitude()));
                                edit.putString("Longitude", Double.toString(location.getLongitude()));
                                edit.apply();
                            }
                            else
                                userid.setText("I am not   executing");

                        }
                    });
*/






          /*  public void loadDatabase(View view) {
                User u = new User();


            }
*/








    /*public void displayRecord(View View)
    {


        com.google.firebase.database.Query q1=FirebaseDatabase.getInstance().getReference().child("Users").orderByKey().equalTo("44545");

        q1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot singlesnapshot : dataSnapshot.getChildren())
                {
                    u = singlesnapshot.getValue(User.class);
                }
                Log.d("MainActivity", userid.toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }*/
}
