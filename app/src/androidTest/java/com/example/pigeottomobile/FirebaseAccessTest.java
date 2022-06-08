package com.example.pigeottomobile;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Assert;
import org.junit.Test;

import androidx.annotation.NonNull;

public class FirebaseAccessTest {
    @Test
    public void getUserFromFirebase(){
        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseDatabase.child("users").child("HhVrnrFTOSPYXmIZpX2ZDqTpXpz2");
        firebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String email, firstName, lastName, phone, profilePhoto, username;
                email = snapshot.child("email").getValue(String.class);
                firstName = snapshot.child("firstname").getValue(String.class);
                lastName = snapshot.child("lastname").getValue(String.class);
                phone = snapshot.child("phone").getValue(String.class);
                profilePhoto = snapshot.child("profilePhoto").getValue(String.class);
                username = snapshot.child("username").getValue(String.class);
                Assert.assertEquals("do.testow1112@gmail.com", email);
                Assert.assertEquals("andrzej", firstName);
                Assert.assertEquals("olaboga", lastName);
                Assert.assertEquals("24142", phone);
                Assert.assertEquals("https://firebasestorage.googleapis.com/v0/b/pigeottomobile-d2229.appspot.com/o/profile_images%2F33?alt=media&token=11d54e09-1f65-4442-b654-b2420718d34e", profilePhoto);
                Assert.assertEquals("alibaba", username);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Assert.fail();
            }
        });
    }
}
