package app.jongyeop2.fireinthehouse;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.util.Log;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by Han on 2016-10-24.
 */

public class MyFcmListenerService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage message) {
        String from = message.getFrom();
        Map<String, String> data = message.getData();
        String title = data.get("title");
        String msg = data.get("message");
        String date = data.get("date");

        Log.v("data.message", msg);
        Log.v("len of data.message", "" + msg.length());

        SharedPreferences historyFile = getSharedPreferences("history", 1);
        String historyStr = historyFile.getString("history", "");
        SharedPreferences.Editor editor = historyFile.edit();
        editor.putString("history", historyStr + title + "/" + msg + "/" + date + "__");
        editor.commit();

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("message", msg);
        intent.putExtra("date", date);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        // 벨소리
        /*
        long[] pattern = {0, 3000, 500, 3000, 500, 3000, 500, 3000, 500, 3000, 500, 3000, 500, 3000, 500, 3000, 500, 3000,
                             3000, 500, 3000, 500, 3000, 500, 3000, 500, 3000, 500, 3000, 500, 3000, 500, 3000, 500, 3000,
                             3000, 500, 3000, 500, 3000, 500, 3000, 500, 3000, 500, 3000, 500, 3000, 500, 3000, 500, 3000,
                             3000, 500, 3000, 500, 3000, 500, 3000, 500, 3000, 500, 3000, 500, 3000, 500, 3000, 500, 3000};
        // 약 2분간 진동
        */
        long[] pattern = {0, 1000};

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(msg)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setVibrate(pattern)
                .setSound(soundUri);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
                        PowerManager.ACQUIRE_CAUSES_WAKEUP |
                        PowerManager.ON_AFTER_RELEASE
                , "hello");
        wl.acquire();
        // Test Version(Screen off after 5 seconds)
        //wl.acquire(5);
    }
}

