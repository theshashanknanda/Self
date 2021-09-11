package com.example.self;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;

public class JournalListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecylerViewAdapter adapter;
    private List<Journal> journalList;
    private ProgressBar progressBar;
    private TextView nameView;
    private TextView noTextView;

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser firebaseUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Journal");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_list);

        getSupportActionBar().setElevation(0);

        progressBar = findViewById(R.id.progressBar);
        journalList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        nameView = findViewById(R.id.nameView);
        noTextView = findViewById(R.id.noTextView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String userName = JournalApi.getInstance().getUsername();
        nameView.setText(userName);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.menuAdd:
                    startActivity(new Intent(JournalListActivity.this, PostJournalActivity.class));
                break;

            case R.id.menuSignOut:
                    firebaseAuth.signOut();
                    startActivity(new Intent(JournalListActivity.this, MainActivity.class));
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        collectionReference.whereEqualTo("userId",JournalApi.getInstance().getUserid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        journalList.clear();
                        if(!queryDocumentSnapshots.isEmpty())
                        {
                            noTextView.setVisibility(View.GONE);
                            for(QueryDocumentSnapshot journals: queryDocumentSnapshots)
                            {
                                Journal journal = journals.toObject(Journal.class);
                                journalList.add(journal);
                            }
                            adapter = new RecylerViewAdapter(journalList, JournalListActivity.this);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }else{
                            noTextView.setVisibility(View.VISIBLE);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
}

