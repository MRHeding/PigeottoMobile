package com.example.pigeottomobile.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.pigeottomobile.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfileActivity extends AppCompatActivity {
    private ImageView ivProfile;
    private EditText etFirstName;
    private EditText etLastName;
    private EditText etPhone;
    private ImageButton imgBtnChangePhoto;
    private Button btnEdit;
    private Button btnLeave;

    private boolean photoEdited = false;
    private boolean dataEdited = false;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private Uri pickedImgUri = null;

    private static final int PReqCode = 2;
    private static final int REQUESCODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        readAndInitializeData();

        imgBtnChangePhoto = findViewById(R.id.imgBtnChangePhoto);
        imgBtnChangePhoto.setOnClickListener((view) -> {
            if (isPermissionGranted()) {
                openGallery();
            } else {
                requestForPermission();
                openGallery();
            }
            btnEdit.setText(R.string.save);
        });

        btnEdit = findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(view -> {
            if (!photoEdited && !dataEdited) {
                makeUserDataEnabledToEdit();
                dataEdited = true;
                btnEdit.setText(R.string.save);
            } else {
                if (dataEdited) modifyUserDataInDatabase();
                if (photoEdited) modifyProfilePhotoInDatabase();
            }
        });

        btnLeave = findViewById(R.id.btnLeave);
        btnLeave.setOnClickListener((view -> {
            startActivity(new Intent(getApplicationContext(), Home.class));
            finish();
        }));
    }

    private void readAndInitializeData() {
        ivProfile = findViewById(R.id.ivProfile);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etPhone = findViewById(R.id.etPhone);

        if (firebaseUser != null) {
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
            usersRef.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Glide.with(ProfileActivity.this).load(
                            dataSnapshot.child("profilePhoto").getValue(String.class)).into(ivProfile);

                    String firstName = dataSnapshot.child("firstname").getValue(String.class);
                    String lastName = dataSnapshot.child("lastname").getValue(String.class);
                    String phone = dataSnapshot.child("phone").getValue(String.class);
                    etFirstName.setText(firstName);
                    etLastName.setText(lastName);
                    etPhone.setText(phone);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getApplicationContext(), "Problem with reading data from database", Toast.LENGTH_LONG).show();
                }
            });

        } else {
            Toast.makeText(getApplicationContext(), "Problem with connecting to database", Toast.LENGTH_LONG).show();
        }
    }

    private void makeUserDataEnabledToEdit() {
        etFirstName.setEnabled(true);
        etLastName.setEnabled(true);
        etPhone.setEnabled(true);
    }

    private void modifyUserDataInDatabase() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference();
        String firstName = etFirstName.getText().toString(),
                lastName = etLastName.getText().toString(),
                phone = etPhone.getText().toString();

        if (firstName.length() > 0 && lastName.length() > 0 && phone.length() > 0) {
            userRef.child("users").child(firebaseUser.getUid()).child("firstname").setValue(firstName);
            userRef.child("users").child(firebaseUser.getUid()).child("lastname").setValue(lastName);
            userRef.child("users").child(firebaseUser.getUid()).child("phone").setValue(phone);
            Toast.makeText(getApplicationContext(), R.string.dataUpdated, Toast.LENGTH_SHORT).show();
        }
    }

    private void requestForPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(ProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(ProfileActivity.this, R.string.pleaseAcceptRequiredPermission, Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(ProfileActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PReqCode);
        }
    }


    private boolean isPermissionGranted(){
        return ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED;
    }

    private void openGallery() {
        //TODO: open gallery intent and wait for user to pick an image !

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUESCODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESCODE && data != null ) {
            pickedImgUri = data.getData() ;
            ivProfile.setImageURI(pickedImgUri);
            photoEdited = true;
            btnEdit.setText(R.string.save);
        }
    }

    private void modifyProfilePhotoInDatabase(){
        if(pickedImgUri == null) return;

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("profile_images");
        final StorageReference imageFilePath = storageReference.child(pickedImgUri.getLastPathSegment());
        imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String uriToPhotoInDatabase = uri.toString();
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference();
                        userRef.child("users").child(firebaseUser.getUid()).child("profilePhoto").setValue(uriToPhotoInDatabase);
                        Toast.makeText(getApplicationContext(), R.string.photoUpdated, Toast.LENGTH_SHORT).show();

                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setPhotoUri(Uri.parse(uriToPhotoInDatabase))
                                .build();

                        firebaseUser.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                }
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), R.string.failedToSaveData, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}