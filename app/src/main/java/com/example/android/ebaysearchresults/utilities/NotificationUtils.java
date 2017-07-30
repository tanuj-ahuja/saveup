package com.example.android.ebaysearchresults.utilities;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.example.android.ebaysearchresults.DetailActivity;
import com.example.android.ebaysearchresults.MainActivity;
import com.example.android.ebaysearchresults.NotificationsActivity;
import com.example.android.ebaysearchresults.R;

/**
 * Created by tanujanuj on 13/07/17.
 */

public class NotificationUtils {

    private static final int PRICE_REMINDER_PENDING_INTENT_ID=3417;
    private static final int PRICE_REMINDER_NOTIFICATION_ID=1138;


    public static void remindUserBecausePriceDropped(Context context,String price){

        NotificationCompat.Builder notificationBuilder=new NotificationCompat.Builder(context)
                .setColor(ContextCompat.getColor(context,R.color.colorPrimary))
                .setSmallIcon(R.drawable.ebay_notification)

                .setContentTitle(context.getString(R.string.price_drop_notification_title))
                .setContentText("hello")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.price_drop_notification_body)+price))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context))
                .setAutoCancel(true);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN){
            notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
        }
        NotificationManager notificationManager=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(PRICE_REMINDER_NOTIFICATION_ID,notificationBuilder.build());

    }


    private static PendingIntent contentIntent(Context context) {
        Intent startActivityIntent = new Intent(context, NotificationsActivity.class);

        return PendingIntent.getActivity(
                context,
                PRICE_REMINDER_PENDING_INTENT_ID,
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
    }
}
