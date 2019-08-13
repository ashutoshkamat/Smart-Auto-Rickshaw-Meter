package com.example.passengermodule;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.mythemes.Models.Passenger;
import com.example.passengermodule.Models.Passenger;
import com.google.firebase.database.FirebaseDatabase;

public class Registration extends AppCompatActivity{

    TextView register_pid;
    Passenger passenger;
    EditText register_name, register_mobile_number;
    Button register_button;
    String pid;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        pid = generatePid();
        passenger = new Passenger();
        register_name = findViewById(R.id.Text1);
        register_mobile_number = findViewById(R.id.Text2);
        register_button = findViewById(R.id.Button1);
        register_pid = findViewById(R.id.View1);
        register_pid.append(pid);

        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passenger.setName(register_name.getText().toString());
                passenger.setMobile_number(register_mobile_number.getText().toString());
                passenger.setPid(pid);
                storeRegisteredData();
            }
        });

    }
    public void storeRegisteredData()
    {


        Firebase.getInstance().getReference().child("PassengerDatabase").child(passenger.getPid()).setValue(passenger);

        Toast.makeText(this, "Registration Successfull !", Toast.LENGTH_SHORT).show();

    }

    public String generatePid()
    {
        int random = (int)Math.floor(100000 + Math.random() * 900000);
        return Integer.toString(random);
    }




}
