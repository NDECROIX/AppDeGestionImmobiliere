package com.openclassrooms.realestatemanager.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Philippe on 21/02/2018.
 */

public class Utils {

    /**
     * Convert dollars into euros
     *
     * @param dollars value in dollars to be converted into euros
     * @return euros
     */
    public static int convertDollarToEuro(int dollars) {
        return (int) Math.round(dollars * 0.812);
    }

    /**
     * Convert euros into dollars
     *
     * @param euros value in euros to be converted into dollars
     * @return dollars
     */
    public static int convertEuroToDollar(int euros) {
        return (int) Math.round(euros / 0.812);
    }

    /**
     * Convert the currently date time to format dd/MM/yyyy
     *
     * @return date into dd/MM/yyyy format
     */
    public static String getTodayDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return dateFormat.format(new Date());
    }

    /**
     * Check the internet connection
     *
     * @param context application context
     * @return true if internet is available
     */
    public static Boolean isInternetAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    /**
     * Convert string to 32 HEX
     * @param data Data to hash
     * @return 32 Hex
     */
    public static String convertStringMd5(String data) {
        String theHash = null;
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(data.getBytes(), 0, data.length());
            StringBuilder dataBuilder = new StringBuilder(new BigInteger(1, messageDigest.digest()).toString(16));
            while (dataBuilder.length() < 32) {
                dataBuilder.insert(0, "0");
            }
            data = dataBuilder.toString();
            theHash = data;
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        return theHash;
    }

    /**
     * Get bytes from photo
     * @param inputStream stream on uri photo
     * @return bytes from the photo
     * @throws IOException fail to read inputStream
     */
    public static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
}
