package com.example.babybook;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class ReminderWorker extends Worker {

    private static final String CHANNEL_ID = "ReminderChannel";

    public ReminderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String appointmentDate = getInputData().getString("appointmentDate");
        String appointmentTime = getInputData().getString("appointmentTime");
        String parentFullName = getInputData().getString("parentFullName");

        // Debugging logs
        if (appointmentDate == null || appointmentTime == null || parentFullName == null) {
            return Result.failure(); // Fail if any required data is missing
        }

        createNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.notification)
                .setContentTitle("Appointment Reminder")
                .setContentText(parentFullName + ", you have an appointment on " + appointmentDate + " at " + appointmentTime)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(0, builder.build());

        // Reschedule the worker
        ScheduleReminderUtil.scheduleReminder(getApplicationContext(), appointmentDate, appointmentTime, parentFullName);

        return Result.success();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Reminder Channel";
            String description = "Channel for appointment reminders";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
