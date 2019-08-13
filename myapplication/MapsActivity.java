package com.example.admin.myapplication;


import android.Manifest;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;

    NotificationCompat.Builder notification = new NotificationCompat.Builder(this,"1");
    String addr=new String("null");

    Button endtripbutton, showdestinationpath;
    TextView destinationaddresstext;
    Destination destination = new Destination();
    LatLng destloc;
    Double lat, longi;
    SupportMapFragment mapFragment;

    double currlat = 0;
    double currlong = 0;
    LocationRequest locationrequest;
    GoogleApiClient googleapiclient;
    Location lastlocation = null, currloc = null;
    String passengerid;

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    Long starttriptime, endtriptime, updatetime, prevtime = (long)0, totalwaittime = (long)0;
    int amount = 18;
    float result[];
    double distance, prevdistance, distancebetween;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        SharedPreferences sp1 = getSharedPreferences(getPackageName() + "passengeridfile", Context.MODE_PRIVATE);
        passengerid=sp1.getString("Passenger id","null");
        prevtime = (long)0;
        totalwaittime = (long)0;
        starttriptime = System.currentTimeMillis();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
         mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        endtripbutton = findViewById(R.id.Button1);
        showdestinationpath = findViewById(R.id.Button2);
        destinationaddresstext = findViewById(R.id.Text2);
        result = new float[3];
        googleapiclient = new GoogleApiClient.Builder(this)
                            .addApi(LocationServices.API)
                            .addConnectionCallbacks(this)
                            .addOnConnectionFailedListener(this)
                            .build();

        locationrequest = new LocationRequest()
                            .setInterval(15*1000)
                            .setFastestInterval(5000)
                            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        getLocation();
        if(addr .equals("null"))
        {
            destinationaddresstext.append("Passenger location");
        }
        else
        {
            destinationaddresstext.append(addr);
        }

                showdestinationpath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDestinationPath();
            }
        });

        endtripbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endTrip();
            }
        });


    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

