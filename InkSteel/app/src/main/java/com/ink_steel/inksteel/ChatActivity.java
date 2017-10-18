package com.ink_steel.inksteel;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

import static com.ink_steel.inksteel.ConstantUtils.FIRESTORE_FRIENDS_REFERNENCE;

public class ChatActivity extends AppCompatActivity {

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
