package com.example.pigeottomobile.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pigeottomobile.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class VerifyActivity extends AppCompatActivity {

    TextView verifyMsg;
    Button verifyButton;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        Button logout = findViewById(R.id.logoutBtn);
        verifyMsg = findViewById(R.id.verifyTxt);
        verifyButton = findViewById(R.id.verifyBtn);

        if(auth.getCurrentUser().isEmailVerified()){
            verifyMsg.setVisibility(View.GONE);
            verifyButton.setVisibility(View.GONE);

        }

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(VerifyActivity.this, "Verification email sent!", Toast.LENGTH_SHORT).show();
                        verifyButton.setVisibility(View.GONE);
                        verifyMsg.setVisibility(View.GONE);
                    }
                });
            }
        });


        logout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        });
    }
}