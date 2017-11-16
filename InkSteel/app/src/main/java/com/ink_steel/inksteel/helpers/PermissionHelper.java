package com.ink_steel.inksteel.helpers;

import android.Manifest;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v13.app.FragmentCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

public class PermissionHelper {

    public static final int PERMISSION_READ_EXTERNAL_STORAGE = 1;
    public static final int PERMISSION_COARSE_LOCATION = 2;

    public static void requestPermission(Fragment fragment, PermissionType type) {
        if (ContextCompat.checkSelfPermission(fragment.getActivity(),
                type.getPermission()) != PackageManager.PERMISSION_GRANTED) {
            if (FragmentCompat.shouldShowRequestPermissionRationale(
                    fragment,
                    type.getPermission())) {
                showDialog(fragment, type);
            } else {
                FragmentCompat.requestPermissions(
                        fragment,
                        new String[]{type.getPermission()},
                        type.getRequestCode());
            }
        }
    }

    private static void showDialog(final Fragment fragment, final PermissionType type) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(fragment.getActivity());
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle(type.getDialogTitle());
        alertBuilder.setMessage(type.getDialogMessage());
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        FragmentCompat.requestPermissions(fragment,
                                new String[]{type.getPermission()},
                                type.getRequestCode());
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    public enum PermissionType {
        LOCATION(Manifest.permission.ACCESS_COARSE_LOCATION,
                "Location permission",
                "Permission is necessary to show the nearby studios!",
                PERMISSION_COARSE_LOCATION),
        STORAGE(Manifest.permission.READ_EXTERNAL_STORAGE,
                "Read external storage",
                "Permission is necessary to get images from your device!",
                PERMISSION_READ_EXTERNAL_STORAGE);

        private String permission;
        private String dialogMessage;
        private String dialogTitle;
        private int requestCode;

        PermissionType(String permission, String dialogMessage, String dialogTitle, int requestCode) {
            this.permission = permission;
            this.dialogMessage = dialogMessage;
            this.dialogTitle = dialogTitle;
            this.requestCode = requestCode;
        }

        public String getPermission() {
            return permission;
        }

        public String getDialogTitle() {
            return dialogTitle;
        }

        public String getDialogMessage() {
            return dialogMessage;
        }

        public int getRequestCode() {
            return requestCode;
        }
    }
}