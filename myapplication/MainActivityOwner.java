package com.example.admin.myapplication;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivityOwner extends AppCompatActivity {

    Button registerButton, loginButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_owner);

        if(getIfAlreadyAssossiated())
        {
            Intent intent = new Intent(MainActivityOwner.this, MapsActivity.class);
            finish();
            startActivity(intent);

        }

        registerButton = findViewById(R.id.Button1);
        loginButton = findViewById(R.id.Button2);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivityOwner.this, Registration.class);

                startActivity(intent);

            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivityOwner.this, Login.class);
                finish();
                startActivity(intent);
            }
        });

    }

    private boolean getIfAlreadyAssossiated() {

        if(!getSharedPreferences(getPackageName() + "passengeridfile", Context.MODE_PRIVATE).getString("Passenger id","null").equals("null"))
        {
            return true;
        }
        else
            return false;
    }

}
