package com.ink_steel.inksteel.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.helpers.ConstantUtils;

import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private static final DocumentReference FIRESTORE_FRIENDS_REFERNENCE = FirebaseFirestore.getInstance()
            .document("users/" + ConstantUtils.EMAIL + "/friends/user1@gmail.com");

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Map<String, Object> friendData = new HashMap<>();
        friendData.put("text", "Hello world!");
        friendData.put("time", System.currentTimeMillis());
        FIRESTORE_FRIENDS_REFERNENCE.set(friendData);

        final TextView message1 = (TextView) findViewById(R.id.message1);
        final TextView message2 = (TextView) findViewById(R.id.message2);
        FIRESTORE_FRIENDS_REFERNENCE.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                message1.setText(documentSnapshot.getString("text"));
                Map<String, Object> friendData = new HashMap<>();
                friendData.put("text", "Zdr brat");
                friendData.put("time", System.currentTimeMillis());
                FIRESTORE_FRIENDS_REFERNENCE.set(friendData);
            }
        });
    }
}
