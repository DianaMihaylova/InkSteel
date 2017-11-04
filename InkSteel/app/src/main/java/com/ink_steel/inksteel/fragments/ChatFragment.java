package com.ink_steel.inksteel.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.adapters.MessageAdapter;
import com.ink_steel.inksteel.data.DatabaseManager;
import com.ink_steel.inksteel.model.ChatRoom;
import com.ink_steel.inksteel.model.Message;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class ChatFragment extends Fragment implements DatabaseManager.ChatRoomCreatedListener,
        DatabaseManager.OnMessagesLoadedListener, DatabaseManager.ChatListener {

    private ImageView mImageView;
    private TextView mTextView;
    private DatabaseManager mManager;
    private ArrayList<Message> mMessages;
    private MessageAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private ImageButton mMsgbtn;
    private ChatRoom mChatRoom;

    public ChatFragment() {
    }

    public static ChatFragment newInstance(String email) {
        ChatFragment chatFragment = new ChatFragment();
        Bundle bundle = new Bundle(1);
        bundle.putString("email", email);
        chatFragment.setArguments(bundle);
        return chatFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        mImageView = view.findViewById(R.id.asdfg);
        mTextView = view.findViewById(R.id.qwerty);
        mMsgbtn = view.findViewById(R.id.chat_send_btn);
        final EditText messageEt = view.findViewById(R.id.chat_message_et);

        String email = getArguments().getString("email");
        mManager = DatabaseManager.getInstance();
        mChatRoom = mManager.isChatRoomCreated(email);
        if (mChatRoom == null)
            mManager.createChatRoom(email, this);
        else {
            displayChatRoom();
            loadChatRoomMessages();
        }

        mMsgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = messageEt.getText().toString();
                if (!msg.isEmpty()) {
                    Message message = new Message(mManager.getCurrentUser().getName(), msg,
                            new Date().getTime());
                    mManager.addMessage(message, mChatRoom);
                    messageEt.setText("");
                }
            }
        });

        mMessages = new ArrayList<>();
        mRecyclerView = view.findViewById(R.id.chat_rv);
        mAdapter = new MessageAdapter(getActivity(), mMessages,
                mManager.getCurrentUser().getName());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    private void displayChatRoom() {
        Picasso.with(getActivity())
                .load(mChatRoom.getProfilePicture())
                .into(mImageView);
        mTextView.setText(mChatRoom.getEmail());
    }

    @Override
    public void onChatRoomCreated(ChatRoom chatRoom) {
        mChatRoom = chatRoom;
        displayChatRoom();
        loadChatRoomMessages();
    }

    @Override
    public void onMessagesLoaded() {
        mMessages.addAll(mManager.getChatMessages());
        mRecyclerView.scrollToPosition(mMessages.size() - 1);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onMessageAdded(Message message) {
        mMessages.add(message);
        mRecyclerView.scrollToPosition(mMessages.size() - 1);
        mAdapter.notifyItemInserted(mMessages.size() - 1);
    }

    @Override
    public void onStop() {
        mManager.unregisterChatListener();
        super.onStop();
    }

    public void loadChatRoomMessages() {
        mManager.getChatMessagesById(mChatRoom.getChatId(), this);
    }
}