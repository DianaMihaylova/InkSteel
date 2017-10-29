package com.ink_steel.inksteel.fragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.activities.HomeActivity;
import com.ink_steel.inksteel.helpers.ConstantUtils;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import static com.ink_steel.inksteel.fragments.AddPost1Fragment.mId;

public class AddPostFragment extends Fragment {

    private String postUUID;

    private Fragment currentFragment;

    public AddPostFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_post, container, false);

        postUUID = UUID.randomUUID().toString();
        setFragment(new AddPost1Fragment());

        Button nextBtn = (Button) view.findViewById(R.id.add_post_next_btn);
        Button cancelBtn = (Button) view.findViewById(R.id.add_post_cancel);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFragment instanceof AddPost2Fragment) {
                    ((HomeActivity) getActivity()).replaceFragment(new ScreenSlidePageFragment());
                } else {
                    setFragment(new AddPost2Fragment());
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // delete from firebase
                getFragmentManager().popBackStack();
            }
        });

        return view;
    }

    private void setFragment(Fragment fragment) {

        Bundle bundle = new Bundle(1);
        bundle.putString("id", postUUID);
        fragment.setArguments(bundle);

        currentFragment = fragment;

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.add_post_placeholder, fragment)
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();
    }
}
