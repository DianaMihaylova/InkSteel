package com.ink_steel.inksteel;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.google.firebase.auth.FirebaseAuth;;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import java.util.HashMap;
import java.util.Map;

public class UserInfoActivity extends AppCompatActivity {

    private static final String USER_NAME = "userName";
    private static final String USER_CITY = "userCity";
    private static final String USER_AGE = "userAge";

    private EditText userName, age, city;
    private Button cancelBtn, saveBtn;
    private DocumentReference saveInfo = FirebaseFirestore.getInstance().collection("users").
            document(FirebaseAuth.getInstance().getCurrentUser().getEmail());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        userName = (EditText) findViewById(R.id.user_name);
        age = (EditText) findViewById(R.id.user_age);
        city = (EditText) findViewById(R.id.user_city);
        cancelBtn = (Button) findViewById(R.id.button_cancel);
        saveBtn = (Button) findViewById(R.id.button_save);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = userName.getText().toString();
                String userCity = city.getText().toString();
                String userAge = age.getText().toString();

                Map<String, Object> data = new HashMap<>();
                data.put(USER_NAME, username);
                data.put(USER_CITY, userCity);
                data.put(USER_AGE, userAge);
                saveInfo.set(data);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        saveInfo.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    userName.setText(documentSnapshot.getString(USER_NAME));
                    city.setText(documentSnapshot.getString(USER_CITY));
                    age.setText(documentSnapshot.getString(USER_AGE));
                }
            }
        });
    }
}
