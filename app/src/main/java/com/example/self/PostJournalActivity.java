package com.example.self;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.nio.channels.GatheringByteChannel;
import java.util.Date;

public class PostJournalActivity extends AppCompatActivity {

    private Button saveButton;
    private ImageView cameraButton;
    private ProgressBar progressBar;
    private EditText titleEditText;
    private EditText thoughtEditText;

    private String currentUserId;
    private String currentUserName;

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Journal");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_journal);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);

        progressBar = findViewById(R.id.progressBar_postJournal_id);
        saveButton = findViewById(R.id.addButton_postJournal_id);
        titleEditText = findViewById(R.id.editText1_postJournal_id);
        thoughtEditText = findViewById(R.id.editText2_postJournal_id);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if(user != null)
                {

                }else{

                }
            }
        };

        if(JournalApi.getInstance() != null)
        {
            currentUserId = JournalApi.getInstance().getUserid();
            currentUserName = JournalApi.getInstance().getUsername();


        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveJournal();
            }
        });
    }

    public void saveJournal(){

        progressBar.setVisibility(View.VISIBLE);

        String title = titleEditText.getText().toString().trim();
        String thought = thoughtEditText.getText().toString().trim();

        if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(thought)){

            Journal journal = new Journal();
            journal.setTitle(title);
            journal.setThought(thought);
            journal.setTimeadded(new Timestamp(new Date()));
            journal.setUserName(currentUserName);
            journal.setUserId(currentUserId);

            collectionReference.add(journal)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {

                            progressBar.setVisibility(View.INVISIBLE);

                            startActivity(new Intent(PostJournalActivity.this, JournalListActivity.class));
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(PostJournalActivity.this,"Journal not added",Toast.LENGTH_LONG).show();
                        }
                    });
        }else{

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        user = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(firebaseAuth != null)
        {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}