//        getNotification();
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


            mMap.setMyLocationEnabled(true);
            showRequestedDestination();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                    showRequestedDestination();

                }
                else {
                    Toast.makeText(this,"Permission Denied !!",Toast.LENGTH_SHORT).show();


                }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleapiclient.connect();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {


        onRequestLocation();

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
    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {


        updatetime = System.currentTimeMillis();

        if(currloc == null)
        {
            currloc = location;
        }

        if (lastlocation == null) {
            lastlocation = location;

        }



        Location.distanceBetween(lastlocation.getLatitude(),
                lastlocation.getLongitude(),
                location.getLatitude(),
                location.getLongitude(),
                result);

        distancebetween = Math.abs(result[0])/1000f;
        distancebetween = Math.round(distancebetween*100)/100.0d;
        distance = (distance) + Math.abs(result[0])/1000f;
        distance = Math.round(distance*100)/100.0d;
        lastlocation = location;

        if(distancebetween == 0)
        {

            if(prevtime == 0)
            {
                prevtime = updatetime ;
            }
            else
            {
                totalwaittime = totalwaittime + (updatetime - prevtime);
                Toast.makeText(MapsActivity.this,"Total wait time :"+Long.toString(totalwaittime),Toast.LENGTH_SHORT).show();
                prevtime = updatetime;
            }


        }
        else
        {
            prevtime = (long)0;
        }


        if(distance >= 1.50 ) {

            if ((distance - prevdistance) >= 1.00 && (distance - prevdistance) <= 1.50) {
                amount = amount + 12;
                prevdistance = distance;
            }
        }
        else
            prevdistance = distance;



        Toast.makeText(this,Double.toString(distance), Toast.LENGTH_SHORT).show();


    }


    public void showRequestedDestination()
    {
        Geocoder geocoder = new Geocoder(this);
        List<Address> addresslist = null;
        try {
            if(addr==null) addr="null";
            if(!addr.equals("null")) {
                destinationaddresstext.setText("To: ");
                destinationaddresstext.append(addr);

                addresslist = geocoder.getFromLocationName(addr, 1);
                Address address = addresslist.get(0);
                destloc = new LatLng(address.getLatitude(), address.getLongitude());
                Toast.makeText(MapsActivity.this, "Got location", Toast.LENGTH_LONG).show();

                mMap.addMarker(new MarkerOptions().position(destloc).title("Destination"));

            }
            else{

                destinationaddresstext.setText("To: ");
                destinationaddresstext.append("Passenger location");
                destloc = new LatLng(lat, longi);

                mMap.addMarker(new MarkerOptions().position(destloc).title("Passenger").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_person)));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(destloc));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(20));



    }

    public void getLocation()
    {
        SharedPreferences sp = getSharedPreferences(getPackageName() + "addressfile", Context.MODE_PRIVATE);
        if(!sp.getString("Address", "null").equals("null"))
        addr = (sp.getString("Address", "null"));
        else {
        SharedPreferences sp1=getSharedPreferences(getPackageName()+"passengerlatlng",Context.MODE_PRIVATE);

            lat=Double.parseDouble(sp1.getString("lat","null"));
            longi=Double.parseDouble(sp1.getString("longi","null"));
            Toast.makeText(MapsActivity.this,lat+","+longi,Toast.LENGTH_LONG).show();
            getDestinationLocation();
        }



    }


    public void getDestinationLocation()
    {
        FirebaseDatabase.getInstance().getReference().child("DestinationLocation").child(passengerid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(!dataSnapshot.child("lat").exists()) {

                        addr = dataSnapshot.child("DestinationAddress").getValue(String.class);
                      //  addr = destination.returnDestinationAddress();

                        mapFragment.getMapAsync(MapsActivity.this);

                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {




            }
        });




    }

    public String returnUrldestination()
    {
        String urld;

        urld = addr.replaceAll( " ","+");
        return urld;


    }

    public void endTrip()
    {

        SharedPreferences sp = getSharedPreferences(getPackageName()+"amountfile",MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();

        edit.putString("Amount",Integer.toString(amount));
        edit.apply();

        showDialogBoxConfirmEndTrip("Do you want to end trip ?","YES","NO");
    }
    public void showDestinationPath()
    {

        Uri navigateuri  =  Uri.parse("https://www.google.com/maps/dir/?api=1&map_action=map&destination="+returnUrldestination()+"&dir_action=navigate&travelmode=driving");
        Intent intent = new Intent(Intent.ACTION_VIEW,navigateuri);
        startActivity(intent);
    }

    public void getNotification()
    {
        Toast.makeText(this,"hello", Toast.LENGTH_SHORT).show();


        String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";

        NotificationChannel notificationChannel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

       notificationChannel  = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_HIGH);

            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
        }


        notification.setSmallIcon(Color.WHITE);
        notification.setContentTitle("This is title");
        notification.setContentText("This is a text");

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(notificationChannel);

        notificationManager.notify(1, notification.build());
    }

    public void storeAmountinDatabase()
    {


        SharedPreferences sp = getSharedPreferences(getPackageName() + "passengeridfile", Context.MODE_PRIVATE);
        String pid = sp.getString("Passenger id","null");


        FirebaseDatabase.getInstance().getReference().child("Fare Info").child(pid).setValue(Long.toString(amount));


    }

    public void transitionToFinalWindow()
    {
        endtriptime = System.currentTimeMillis();

        amount = amount + (int)(totalwaittime/6000);

        storeAmountinDatabase();
        prevtime = (long)0;
        totalwaittime = (long)0;


        SharedPreferences sp = getSharedPreferences(getPackageName()+"amountfile",MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();

        edit.putString("Amount",Integer.toString(amount));
        edit.apply();


        sp = getSharedPreferences(getPackageName()+"owneridfile", Context.MODE_PRIVATE);

        FirebaseDatabase.getInstance().getReference().child("OwnerDatabase").child(sp.getString("OwnerId",null)).child("ishired").setValue(false);

        googleapiclient.disconnect();

        Intent intent = new Intent(MapsActivity.this, FinalWindow.class);

        finish();
        startActivity(intent);

    }
    @Override
    protected void onPause() {
        super.onPause();
        onStart();

    }


    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            enterPictureInPictureMode();
        }

    }


    public void showDialogBox(String message, String positivetext)
    {
        new AlertDialog.Builder(this).setMessage(message)
                .setPositiveButton(positivetext, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                }).show();

    }

    public void showDialogBoxConfirmEndTrip(String message, String positivetext, String negativetext)
    {
        new AlertDialog.Builder(this).setMessage(message)
                .setPositiveButton(positivetext, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        transitionToFinalWindow();

                    }
                }).setNegativeButton(negativetext, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }}).show();


    }



}