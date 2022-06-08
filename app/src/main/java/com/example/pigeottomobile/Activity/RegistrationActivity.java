package com.example.pigeottomobile.Activity;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pigeottomobile.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegistrationActivity extends Activity {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://pigeottomobile-d2229-default-rtdb.europe-west1.firebasedatabase.app/");
    FirebaseAuth fAuth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        final EditText name = findViewById(R.id.nameInput);
        final EditText surname = findViewById(R.id.surnameInput);
        final EditText username = findViewById(R.id.usernameInput);
        final EditText password = findViewById(R.id.passwordInput);
        final EditText conPassword = findViewById(R.id.conPasswordInput);
        final EditText email = findViewById(R.id.mailInput);
        final EditText phoneNumber = findViewById(R.id.phoneNumberInput);
        final Button registerBtn = findViewById(R.id.registerButton);
        final Button backLoginBtn = findViewById(R.id.returnToLoginActivityButton);

        fAuth = FirebaseAuth.getInstance();

        backLoginBtn.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();

        });

        registerBtn.setOnClickListener(view -> {
            final String nameTxt = name.getText().toString();
            final String surnameTxt = surname.getText().toString();
            final String usernameTxt = username.getText().toString();
            final String passwordTxt = password.getText().toString();
            final String conPasswordTxt = conPassword.getText().toString();
            final String emailTxt = email.getText().toString();
            final String phoneTxt = phoneNumber.getText().toString();

            if(nameTxt.isEmpty() || surnameTxt.isEmpty() || usernameTxt.isEmpty() || passwordTxt.isEmpty() || conPasswordTxt.isEmpty() || emailTxt.isEmpty() || phoneTxt.isEmpty()){
                Toast.makeText(RegistrationActivity.this, "Wypełnij wszystkie pola", Toast.LENGTH_SHORT).show();
            }
            else if(!passwordTxt.equals(conPasswordTxt)){
                Toast.makeText(RegistrationActivity.this, "Hasła rożnią się od siebie", Toast.LENGTH_SHORT).show();
            }
            else {

                fAuth.createUserWithEmailAndPassword(emailTxt, passwordTxt).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                String uid = fAuth.getCurrentUser().getUid();
                                databaseReference.child("users").child(uid).child("phone").setValue(phoneTxt);
                                databaseReference.child("users").child(uid).child("firstname").setValue(nameTxt);
                                databaseReference.child("users").child(uid).child("lastname").setValue(surnameTxt);
                                databaseReference.child("users").child(uid).child("password").setValue(passwordTxt);
                                databaseReference.child("users").child(uid).child("email").setValue(emailTxt);
                                databaseReference.child("users").child(uid).child("username").setValue(usernameTxt);
                                databaseReference.child("users").child(uid).child("profilePicture").setValue("https://firebasestorage.googleapis.com/v0/b/pigeottomobile-d2229.appspot.com/o/post_images%2FdefeultProfile.png?alt=media&token=e3b0472f-ecaf-4f81-ac9c-03774df2721e");


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        FirebaseUser user;
                        user = fAuth.getCurrentUser();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(usernameTxt)
                                .setPhotoUri(Uri.parse("https://firebasestorage.googleapis.com/v0/b/pigeottomobile-d2229.appspot.com/o/post_images%2FdefeultProfile.png?alt=media&token=e3b0472f-ecaf-4f81-ac9c-03774df2721e"))
                                .build();

                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "User profile updated.");
                                        }
                                    }
                                });

                        Toast.makeText(RegistrationActivity.this, "Rejestracja przebiegła pomyślnie", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegistrationActivity.this, "Register error!", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

}