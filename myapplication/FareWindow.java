package com.example.admin.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class FareWindow extends AppCompatActivity {

    TextView distancetravelled,  amount;
    Location lastlocation=null;
    float[] result = new float[2];
    double distance=0;
    Toolbar toolbar;

    Button stoptrip;
    LocationManager locationmanager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fare_window);

        setUpToolbar();

        setUpDrawer();

        stoptrip = findViewById(R.id.Button1);
        distancetravelled = findViewById(R.id.View2);
        amount = findViewById(R.id.View1);
        locationmanager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
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

            locationmanager.requestLocationUpdates("gps", 1000, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    if (lastlocation == null) {
                        lastlocation = location;

                    }
                    Location.distanceBetween(lastlocation.getLatitude(),
                            lastlocation.getLongitude(),
                            location.getLatitude(),
                            location.getLongitude(),
                            result);
                    distance = (distance) + Math.abs(result[0])/1000f;
                    distance = Math.round(distance*100)/100.0d;
                    lastlocation = location;

                    amount.setText(Double.toString(distance));

                    //   name.setText(Long.toString(location.getTime()));
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
        }

    }



    private void setUpToolbar() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Auto Fare App");
        toolbar.inflateMenu(R.menu.menu_main);
    }


    private void setUpDrawer() {

        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.nav_drwr_fragment);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerFragment.setUpDrawer(R.id.nav_drwr_fragment, drawerLayout, toolbar);
    }

    @SuppressLint("MissingPermission")
    public void getRequiredLocation()
    {

        }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    getRequiredLocation();
                }
                else {
                    Toast.makeText(this,"Permission Denied !!",Toast.LENGTH_SHORT).show();


                }
        }
    }


}
