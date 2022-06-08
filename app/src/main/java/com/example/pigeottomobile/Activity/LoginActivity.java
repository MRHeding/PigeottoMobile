package com.example.pigeottomobile.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pigeottomobile.R;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText username = findViewById(R.id.username);
        final EditText password = findViewById(R.id.password);
        final Button loginBtn = findViewById(R.id.login);
        final Button registerNowBtn = findViewById(R.id.register);
        firebaseAuth = FirebaseAuth.getInstance();

        loginBtn.setOnClickListener(view -> {
                    if (username.getText().toString().isEmpty()) {
                        username.setError("Email is missing.");
                        return;
                    }

                    if (password.getText().toString().isEmpty()) {
                        password.setError("Password is missing");
                        return;
                    }

            SafetyNet.getClient(this).verifyWithRecaptcha("6LeCRj0gAAAAAJz3wKic5F-y9Ml3gZAZSmQ7UNkA")
                    .addOnSuccessListener(this, recaptchaTokenResponse -> {
                        String userResponseToken = recaptchaTokenResponse.getTokenResult();
                        if (!userResponseToken.isEmpty()){
                            firebaseAuth.signInWithEmailAndPassword(username.getText().toString(), password.getText().toString()).addOnSuccessListener(authResult -> {
                                startActivity(new Intent(getApplicationContext(), LoadingActivity.class));
                                finish();
                            }).addOnFailureListener(
                                    e -> Toast.makeText(LoginActivity.this, "Login failed!", Toast.LENGTH_SHORT).show());
                        }
                    })
                    .addOnFailureListener(this, e -> {
                        if (e instanceof ApiException) {
                            ApiException apiException = (ApiException) e;
                            int statusCode = apiException.getStatusCode();
                            Log.d("reCaptchaException", "Error: " + CommonStatusCodes.getStatusCodeString(statusCode));
                        } else {
                            Log.d("reCaptchaException", "Error: " + e.getMessage());
                        }
                    });
                });



        registerNowBtn.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), RegistrationActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), VerifyActivity.class));
            finish();
        }
    }
}