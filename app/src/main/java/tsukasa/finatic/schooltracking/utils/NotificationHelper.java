package tsukasa.finatic.schooltracking.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;

import tsukasa.finatic.schooltracking.R;

public class NotificationHelper extends ContextWrapper {
    public static final String KD_CHANNEL_ID ="com.tsukasa.finatic.schooltracking";
    private static final String KD_CHANNEL_NAME = "SchoolTracking";


    private NotificationManager manager;

    public NotificationHelper(Context base) {
        super(base);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createChannel();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel kdChannel = new NotificationChannel(KD_CHANNEL_ID,KD_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        kdChannel.enableLights(false);
        kdChannel.enableVibration(true);
        kdChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(kdChannel);
    }

    public NotificationManager getManager() {
        if(manager==null)
            manager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        return manager;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getRealTimeTrackingNotification(String title, String content, Uri defaultSound) {

        return new Notification.Builder(getApplicationContext(),KD_CHANNEL_ID)
                //setting notification icon
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(title)
                .setContentText(content)
                .setSound(defaultSound)
                .setAutoCancel(false);
    }
}
