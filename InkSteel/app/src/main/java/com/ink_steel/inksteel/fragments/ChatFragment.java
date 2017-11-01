package com.ink_steel.inksteel.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.data.DatabaseManager;
import com.ink_steel.inksteel.model.ChatRoom;
import com.squareup.picasso.Picasso;

public class ChatFragment extends Fragment implements DatabaseManager.ChatRoomCreatedListener {

    private ImageView mImageView;
    private TextView mTextView;
    private DatabaseManager mManager;

    public ChatFragment() {
    }

    private ChatRoom mChatRoom;

    public static ChatFragment newInstance(String userEmail) {
        ChatFragment chatFragment = new ChatFragment();
        Bundle bundle = new Bundle(1);
        bundle.putString("userEmail", userEmail);
        chatFragment.setArguments(bundle);
        return chatFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        String userEmail = getArguments().getString("userEmail");
        mManager = DatabaseManager.getInstance();
        mChatRoom = mManager.getChatRoomByOtherUser(this, userEmail);
        if (mChatRoom != null)
            displayChatRoom();

        mImageView = view.findViewById(R.id.asdfg);
        mTextView = view.findViewById(R.id.qwerty);

        return view;
    }

    private void displayChatRoom() {
        String email = mManager.getCurrentUser().getEmail();
        Picasso.with(getActivity())
                .load(mChatRoom.getOtherProfilePicture(email)).into(mImageView);
        mTextView.setText(mChatRoom.getOtherUserName(email));
    }

    @Override
    public void onChatRoomCreated(ChatRoom chatRoom) {
        mChatRoom = chatRoom;
        if (mChatRoom != null)
            displayChatRoom();
    }
}
