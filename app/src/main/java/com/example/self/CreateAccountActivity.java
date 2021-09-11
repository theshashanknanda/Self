package com.example.self;

import androidx.annotation.NonNull;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateAccountActivity extends AppCompatActivity {

    // UI Components;
    private AutoCompleteTextView userNameEditText;
    private AutoCompleteTextView emailEditText;
    private EditText passwordEditText;
    private Button createAccountButton;
    private ProgressBar progressBar;

    // Firebase Authentication Connections
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    // Firebase Firestore connections
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference collectionReference = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        getSupportActionBar().setElevation(0);

        userNameEditText = findViewById(R.id.userNameEditText_createAccount_id);
        emailEditText = findViewById(R.id.emailEditText_createAccount_id);
        passwordEditText = findViewById(R.id.passwordEditText_createAccount_id);
        createAccountButton = findViewById(R.id.createAccountButton_createAccount_id);
        progressBar = findViewById(R.id.progressBar_createAccount_id);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                currentUser = firebaseAuth.getCurrentUser();
                if(currentUser != null)
                {
                    // User is already logged in
                }else{
                    // no user yet...
                }
            }
        };

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create Account code
                if(!TextUtils.isEmpty(userNameEditText.getText().toString()) &&
                !TextUtils.isEmpty(emailEditText.getText().toString()) &&
                !TextUtils.isEmpty(passwordEditText.getText().toString()))
                {
                    String email = emailEditText.getText().toString().trim();
                    String password = passwordEditText.getText().toString().trim();
                    String usermame = userNameEditText.getText().toString().trim();

                    createUserEmailAccount(email ,password, usermame);
                }else{
                    Toast.makeText(CreateAccountActivity.this, "Empty Fields", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void createUserEmailAccount(String email, String password, String username)
    {
            progressBar.setVisibility(View.VISIBLE);

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){
                                //User Created
                                currentUser = firebaseAuth.getCurrentUser();
                                String currentUserId = currentUser.getUid();

                                //Creating a document for user in firestore cloud database
                                Map<String, String> userObject = new HashMap<>();
                                userObject.put("userId",currentUserId);
                                userObject.put("userName",username);

                                collectionReference.add(userObject)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                //Succesfully created document in firestore

                                                documentReference.get()
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if(task.getResult().exists())
                                                                {
                                                                    progressBar.setVisibility(View.INVISIBLE);

                                                                    JournalApi journalApi = JournalApi.getInstance();
                                                                    journalApi.setUserid(currentUserId);
                                                                    journalApi.setUsername(username);

                                                                    Intent intent = new Intent(CreateAccountActivity.this, JournalListActivity.class);
                                                                    startActivity(intent);
                                                                }else {
                                                                    Toast.makeText(CreateAccountActivity.this, "Document doesn't exists",Toast.LENGTH_LONG).show();
                                                                    progressBar.setVisibility(View.INVISIBLE);

                                                                }
                                                            }
                                                        });
                                            }
                                        });
                            }else{
                                //Something went wrong
                                Toast.makeText(CreateAccountActivity.this, "User is not created",Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.INVISIBLE);


                            }
                        }
                    });
    }

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}


