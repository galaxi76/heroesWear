package app.heroeswear.com.heroesmakers.login.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import app.heroeswear.com.heroesmakers.R;
import app.heroeswear.com.heroesmakers.login.Activities.AreYouOkActivity;
import app.heroeswear.com.heroesmakers.login.enums.NotificationChannelType;

public class NotificationFactory {

    public static final int NOTIFICATION_ID = 1111;

    public static void build(Context context,
                             String title,
                             String body,
                             NotificationChannelType channelType) {

        Intent intent = new Intent(context, AreYouOkActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelType.getId())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setSound(getNotificationSound(context))
                .setVibrate(new long[]{0, 500, 1000})
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);
        notify(context, NOTIFICATION_ID, builder);
    }

    private static void notify(Context context, int id, NotificationCompat.Builder builder) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(id, builder.build());
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    public static void createChannel(Context context, NotificationChannelType channelType) {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                .build();

        NotificationChannel channel = new NotificationChannel(channelType.getId(),
                channelType.getName(),
                channelType.getImportance());
        channel.setSound(getNotificationSound(context), audioAttributes);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }
    }

    private static Uri getNotificationSound(Context context) {
        return Uri.parse("android.resource://" + context.getApplicationContext().getPackageName() + "/" + R.raw.push_notification_sound);
    }
}
