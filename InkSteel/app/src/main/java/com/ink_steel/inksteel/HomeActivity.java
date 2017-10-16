package com.ink_steel.inksteel;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.data.DataBufferObserver;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;
import java.util.concurrent.Future;

public class HomeActivity extends AppCompatActivity {

    private TextView name, date, age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DocumentReference saveInfo = FirebaseFirestore.getInstance().collection("users").
                document(user.getEmail());

        name = (TextView) findViewById(R.id.userName);
        date = (TextView) findViewById(R.id.userTime);
        age = (TextView) findViewById(R.id.userAge);

        final Task<DocumentSnapshot> future = saveInfo.get();

        future.addOnCompleteListener(this, new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                DocumentSnapshot snapshot = task.getResult();

                name.setText(snapshot.getString(UserInfoActivity.USER_NAME));
                age.setText(snapshot.getString(UserInfoActivity.USER_AGE));
//                date.setText(snapshot.getLong(UserInfoActivity.USER_CREATED) + "");
            }
        });

    }
}
