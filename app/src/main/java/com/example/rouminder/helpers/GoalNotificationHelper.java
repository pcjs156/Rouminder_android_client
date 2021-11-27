package com.example.rouminder.helpers;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.rouminder.MainApplication;
import com.example.rouminder.activities.MainActivity;
import com.example.rouminder.R;
import com.example.rouminder.data.goalsystem.Goal;
import com.example.rouminder.data.goalsystem.GoalManager;
import com.example.rouminder.receivers.NotifyAlarmReceiver;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GoalNotificationHelper {
    public static String CHANNEL_ID = "rouminder_goal_notification_channel";
    public static String GROUP_SINGLE = "com.example.rouminder.SINGLE";
    public static String GROUP_ONGOING = "com.example.rouminder.ONGOING";
    public static int GROUP_ONGOING_SUMMARY = -1;

    private static final int MINUTES_TO_ALERT_BEFORE_END = 5;
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
        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        long hoursLeft = ChronoUnit.HOURS.between(LocalDateTime.now(), goal.getEndTime());
        long minutesLeft = ChronoUnit.MINUTES.between(LocalDateTime.now(), goal.getEndTime()) % 60;
        String hoursLeftText = hoursLeft + "시간";
        String minutesLeftText = minutesLeft + "분";
        String timeLeftText = hoursLeft > 0 ? hoursLeftText + " " + minutesLeftText : minutesLeftText;

        switch(type) {
            case ONGOING:
                builder.setContentTitle(context.getString(R.string.notification_ongoing_title, goal.getName()))
                        .setContentText(context.getString(R.string.notification_ongoing_content, goal.progressToString(), timeLeftText))
                        .setGroup(GROUP_ONGOING)
                        .setAutoCancel(false)
                        .setOngoing(true)
                        .setProgress(goal.getTarget(), goal.getCurrent(), false)
                        .setShowWhen(false)
                        .setPriority(NotificationCompat.PRIORITY_LOW);
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

        if(type == NotificationType.ONGOING)
            setSummaryForOngoingNotification();

        Log.d("notify", "id: " + goal.getId()
                + ", type: " + type.name());
    }

    public void setSummaryForOngoingNotification() {
        NotificationManager manager = context.getSystemService(NotificationManager.class);
        GoalManager goalManager = ((MainApplication)context.getApplicationContext()).getGoalManager();
        List<StatusBarNotification> notifications = Arrays.stream(manager.getActiveNotifications()).filter(n -> n.getTag().equals(NotificationType.ONGOING.name())).collect(Collectors.toList());
        long total = notifications.size();
        long accomplished = notifications.stream().filter(n-> {
            Goal goal = goalManager.getGoal(n.getId());
            return goal != null && goal.isAccomplished();
        }).count();
        String summary = context.getString(R.string.notification_ongoing_summary_title, total, accomplished);

        Log.d("test", summary);
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("진행 중")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setStyle(new NotificationCompat.InboxStyle()
                        .setSummaryText(summary))
                .setGroup(GROUP_ONGOING)
                .setGroupSummary(true)
                .setShowWhen(false)
                .build();

        manager.notify(NotificationType.ONGOING.name(), GROUP_ONGOING_SUMMARY, notification);
    }

    public void unsetNotification(int id, NotificationType type) {
        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.cancel(type.name(), id);
        if(type == NotificationType.ONGOING) {
            setSummaryForOngoingNotification();
        }
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
        LocalDateTime now = LocalDateTime.now();
        if(!goal.isAfterEnd(now))
            setAlarmAtGoalStart(goal);
        if(!goal.isAfterEnd(now) && goal.getEndTime().minusMinutes(MINUTES_TO_ALERT_BEFORE_END).isAfter(now))
            setAlarmBeforeGoalEnd(goal);
        if(goal.isOnProgress(now)) {
            setNotification(goal, NotificationType.ONGOING);
        }
    }

    private void setAlarmAtGoalStart(Goal goal) {
        Intent intent = new Intent(context, NotifyAlarmReceiver.class);
        intent.putExtra("goal_id", goal.getId());
        intent.putExtra("notify_type", NotificationType.AT_START.name());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

//        Instant at = goal.getStartTime()
//                .toInstant(ZoneOffset.systemDefault().getRules().getOffset(Clock.systemDefaultZone().instant()));
        long at = goal.getStartTime()
                .toInstant(OffsetDateTime.now().getOffset())
                .toEpochMilli();
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.set(AlarmManager.RTC_WAKEUP, at, pendingIntent);

        pendingIntentMap.put(goal.getId(), pendingIntent);
        Log.d("alarm", "register " + goal.getId()
                + ", type: " + NotificationType.AT_START.name());
    }

    private void setAlarmBeforeGoalEnd(Goal goal) {
        Intent intent = new Intent(context, NotifyAlarmReceiver.class);
        intent.putExtra("goal_id", goal.getId());
        intent.putExtra("notify_type", NotificationType.BEFORE_END.name());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        long at = goal.getEndTime()
                .minusMinutes(MINUTES_TO_ALERT_BEFORE_END)
                .toInstant(OffsetDateTime.now().getOffset())
                .toEpochMilli();
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.set(AlarmManager.RTC_WAKEUP, at, pendingIntent);
        pendingIntentMap.put(goal.getId(), pendingIntent);
        Log.d("alarm", "register " + goal.getId()
                + ", type: " + NotificationType.BEFORE_END.name());
    }

    /**
     * Unregister a goal reminder notification
     *
     * @param id an id of a goal to be unregistered.
     */
    public void unregisterGoal(int id) {
        PendingIntent pendingIntent;
        while(pendingIntentMap.containsKey(id)) {
            pendingIntent = pendingIntentMap.remove(id);
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            manager.cancel(pendingIntent);
            for(NotificationType type: NotificationType.values()) {
                unsetNotification(id, type);
            }

            Log.d("alarm", "unregister " + id);
        }
    }

    public enum NotificationType {
        AT_START,
        ONGOING,
        BEFORE_END
    }
}
