package com.example.pigeottomobile;

import android.view.View;

import com.example.pigeottomobile.models.Post;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Assert;
import org.junit.Test;

public class FirebaseUploadTest {
    @Test
    public void uploadPostToFirebase(){
        DatabaseReference postsTableRef = FirebaseDatabase.getInstance().getReference().child("test_posts");

        String key = postsTableRef.getKey();
        Post post = new Post("olek",
                "hiacynt",
                "https://firebasestorage.googleapis.com/v0/b/pigeottomobile-d2229.appspot.com/o/post_images%2F21?alt=media&token=784903d1-8b73-4b35-bfec-65c4ffe1c861",
                "HhVrnrFTOSPYXmIZpX2ZDqTpXpz2",
                "https://firebasestorage.googleapis.com/v0/b/pigeottomobile-d2229.appspot.com/o/profile_images%2F33?alt=media&token=11d54e09-1f65-4442-b654-b2420718d34e");
        post.setPostKey(key);

        postsTableRef.setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
    }
}
