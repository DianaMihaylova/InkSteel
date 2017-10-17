package com.ink_steel.inksteel;

<<<<<<< HEAD
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
=======
import android.support.annotation.NonNull;
>>>>>>> a2508c322af589ccc65afb4144428bb60f7ee917
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

import com.ink_steel.inksteel.fragments.ScreenSlidePageFragment;

public class HomeActivity extends AppCompatActivity {

    private TextView name, date, age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

<<<<<<< HEAD
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
        transaction.replace(R.id.view_pager, fragment);
        transaction.commit();

=======
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
>>>>>>> a2508c322af589ccc65afb4144428bb60f7ee917

    }
}
