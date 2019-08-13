package com.example.admin.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    Button loginbutton ;
    EditText pinnumber;
    TextView owneridtxt;
    Module.Login logindata= new Module.Login();
    String ownerid=null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      setContentView(R.layout.login_window);


        SharedPreferences sp = getSharedPreferences(getPackageName()+"owneridfile", Context.MODE_PRIVATE);

         ownerid = (sp.getString("OwnerId",null));

        pinnumber = (EditText)findViewById(R.id.Pin1);
        loginbutton = findViewById(R.id.Button1);
        owneridtxt = findViewById(R.id.View1);
        owneridtxt.append(ownerid);

        getDatafromDatabase();

        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    verifyLogin();

            }
        });

    }

    public void verifyLogin()
    {
        if(pinnumber.getText().toString().equals(logindata.returnPin()))
        {
            Intent intent;
            //if(getSharedPreferences(getPackageName()+"passengeridfile", Context.MODE_PRIVATE).getString("Passenger id","null").equals("null")) {


                 intent = new Intent(Login.this, Homepage.class);
            //}
            //else
            //{
              //  intent = new Intent(Login.this,MapsActivity.class);
            //}
            finish();

            startActivity(intent);

            }
        else
        {
            showDialogBox("The Entered Pin is wrong","OK");
        }

    }

    public void getDatafromDatabase() {

        FirebaseDatabase.getInstance().getReference().child("Pin number").child(ownerid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    logindata.acceptOwnerid(dataSnapshot.child("ownerid").getValue(String.class));
                    logindata.acceptPin(dataSnapshot.child("pin").getValue(String.class));
                Toast.makeText(Login.this, "Active", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
