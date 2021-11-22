package com.example.rouminder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String PRIMARY_CHANNEL_ID = "rouminder_notification_channel";
    private NotificationManager mNotificationManager;
    private static final int NOTIFICATION_ID = 0;


    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID
                    , "Test Notification", mNotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification from Rouminder");
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(context.getApplicationContext(), PRIMARY_CHANNEL_ID);
        notifyBuilder.setContentTitle("응애");
        // 콘텐츠 텍스트 설정할 수 있도록 수정해야함.
        notifyBuilder.setContentText("나 아기 학부생 학점 줘");
        notifyBuilder.setSmallIcon(R.drawable.ic_launcher_background);
        mNotificationManager.notify(NOTIFICATION_ID, notifyBuilder.build());
        Log.e("Alarm", "브로드캐스트 리시버");
    }
}