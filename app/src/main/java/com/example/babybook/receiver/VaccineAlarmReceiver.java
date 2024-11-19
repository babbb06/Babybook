package com.example.babybook.receiver;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.babybook.R;
import com.example.babybook.utils.VaccineNotificationUtil;

import java.util.List;

public class VaccineAlarmReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "vaccine_reminder_channel";
    private static final String CHANNEL_NAME = "Vaccine Reminders";
    private static final String CHANNEL_DESC = "Notifications for upcoming vaccine schedules";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive called");

        String birthDate = intent.getStringExtra("birthDate");
        String childName = intent.getStringExtra("childName");

        Log.d(TAG, "Received birthDate: " + birthDate + ", childName: " + childName);

        if (birthDate != null && childName != null) {
            List<String> reminders = VaccineNotificationUtil.getUpcomingVaccineReminders(birthDate);
            Log.d(TAG, "Number of reminders: " + reminders.size());

            for (int i = 0; i < reminders.size(); i++) {
                Log.d(TAG, "Showing notification " + i + ": " + reminders.get(i));
                showNotification(context, childName, reminders.get(i), i);
            }
        }
    }

    private void showNotification(Context context, String childName, String message, int notificationId) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create notification channel for Android 8.0 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(CHANNEL_DESC);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{1000, 1000, 1000, 1000, 1000});
            notificationManager.createNotificationChannel(channel);
        }

        // Get default notification sound
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // Build the notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.baby_book_logo)
                .setContentTitle("Vaccine Reminder for " + childName)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});

        // Show the notification
        notificationManager.notify(notificationId, notificationBuilder.build());
    }
}