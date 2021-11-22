package com.example.rouminder.helpers;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.rouminder.MainActivity;
import com.example.rouminder.R;
import com.example.rouminder.data.goalsystem.Goal;
import com.example.rouminder.receivers.NotifyAlarmReceiver;

import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public class GoalNotificationHelper {
    public static String CHANNEL_ID;
    private final Context context;
    private final Map<Integer, PendingIntent> pendingIntentMap;

    public GoalNotificationHelper(Context context) {
        this.context = context;
        this.pendingIntentMap = new HashMap<>();

        createNotificationChannel();
    }

    /**
     * Show notification of a goal.
     *
     * @param context a context.
     * @param goal    a goal to be notified.
     */
    public static void showNotification(Context context, Goal goal) {
        if (goal == null)
            return;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(goal.getName())
                .setContentText(goal.progressToString())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        Intent mainIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 2, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.notify(goal.getId(), builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Register a goal reminder notification.
     *
     * @param goal a goal to be registered.
     */
    public void registerGoal(Goal goal) {
        Intent alarmIntent = new Intent(context, NotifyAlarmReceiver.class);
        alarmIntent.putExtra("goal_id", goal.getId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        pendingIntentMap.put(goal.getId(), pendingIntent);

        long duration = ChronoUnit.MILLIS.between(goal.getStartTime(), goal.getEndTime());
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.set(AlarmManager.RTC_WAKEUP, goal.getStartTime().toInstant(ZoneOffset.UTC).toEpochMilli() + duration / 2, pendingIntent);
    }

    /**
     * Unregister a goal reminder notification
     *
     * @param goal a goal to be unregistered.
     */
    public void unregisterGoal(Goal goal) {
        PendingIntent pendingIntent = pendingIntentMap.get(goal.getId());
        if (pendingIntent == null)
            return;
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        pendingIntentMap.remove(goal.getId(), pendingIntent);
    }
}
