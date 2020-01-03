package com.openclassrooms.realestatemanager.base;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.openclassrooms.realestatemanager.R;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public abstract class BaseActivity extends AppCompatActivity {

    // Data to save on the rotation
    public static final String PROPERTY = "property";
    public static final String POIS = "pois";
    public static final String PHOTOS = "photos";

    public static final int RC_READ_WRITE = 854;
    public static final int RC_IMAGE_PERMS = 666;
    public static final int RC_CHOOSE_PHOTO = 333;
    public static final int RC_CAMERA_PERMS = 777;
    public static final int RC_TAKE_PHOTO = 999;

    /**
     * Display a toast
     *
     * @param context Activity context
     * @param message Message to display
     */
    protected static void showToastMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Display a custom toast
     *
     * @param context Activity context
     * @param message Message to display
     */
    public static void customToast(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        View view = toast.getView();
        view.getBackground().setColorFilter(context.getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.DARKEN);
        TextView text = view.findViewById(android.R.id.message);
        text.setTextColor(Color.WHITE);
        toast.show();
    }

    /**
     * Get the read external storage permission.
     */
    public void getReadExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(this,
                READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{READ_EXTERNAL_STORAGE},
                    RC_IMAGE_PERMS);
        }
    }

    /**
     * Get the read write external storage permission.
     */
    public void getReadWriteExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(this,
                WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE},
                    RC_READ_WRITE);
        }
    }

    /**
     * Get the Camera permission.
     */
    public void getCameraPermission() {
        if (ContextCompat.checkSelfPermission(this,
                CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{CAMERA, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE},
                    RC_CAMERA_PERMS);
        }
    }
}
