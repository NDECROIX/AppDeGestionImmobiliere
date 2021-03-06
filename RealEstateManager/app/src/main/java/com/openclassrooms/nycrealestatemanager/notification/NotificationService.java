package com.openclassrooms.nycrealestatemanager.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.openclassrooms.nycrealestatemanager.R;
import com.openclassrooms.nycrealestatemanager.controllers.activities.MainActivity;
import com.openclassrooms.nycrealestatemanager.utils.Utils;

import java.util.Objects;

public class NotificationService extends FirebaseMessagingService {

    private static final int NOTIFICATION_ID = 777;
    private static final String NOTIFICATION_TAG = "FIREBASE_REM";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() == 0 || remoteMessage.getFrom() == null) {
            return;
        }
        String title;
        String message;

        // Check if message contains a data payload.
        if (remoteMessage.getFrom().contains(getString(R.string.notification_service_new_agent_topic_name))) {
            title = getString(R.string.notification_service_new_agent_title);
            String firstName = remoteMessage.getData().get(getString(R.string.notification_service_new_agent_first_name_key));
            if (Objects.equals(firstName, "test")) return;
            message = firstName
                    + " " + remoteMessage.getData().get(getString(R.string.notification_service_new_agent_last_name_key));
        } else if (remoteMessage.getFrom().contains(getString(R.string.notification_service_new_property_topic_name))) {
            title = getString(R.string.notification_service_new_property_title);
            String p = remoteMessage.getData().get(getString(R.string.notification_service_new_agent_price_key));
            String type = remoteMessage.getData().get(getString(R.string.notification_service_new_agent_type_key));
            if (Objects.equals(type, "test")) return;
            if (p != null) {
                Double price = Double.valueOf(p);
                message = String.format("%s $ %s ", type, Utils.getPrice(price));
            } else {
                message = type;
            }
        } else {
            return;
        }
        sendVisualNotification(title, message);
    }

    private void sendVisualNotification(String title, String messageBody) {

        // Create an Intent that will be shown when user will click on the Notification
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        // Create a Style for the Notification
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(title);
        inboxStyle.addLine(messageBody);

        // Create a Channel (Android 8)
        String channelId = getString(R.string.firebase_notification_channel_id);

        // Build a Notification object
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_new_york_notif)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(title)
                        .setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentIntent(pendingIntent)
                        .setStyle(inboxStyle);

        // Add the Notification to the Notification Manager and show it.
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Support Version >= Android 8
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = getString(R.string.notification_service_message_from_firebase);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(mChannel);
            }
        }

        // Show notification
        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, notificationBuilder.build());
        }
    }
}