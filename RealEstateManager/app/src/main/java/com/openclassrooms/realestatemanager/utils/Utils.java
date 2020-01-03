package com.openclassrooms.realestatemanager.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Useful function
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
            messageDigest.update(data.toLowerCase().getBytes(), 0, data.length());
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

    public static String getPrice(Double price){
        NumberFormat format = NumberFormat.getInstance(Locale.US);
        format.setMinimumFractionDigits(0);
        return format.format(price);
    }

    /**
     * Get latitude and longitude from String address
     * @param context Application context
     * @param strAddress Address
     * @return LatLng of the address
     */
    public static LatLng getLocationFromAddress(Context context, String strAddress) {
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng latLng = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 1);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            latLng = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return latLng;
    }
}
