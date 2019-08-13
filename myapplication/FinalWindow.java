package com.example.admin.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;


public class FinalWindow extends AppCompatActivity {


    Button paidbutton;
    TextView amountview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.final_window);

        amountview = findViewById(R.id.View1);
        paidbutton = findViewById(R.id.Button1);

        amountview.append(getAmount());

        paidbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onPayment();

            }
        });





    }

    public String getAmount()
    {
        SharedPreferences sp = getSharedPreferences(getPackageName()+"amountfile",MODE_PRIVATE);
        return sp.getString("Amount","null");
    }


    public void onPayment()
    {
        SharedPreferences sp = getSharedPreferences(getPackageName()+"passengeridfile", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();

        FirebaseDatabase.getInstance().getReference().child("PassengerDatabase").child(sp.getString("Passenger id",null)).child("assossiated_owner").setValue("null");
        edit.clear();
        edit.commit();

        sp = getSharedPreferences(getPackageName()+"owneridfile", Context.MODE_PRIVATE);

        FirebaseDatabase.getInstance().getReference().child("OwnerDatabase").child(sp.getString("OwnerId",null)).child("requesting_passenger").setValue("null");


        finish();


    }
}
