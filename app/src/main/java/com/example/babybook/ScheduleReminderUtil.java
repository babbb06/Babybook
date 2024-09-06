package com.example.babybook;

import android.content.Context;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class ScheduleReminderUtil {

    public static void scheduleReminder(Context context, String appointmentDate, String appointmentTime, String parentFullName) {
        Data inputData = new Data.Builder()
                .putString("appointmentDate", appointmentDate)
                .putString("appointmentTime", appointmentTime)
                .putString("parentFullName", parentFullName)
                .build();

        OneTimeWorkRequest reminderRequest = new OneTimeWorkRequest.Builder(ReminderWorker.class)
                .setInitialDelay(1, TimeUnit.MINUTES)
                .setInputData(inputData)
                .build();

        WorkManager.getInstance(context).enqueue(reminderRequest);
    }
}
