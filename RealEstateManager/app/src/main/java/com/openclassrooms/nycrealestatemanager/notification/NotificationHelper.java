package com.openclassrooms.nycrealestatemanager.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.openclassrooms.nycrealestatemanager.R;
import com.openclassrooms.nycrealestatemanager.model.Property;
import com.openclassrooms.nycrealestatemanager.utils.Utils;

/**
 * Manage local notifications
 */
public abstract class NotificationHelper {

    /**
     * Display a notification
     *
     * @param property Property created
     * @param context  Application context
     */
    public static void displayNotification(Property property, Context context) {
        String title = property.getType() + " added.";
        String content = String.format("$ %s ", Utils.getPrice(property.getPrice()));
        Notification notification = NotificationHelper.createNotification(context, title, content);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(property.hashCode(), notification);
    }

    /**
     * Create a notification
     *
     * @param context     Application context
     * @param textTitle   Notification title
     * @param textContent Notification content
     * @return Notification
     */
    private static Notification createNotification(Context context, String textTitle, String textContent) {
        createNotificationChannel(context);
        return new NotificationCompat.Builder(context, context.getString(R.string.local_notification_channel_id))
                .setSmallIcon(R.drawable.ic_new_york_notif)
                .setContentTitle(textTitle)
                .setContentText(textContent)
                .setPriority(NotificationCompat.PRIORITY_MAX).build();
    }

    /**
     * Create a chanel for the local notification.
     *
     * @param context Application context
     */
    private static void createNotificationChannel(Context context) {
        String channelId = context.getString(R.string.local_notification_channel_id);
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.local_notification_channel_name);
            String description = context.getString(R.string.local_notification_channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);// Add the Notification to the Notification Manager and show it.
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}