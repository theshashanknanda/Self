package com.example.self;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {

    private Button createAccountButton;
    private Button loginButton;
    private AutoCompleteTextView emailEditText;
    private EditText passwordEditText;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser firebaseUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setElevation(0);

        loginButton = findViewById(R.id.loginButton_login_id);
        emailEditText = findViewById(R.id.emailEditText_login_id);
        passwordEditText = findViewById(R.id.passwordEditText_login_id);
        progressBar = findViewById(R.id.progressBar);

        createAccountButton = findViewById(R.id.createAccountButton_login_id);
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,CreateAccountActivity.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                loginWithEmailPassword(email, password);
            }
        });

    }

    void loginWithEmailPassword(String email, String password){

        progressBar.setVisibility(View.VISIBLE);

        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {

                            firebaseUser = firebaseAuth.getCurrentUser();
                            String currentUserId = firebaseUser.getUid();

                            collectionReference.whereEqualTo("userId", currentUserId)
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                                            if(!value.isEmpty()){

                                                for(QueryDocumentSnapshot snapshot: value)
                                                {
                                                    JournalApi journalApi = JournalApi.getInstance();
                                                    journalApi.setUsername(snapshot.getString("userName"));
                                                    journalApi.setUserid(snapshot.getString("userId"));

                                                    progressBar.setVisibility(View.INVISIBLE);

                                                    startActivity(new Intent(LoginActivity.this, JournalListActivity.class));
                                                }
                                            }
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(LoginActivity.this,"Password Maybe Wrong",Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.INVISIBLE);

                        }
                    });
        }else{
            Toast.makeText(LoginActivity.this,"Fields Empty",Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

}

