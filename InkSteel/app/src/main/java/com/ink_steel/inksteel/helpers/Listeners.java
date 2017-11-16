package com.ink_steel.inksteel.helpers;

import android.app.Fragment;

public class Listeners {

    public interface OnLoginActivityButtonClickListener {
//        void onButtonClick(LoginActivity.ButtonType buttonType, String email, String password);
    }

    public interface ShowSnackBarListener {
        void showSnackBar(String message);

        void dismissSnackBar();
    }

    public interface OnReplaceFragmentListener {
        void replaceFragment(Fragment fragment);
    }

    public interface PostClickListener {
        void onPostClick(int position);

        void onPostsEnding();
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