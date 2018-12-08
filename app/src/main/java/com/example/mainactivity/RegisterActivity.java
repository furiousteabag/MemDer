package com.example.mainactivity;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.mainactivity.StartActivity.hideKeyboard;

public class RegisterActivity extends AppCompatActivity {

    // User data.
    MaterialEditText username, email, password;

    // Activity element.
    Button btn_register;

    // Firebase variables.
    FirebaseAuth auth;
    DatabaseReference reference;
    DatabaseReference referenceSeen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Creating toolbar.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Associating variables.
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btn_register = findViewById(R.id.btn_register);

        // Initializing auth.
        auth = FirebaseAuth.getInstance();

        // Listening the button click.
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);

                hideKeyboard(RegisterActivity.this);

                // Getting user data to string.
                String txt_username = username.getText().toString();
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();

                // Check if data is ok and if yes registering the user.
                if (TextUtils.isEmpty(txt_username) || TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)) {
                    findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                    Toast.makeText(RegisterActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();

                } else if (txt_password.length() < 6) {
                    findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                    Toast.makeText(RegisterActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                } else {
                    register(txt_username, txt_email, txt_password);
                }
            }
        });
    }

    // Register method.
    private void register(final String username, String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // Initializing variables.
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            assert firebaseUser != null;
                            String userid = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
                            referenceSeen = FirebaseDatabase.getInstance().getReference("Users").child(userid).child("Categories_seen");

                            ArrayList<Integer> preferences = new ArrayList<>();
                            for (int i = 0; i < LoadActivity.categories.size(); i++) {
                                preferences.add(0);
                            }

                            HashMap<String, String> seen = new HashMap<>();
                            seen.put("abstract", "0");
                            seen.put("anime", "0");
                            seen.put("cats", "0");
                            seen.put("cybersport", "0");
                            seen.put("disgraceful", "0");
                            seen.put("lentach", "0");
                            seen.put("mhk", "0");
                            seen.put("normal", "0");
                            seen.put("physkek", "0");
                            seen.put("programmer", "0");

                            // Initializing hashmap to send.
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", userid);
                            hashMap.put("username", username);
                            hashMap.put("imageURL", "default");
                            hashMap.put("preferences", preferences.toString());



                            // Send the hashmap and close the form.
                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });

                            // Send the hashmap of seen urls
                            referenceSeen.setValue(seen);
                        } else {
                            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                            Toast.makeText(RegisterActivity.this, "You can't register with this email or password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.right_to_left_1, R.anim.right_to_left_2);
    }



}
