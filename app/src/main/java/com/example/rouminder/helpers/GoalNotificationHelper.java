package com.example.rouminder.helpers;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.rouminder.activities.MainActivity;
import com.example.rouminder.R;
import com.example.rouminder.data.goalsystem.Goal;
import com.example.rouminder.receivers.NotifyAlarmReceiver;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GoalNotificationHelper {
    public static String CHANNEL_ID = "rouminder_goal_notification_channel";
    public static String GROUP_SINGLE = "com.example.rouminder.SINGLE";
    public static String GROUP_ONGOING = "com.example.rouminder.ONGOING";
    private final Context context;
    private final Map<Integer, PendingIntent> pendingIntentMap;

    public GoalNotificationHelper(Context context) {
        this.context = context;
        this.pendingIntentMap = new HashMap<>();
    }

    /**
     * Show notification of a goal.
     *
     * @param goal    a goal to be notified.
     */
    public void setNotification(Goal goal, NotificationType type) {
        if (goal == null)
            return;
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        long hoursLeft = ChronoUnit.HOURS.between(goal.getEndTime(), LocalDateTime.now());
        long minutesLeft = ChronoUnit.MINUTES.between(goal.getEndTime(), LocalDateTime.now()) % 60;
        String hoursLeftText = hoursLeft + "시간";
        String minutesLeftText = minutesLeft + "분";
        String timeLeftText = hoursLeft > 0 ? hoursLeftText + " " + minutesLeftText : minutesLeftText;

        switch(type) {
            case ONGOING:
                builder.setContentTitle(context.getString(R.string.notification_ongoing_title, goal.getName()))
                        .setContentText(context.getString(R.string.notification_ongoing_content, goal.progressToString(), timeLeftText))
                        .setGroup(GROUP_ONGOING)
                        .setAutoCancel(false)
                        .setOngoing(true);
                break;
            case AT_START:
                builder.setContentTitle(context.getString(R.string.notification_at_start_title, goal.getName()))
                        .setContentText(context.getString(R.string.notification_at_start_content, goal.getName()))
                        .setGroup(GROUP_SINGLE);
                break;
            case BEFORE_END:
                builder.setContentTitle(context.getString(R.string.notification_before_end_title, goal.getName()))
                    .setContentText(context.getString(R.string.notification_before_end_content, goal.getName(), timeLeftText))
                    .setGroup(GROUP_SINGLE);
                break;
            default:
                Log.d("notify", "invalid type");
                return;
        }
        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.notify(type.name(), goal.getId(), builder.build());

        Log.d("notify", "id: " + goal.getId());
    }

    public void unsetNotification(Goal goal, String typeName) {
        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        String tag = NotificationType.valueOf(typeName).name();
        manager.cancel(tag, goal.getId());
    }

    public void createNotificationChannel() {
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
        setAlarmAtGoalStart(goal);
        setAlarmBeforeGoalEnd(goal);
        setNotification(goal, NotificationType.ONGOING);

        Log.d("alarm", "register " + goal.getId());
    }

    private void setAlarmAtGoalStart(Goal goal) {
        Intent intent = new Intent(context, NotifyAlarmReceiver.class);
        intent.putExtra("goal_id", goal.getId());
        intent.putExtra("notify_type", NotificationType.AT_START.name());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

//        Instant at = goal.getStartTime()
//                .toInstant(ZoneOffset.systemDefault().getRules().getOffset(Clock.systemDefaultZone().instant()));
        long at = goal.getStartTime()
                .toInstant(ZoneOffset.of(TimeZone.getDefault().getID()))
                .toEpochMilli();
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.set(AlarmManager.RTC_WAKEUP, at, pendingIntent);

        pendingIntentMap.put(goal.getId(), pendingIntent);
    }

    private void setAlarmBeforeGoalEnd(Goal goal) {
        Intent intent = new Intent(context, NotifyAlarmReceiver.class);
        intent.putExtra("goal_id", goal.getId());
        intent.putExtra("notify_type", NotificationType.BEFORE_END.name());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        long at = goal.getEndTime()
                .minusMinutes(5)
                .toInstant(ZoneOffset.of(TimeZone.getDefault().getID()))
                .toEpochMilli();
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.set(AlarmManager.RTC_WAKEUP, at, pendingIntent);
        pendingIntentMap.put(goal.getId(), pendingIntent);
    }

    /**
     * Unregister a goal reminder notification
     *
     * @param id an id of a goal to be unregistered.
     */
    public void unregisterGoal(int id) {
        PendingIntent pendingIntent = pendingIntentMap.get(id);
        if (pendingIntent == null)
            return;
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        pendingIntentMap.remove(id, pendingIntent);
        Log.d("alarm", "unregister " + id);
    }

    public enum NotificationType {
        AT_START,
        ONGOING,
        BEFORE_END
    }
}
