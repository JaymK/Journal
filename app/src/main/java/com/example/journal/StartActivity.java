package com.example.journal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.journal.auth.LoginActivity;
import com.example.journal.auth.RegisterActivity;
import com.google.firebase.auth.FirebaseAuth;

public class StartActivity extends AppCompatActivity {
    private Button signin_button,signup_button;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        signin_button = findViewById(R.id.sign_in_btn);
        signup_button = findViewById(R.id.sign_up_btn);

        firebaseAuth = FirebaseAuth.getInstance();

        updateUI();

        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        signin_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });


    }


    private void signup(){
        Intent signupIntent = new Intent(StartActivity.this, RegisterActivity.class);
        startActivity(signupIntent);
    }

    private void login(){
        Intent logIntent = new Intent(StartActivity.this, LoginActivity.class);
        startActivity(logIntent);

    }

    private void updateUI(){
        if (firebaseAuth.getCurrentUser() != null) {
            Log.i("StartActivity", "firebaseAuth:= null");
            Intent startIntent = new Intent(StartActivity.this, MainActivity.class);
            startActivity(startIntent);
            finish();
        }else {

            Log.i("StartActivity", "firebaseAuth == null");
        }
    }
}
