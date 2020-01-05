package com.openclassrooms.realestatemanager.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Class useful for photo
 */
public class UtilsPhoto {

    /**
     * Create path for the photo
     *
     * @return Image file
     * @throws IOException Create temp file
     */
    public static File createImageFile(Context context) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    /**
     * Add the current picture to the gallery
     */
    public static void galleryAddPic(Context context, String currentPhotoPath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    /**
     * Move the bitmap in a new directory
     *
     * @param data Bitmap uri
     */
    public static String saveBitmapToThePath(Context context, Uri data) {
        String path = "";
        try {
            InputStream iStream = context.getContentResolver().openInputStream(data);
            byte[] inputData = new byte[0];
            if (iStream != null) {
                inputData = Utils.getBytes(iStream);
            }
            File image = createImageFile(context);
            FileOutputStream out = new FileOutputStream(image);
            out.write(inputData);
            out.close();
            path = image.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }
}
