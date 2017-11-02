package com.ink_steel.inksteel.helpers;

import android.app.Fragment;

import com.ink_steel.inksteel.activities.LoginActivity;

public class Listeners {

    public interface OnLoginActivityButtonClickListener {
        void onButtonClick(LoginActivity.ButtonType buttonType, String email, String password);
    }

    public interface OnReplaceFragment {
        void replaceFragment(Fragment fragment);
    }

    public interface PostClickListener {
        void onPostClick(int position);
    }

    public interface GalleryImageLongClickListener {
        void onGalleryImageLongClick(int position, boolean isLongClick);
    }

    public interface FriendClickListener {
        void onFriendClick(int position);
    }

    public interface StudioClickListener {
        void onStudioClick(int position);
    }

    public interface ChatListClickListener {
        void onChatItemClick(int position);
    }
}
