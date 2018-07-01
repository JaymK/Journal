package com.example.journal;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NewEntryActivity extends AppCompatActivity {

    private Button createButton;
    private EditText etTitle, etContent;
    private Toolbar mToolbar;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference fEntryDatabase;

    private Menu mainMenu;
    private String entryID;

    private boolean isExist;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.new_entry_menu, menu);
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);

        try {
            entryID = getIntent().getStringExtra("entryId");

            if (!entryID.trim().equals("")) {
                isExist = true;
            } else {
                isExist = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        createButton = findViewById(R.id.new_entry_btn);
        etTitle = findViewById(R.id.new_entry_title);
        etContent = findViewById(R.id.new_entry_content);
        mToolbar = findViewById(R.id.new_entry_toolbar);


        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        firebaseAuth = FirebaseAuth.getInstance();
        fEntryDatabase = FirebaseDatabase.getInstance().getReference().child("Entries").child
                (Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid());

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = etTitle.getText().toString().trim();
                String content = etContent.getText().toString().trim();

                if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
                    Snackbar.make(findViewById(android.R.id.content), "FILL EMPTY FIELDS",
                            Snackbar.LENGTH_SHORT).show();
                } else {
                    createEntry(title, content);
                }
            }
        });

        putData();
    }
    private void putData() {
        if (isExist) {
            fEntryDatabase.child(entryID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("title") && dataSnapshot.hasChild("content")) {
                        String title = dataSnapshot.child("title").getValue().toString();
                        String content = dataSnapshot.child("content").getValue().toString();

                        etTitle.setText(title);
                        etContent.setText(content);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }
    private void createEntry(String title, String content) {
        if (firebaseAuth.getCurrentUser() != null) {
            if (isExist) {
                Map updateMap = new HashMap();
                updateMap.put("title", etTitle.getText().toString().trim());
                updateMap.put("content", etContent.getText().toString().trim());
                updateMap.put("timestamp", ServerValue.TIMESTAMP);
                fEntryDatabase.child(entryID).updateChildren(updateMap);
                Toast.makeText(this, "Entry Updated", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                final DatabaseReference newEntryRef = fEntryDatabase.push();
                final Map entryMap = new HashMap();
                entryMap.put("title", title);
                entryMap.put("content", content);
                entryMap.put("timestamp", ServerValue.TIMESTAMP);

                Thread mainThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        newEntryRef.setValue(entryMap).addOnCompleteListener(new OnCompleteListener
                                <Void>() {

                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {
                                    Toast.makeText(NewEntryActivity.this,
                                            "Entry Added Successfully",
                                            Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(NewEntryActivity.this, "ERROR!" +
                                            task.getException().getMessage(), Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                        });
                    }
                });
                mainThread.start();
            }
        } else {
            Toast.makeText(this, "USER IS NOT SIGNED IN", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.new_entry_delete_btn:
                if (isExist) {
                    deleteEntry();
                } else {
                    Toast.makeText(this, "NOTHING TO DELETE", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return true;
    }
    private void deleteEntry () {
        fEntryDatabase.child(entryID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(NewEntryActivity.this, "ENTRY DELETED",
                                    Toast.LENGTH_SHORT).show();
                            entryID = "no";
                            finish();
                        } else {
                            Log.e("NewEntryActivity", task.getException().toString());
                            Toast.makeText(NewEntryActivity.this, "ERROR!  " +
                                    task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
        });
    }
}
