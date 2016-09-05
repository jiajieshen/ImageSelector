package com.fubaisum.imageselector.lib;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

/**
 * Created by sum on 9/1/16.
 */
class PermissionHelper {

    private static final int REQUEST_ACCESS_EXTERNAL_STORAGE = 100;
    private static final int REQUEST_SHOW_CAMERA = 101;

    private OnPermissionListener onPermissionListener;

    public void accessExternalStorageWithCheck(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    activity.getString(R.string.is_permission_rationale_write_storage),
                    REQUEST_ACCESS_EXTERNAL_STORAGE);
        } else {
            onPermissionListener.accessExternalStorage();
        }
    }

    public void showCameraWithCheck(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission(activity,
                    Manifest.permission.CAMERA,
                    activity.getString(R.string.is_permission_rationale_camera),
                    REQUEST_SHOW_CAMERA);
        } else {
            onPermissionListener.showCamera();
        }
    }

    private void requestPermission(final Activity activity, final String permission, String rationale, final int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            new AlertDialog.Builder(activity)
                    .setTitle(R.string.is_permission_dialog_title)
                    .setMessage(rationale)
                    .setPositiveButton(R.string.is_permission_dialog_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
                        }
                    })
                    .setNegativeButton(R.string.is_permission_dialog_cancel, null)
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ACCESS_EXTERNAL_STORAGE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onPermissionListener.accessExternalStorage();
                } else {
                    onPermissionListener.onAccessExternalStorageDenied();
                }
            }
            break;
            case REQUEST_SHOW_CAMERA: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onPermissionListener.showCamera();
                } else {
                    onPermissionListener.onCameraDenied();
                }
            }
            break;
        }
    }

    /**
     *
     */
    public interface OnPermissionListener {

        void showCamera();

        void onCameraDenied();

        void accessExternalStorage();

        void onAccessExternalStorageDenied();
    }

    public void setOnPermissionListener(OnPermissionListener onPermissionListener) {
        this.onPermissionListener = onPermissionListener;
    }
}
