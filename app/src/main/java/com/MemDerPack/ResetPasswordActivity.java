package com.MemDerPack;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import static com.MemDerPack.StartActivity.hideKeyboard;

public class ResetPasswordActivity extends AppCompatActivity {


    EditText send_email;
    Button btn_reset;
    ProgressBar loadingPanel;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Reset Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
                overridePendingTransition(R.anim.right_to_left_1, R.anim.right_to_left_2);
            }
        });

        send_email = findViewById(R.id.send_email);
        btn_reset = findViewById(R.id.btn_reset);
        loadingPanel = findViewById(R.id.loadingPanel);

        firebaseAuth = FirebaseAuth.getInstance();

        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btn_reset.setEnabled(false);
                hideKeyboard(ResetPasswordActivity.this);
                loadingPanel.setVisibility(View.VISIBLE);

                String email = send_email.getText().toString();

                if (email.equals("")){
                    Toast.makeText(ResetPasswordActivity.this, "All fileds are requred!", Toast.LENGTH_SHORT).show();
                    btn_reset.setEnabled(true);
                    loadingPanel.setVisibility(View.GONE);
                } else {
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(ResetPasswordActivity.this, "Check your email", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                                startActivity(i);
                                finish();
                                overridePendingTransition(R.anim.right_to_left_1, R.anim.right_to_left_2);
                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(ResetPasswordActivity.this, error, Toast.LENGTH_SHORT).show();
                                btn_reset.setEnabled(true);
                                loadingPanel.setVisibility(View.GONE);

                            }
                        }
                    });
                }
            }
        });

    }



    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent i = new Intent(ResetPasswordActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
        overridePendingTransition(R.anim.right_to_left_1, R.anim.right_to_left_2);
    }
}
