package com.example.pigeottomobile.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.pigeottomobile.R;
import com.google.firebase.auth.FirebaseAuth;

public class LoadingActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        firebaseAuth = FirebaseAuth.getInstance();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(firebaseAuth.getCurrentUser().isEmailVerified()){
                    startActivity(new Intent(getApplicationContext(), Home.class));
                    finish();
                }
                else{
                    startActivity(new Intent(getApplicationContext(), VerifyActivity.class));
                }
            }
        },2000);
    }
}