package com.example.admin.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Homepage extends AppCompatActivity  implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {



    Destination destination;
    EditText PassengerId;
    Button submitbutton;
    String addr = new String("null"), requesting_passenger_id=new String("null");
    int removelistenerstatus=0;

    LocationRequest locationrequest;
    GoogleApiClient googleapiclient;
    Location currloc = null;
     int bookride=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requesting_passenger_id=new String("null");
        addr = new String("null");
        setContentView(R.layout.homepage_resource);

        PassengerId = findViewById(R.id.Text1);
        submitbutton = findViewById(R.id.Button1);


        googleapiclient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();



        locationrequest = new LocationRequest()
                .setInterval(15*1000)
                .setFastestInterval(5000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        getRequestingPassengers();

        getMyLocationTracker();



        submitbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                storePassengerId();
                getRequestedLocationfromDatabase();
            }
        });

    }

    public void getMyLocationTracker()
    {


    }


    public void getRequestingPassengers()
    {

        SharedPreferences sp = getSharedPreferences(getPackageName()+"owneridfile", Context.MODE_PRIVATE);

        FirebaseDatabase.getInstance().getReference().child("OwnerDatabase").child(sp.getString("OwnerId",null)).child("requesting_passenger").addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            requesting_passenger_id=dataSnapshot.getValue(String.class);
            if(removelistenerstatus==1)
            {

                SharedPreferences sp = getSharedPreferences(getPackageName()+"owneridfile", Context.MODE_PRIVATE);

                FirebaseDatabase.getInstance().getReference().child("OwnerDatabase").child(sp.getString("OwnerId",null)).child("requesting_passenger").removeEventListener(this);
            }
            if(!requesting_passenger_id.equals("null"))
            {
                passengerRequestDialog("Ride request" , "Accept", "Ignore");
            }

    }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {




    }
    });


}

    protected void onStart() {
        super.onStart();
        googleapiclient.connect();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(Homepage.this,"failure loaction",Toast.LENGTH_LONG).show();

    }

    @Override
    public void onLocationChanged(Location location) {


        SharedPreferences sp = getSharedPreferences(getPackageName()+"owneridfile", Context.MODE_PRIVATE);
        Toast.makeText(Homepage.this,"Tracking loaction",Toast.LENGTH_LONG).show();

        FirebaseDatabase.getInstance().getReference().child("OwnerDatabase").child(sp.getString("OwnerId",null)).child("currentloclat").setValue(location.getLatitude());

        FirebaseDatabase.getInstance().getReference().child("OwnerDatabase").child(sp.getString("OwnerId",null)).child("currentloclong").setValue(location.getLongitude());


    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {


        onRequestLocation();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public void onRequestLocation()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 10);
            return;
        } else {

            LocationServices.FusedLocationApi.requestLocationUpdates(googleapiclient, locationrequest, this);
        }
    }
    private void storePassengerId() {

        SharedPreferences sp1 = getSharedPreferences(getPackageName() + "passengeridfile", Context.MODE_PRIVATE);

        SharedPreferences.Editor edit = sp1.edit();
        edit.putString("Passenger id", PassengerId.getText().toString());
        edit.apply();

    }

    public void updatePassengerData()
    {
        SharedPreferences sp = getSharedPreferences(getPackageName()+"owneridfile", Context.MODE_PRIVATE);


        FirebaseDatabase.getInstance().getReference().child("PassengerDatabase").child(PassengerId.getText().toString()).child("assossiated_owner").setValue(sp.getString("OwnerId",null));

        FirebaseDatabase.getInstance().getReference().child("OwnerDatabase").child(sp.getString("OwnerId",null)).child("ishired").setValue(true);

        switchtoMapsActivity();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    public void switchtoMapsActivity()
    {
        googleapiclient.disconnect();

        removelistenerstatus=1;
        Intent intent =new Intent(Homepage.this, MapsActivity.class);

        finish();
        startActivity(intent);

    }

    public void getRequestedLocationfromDatabase()
    {
        FirebaseDatabase.getInstance().getReference().child("DestinationLocation").child(PassengerId.getText().toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.child("lat").exists())
                    {
                        bookride=1;
                        SharedPreferences sp=getSharedPreferences(getPackageName()+"passengerlatlng",MODE_PRIVATE);
                        SharedPreferences.Editor edit = sp.edit();
                        String lat= Double.toString( dataSnapshot.child("lat").getValue(Double.class));
                        String longi= Double.toString( dataSnapshot.child("longi").getValue(Double.class));
                        edit.putString("lat",lat);
                        edit.putString("longi",longi);
                        edit.apply();

                    }
                    else {

                        SharedPreferences sp=getSharedPreferences(getPackageName()+"passengerlatlng",MODE_PRIVATE);
                        SharedPreferences.Editor edit = sp.edit();
                        edit.putString("lat","null");
                        edit.putString("longi","null");
                        edit.apply();

                        destination = dataSnapshot.getValue(Destination.class);
                        addr = destination.returnDestinationAddress();

                    }
                FirebaseDatabase.getInstance().getReference().child("DestinationLocation").child(PassengerId.getText().toString()).removeEventListener(this);
                         proceedData();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {




            }
        });


    }


    public void proceedData()
    {
        if(addr.equals("null")&&bookride==0)
        {
            showDialogBox("Incorrect Passenger Id","OK");
        }
        SharedPreferences sp = getSharedPreferences(getPackageName()+"addressfile", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.clear();

        edit.putString("Address",addr);
        Toast.makeText(Homepage.this,addr,Toast.LENGTH_SHORT).show();

        edit.apply();
        updatePassengerData();
    }


    public void showDialogBox(String message, String positivetext)
    {
        new AlertDialog.Builder(this).setMessage(message)
                .setPositiveButton(positivetext, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        Intent intent = new Intent(Homepage.this, Homepage.class);
                        Homepage.this.finish();
                        startActivity(intent);

                    }
                }).show();

    }


    public void passengerRequestDialog(String message, String positivetext, String negativetext)
    {
        new AlertDialog.Builder(this).setMessage(message)
                .setPositiveButton(positivetext, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        PassengerId.setText(requesting_passenger_id);

                    }
                }).setNegativeButton(negativetext, new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();

                           SharedPreferences sp = getSharedPreferences(getPackageName()+"owneridfile", Context.MODE_PRIVATE);

                           FirebaseDatabase.getInstance().getReference().child("OwnerDatabase").child(sp.getString("OwnerId",null)).child("requesting_passenger").setValue("null");

                           Intent intent = new Intent(Homepage.this, Homepage.class);
                           Homepage.this.finish();
                           startActivity(intent);


                       }

                }).show();


    }



}
