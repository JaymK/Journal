package com.example.journal.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.journal.MainActivity;
import com.example.journal.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private Button regButton;
    private TextInputLayout regName, regEmail, regPassword;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference fUsersDatabase;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        regButton = findViewById(R.id.reg_btn);
        regName = findViewById(R.id.input_reg_name);
        regEmail = findViewById(R.id.input_reg_email);
        regPassword = findViewById(R.id.input_reg_pass);

        firebaseAuth = FirebaseAuth.getInstance();
        fUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String r_Name = regName.getEditText().getText().toString().trim();
                String r_Email = regEmail.getEditText().getText().toString().trim();
                String r_Pass = regPassword.getEditText().getText().toString().trim();

                registeruser(r_Name, r_Email, r_Pass);
            }
        });

    }

    private void registeruser(final String name, String email, String password){

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait, I'm processing your request...");
        progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            fUsersDatabase.child(firebaseAuth.getCurrentUser().getUid())
                                    .child("basic").child("name").setValue(name)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {

                                                progressDialog.dismiss();

                                                Intent mainIntent = new Intent(RegisterActivity.this,
                                                        MainActivity.class);
                                                startActivity(mainIntent);
                                                finish();
                                                Toast.makeText(RegisterActivity.this,
                                                        "Account Created", Toast.LENGTH_SHORT).show();

                                            }else {
                                                progressDialog.dismiss();
                                                Toast.makeText(RegisterActivity.this, "ERROR :" +
                                                        " " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                        } else {

                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "ERROR! " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return  true;
    }
}