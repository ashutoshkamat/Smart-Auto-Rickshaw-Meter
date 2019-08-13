package com.example.admin.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.FirebaseDatabase;

import Module.Login;
import Module.Owner;

public class Registration extends AppCompatActivity{
    Owner owner = new Owner();
    Login logindata = new Login();

    EditText register_name, register_mobile_number, register_vehicle_no;
    TextView owner_id;
    Button registerbutton;
    EditText pinnumber;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.owner_register);

        owner.setOwnerid("O"+generatePid());

        pinnumber = findViewById(R.id.Pin1);
        owner_id = findViewById(R.id.View1);
        register_name= findViewById(R.id.Text1);
        register_mobile_number = findViewById(R.id.Text3);
        register_vehicle_no = findViewById(R.id.Text2);
        registerbutton = findViewById(R.id.Button1);
        owner_id.append(owner.getOwnerid());


        registerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authenticateUserData();

            }
        });

    }


    public void authenticateUserData()
    {
        if(register_name.getText().toString().equals("") || register_mobile_number.getText().toString().equals("") || register_vehicle_no.getText().toString().equals(""))
        {
                showDialogBox("Please fill up all the fields","OK");
        }
        else
        {
            owner.setName(register_name.getText().toString());
            owner.setMobile_number(register_mobile_number.getText().toString());
            owner.setVehicle_number(register_vehicle_no.getText().toString());
            logindata.acceptOwnerid(owner.getOwnerid());
            logindata.acceptPin(pinnumber.getText().toString());

            storeRegisteredData();

            showDialogBox("You are successfully registered \n Onwer id :"+owner.getOwnerid(),"OK");
            SharedPreferences sp = getSharedPreferences(getPackageName()+"owneridfile", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = sp.edit();

            edit.putString("OwnerId",owner.getOwnerid());
            edit.apply();


            //After successfull registration go to start page

            Intent intent = new Intent(Registration.this, MainActivityOwner.class);
            startActivity(intent);

        }

    }

    public void storeRegisteredData()
    {


        FirebaseDatabase.getInstance().getReference().child("OwnerDatabase").child(owner.getOwnerid()).setValue(owner);

        Toast.makeText(this, "Registration Successfull !", Toast.LENGTH_SHORT).show();

        FirebaseDatabase.getInstance().getReference().child("Pin number").child(owner.getOwnerid()).child("ownerid").setValue(owner.getOwnerid());
        FirebaseDatabase.getInstance().getReference().child("Pin number").child(owner.getOwnerid()).child("pin").setValue(pinnumber.getText().toString());

    }

    public String generatePid()
    {
        int random = (int)Math.floor(100000 + Math.random() * 900000);
        return Integer.toString(random);
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

}


