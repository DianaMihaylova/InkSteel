package com.ink_steel.inksteel.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.ToxicBakery.viewpager.transforms.FlipHorizontalTransformer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.adapters.ExploreAdapter;
import com.ink_steel.inksteel.helpers.ConstantUtils;
import com.ink_steel.inksteel.model.User;
import com.tmall.ultraviewpager.UltraViewPager;

import java.util.ArrayList;

public class ExploreFragment extends Fragment {

    public static ArrayList<User> users = new ArrayList<>();
    private ExploreAdapter mAdapter;


    public ExploreFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        Switch autoScroll = (Switch) view.findViewById(R.id.switch_auto_scroll);
        Button likeBtn = view.findViewById(R.id.btn_like);
        Button unlikeBtn = view.findViewById(R.id.btn_unlike);

        UltraViewPager ultraViewPager = view.findViewById(R.id.ultra_viewpager);
        ultraViewPager.setScrollMode(UltraViewPager.ScrollMode.VERTICAL);
        ultraViewPager.setPageTransformer(true, new FlipHorizontalTransformer());
        ultraViewPager.setInfiniteLoop(true);
        ultraViewPager.setAutoScroll(5000);

        ExploreAdapter adapter = new ExploreAdapter((getActivity()), users);
        ultraViewPager.setAdapter(adapter);

        mAdapter = new ExploreAdapter(getActivity().getApplicationContext());

        ultraViewPager.setAdapter(mAdapter);

        autoScroll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ultraViewPager.setAutoScroll(5000);
                } else {
                    ultraViewPager.disableAutoScroll();
                }
            }
        });

        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "You like it!", Toast.LENGTH_SHORT).show();
            }
        });

        unlikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "You not like it!", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        ConstantUtils.FIRESTORE_USERS_REFERENCE
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                users.clear();
                                final String mail = document.getId();
                                DocumentReference docRef = ConstantUtils.FIRESTORE_USERS_REFERENCE.document(mail);
                                docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                    @Override
                                    public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                                        String name = documentSnapshot.getString("userName");
                                        String city = documentSnapshot.getString("userCity");
                                        String pic = documentSnapshot.getString("userProfileImage");
                                        User u = new User(mail, name, city, pic);
                                        users.add(u);
                                    }
                                });
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }
}