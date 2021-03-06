package com.example.journal;

import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.journal.auth.GoogleSignInActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private RecyclerView mEntryList;
    private GridLayoutManager gridLayoutManager;

    private DatabaseReference fEntryDatabase;

    private FloatingActionButton logOutButton;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logOutButton = findViewById(R.id.log_out_btn);
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(MainActivity.this, GoogleSignInActivity.class));
                }
            }
        };

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
            }
        });


        mEntryList = findViewById(R.id.main_entries_list);

        gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL,
                false);

        mEntryList.setHasFixedSize(true);
        mEntryList.setLayoutManager(gridLayoutManager);
        mEntryList.addItemDecoration(new GridSpacing(2,dpToPx(10), true));

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            fEntryDatabase = FirebaseDatabase.getInstance().getReference().child("Entries")
                    .child(firebaseAuth.getCurrentUser().getUid());
        }

        updateUI();

    }

    @Override
    public void onStart() {
        super.onStart();
        loadData();
        mAuth.addAuthStateListener(mAuthListener);

    }

    private void loadData() {
        Query query = fEntryDatabase.orderByChild("timestamp");
        FirebaseRecyclerAdapter<EntryModel, EntryViewHolder> firebaseRecyclerAdapter = new
                FirebaseRecyclerAdapter<EntryModel, EntryViewHolder>(

                EntryModel.class,
                R.layout.single_entry,
                EntryViewHolder.class,
                query

        ) {
            @Override
            protected void populateViewHolder(final EntryViewHolder viewHolder, EntryModel model,
                                              int position) {
                final String entryId = getRef(position).getKey();

                fEntryDatabase.child(entryId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("title") && dataSnapshot
                                .hasChild("timestamp")) {
                            String title = dataSnapshot.child("title").getValue().toString();
                            String timestamp = dataSnapshot.child("timestamp").getValue().toString();

                            viewHolder.setEntryTitle(title);
                            viewHolder.setEntryTime(timestamp);

                            GetPostTime getPostTime = new GetPostTime();
                            viewHolder.setEntryTime(getPostTime.GetPostTime(Long.parseLong(timestamp),
                                    getApplicationContext()));

                            viewHolder.entryCard.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(MainActivity.this,
                                            NewEntryActivity.class);
                                    intent.putExtra("entryId", entryId);
                                    startActivity(intent);
                                }
                            });
                        }


                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        };

        mEntryList.setAdapter(firebaseRecyclerAdapter);
    }

    private void updateUI() {
        if (firebaseAuth.getCurrentUser() != null) {
            Log.i("MainActivity", "firebaseAuth:= null");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.main_new_entry_btn:
                Intent newIntent = new Intent(MainActivity.this, NewEntryActivity.class);
                startActivity(newIntent);
                break;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                r.getDisplayMetrics()));
    }
}