package tsukasa.finatic.schooltracking.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

import tsukasa.finatic.schooltracking.Model.User;
import tsukasa.finatic.schooltracking.R;
import tsukasa.finatic.schooltracking.utils.Common;
import tsukasa.finatic.schooltracking.utils.NotificationHelper;

public class MyFCMService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        if(message.getData()!=null){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                sendNotificationWithChannel(message);
            else
                sendNotification(message);
            //after sending and accepting friend req

            addRequestToUserInformation(message.getData());


        }

    }

    private void addRequestToUserInformation(Map<String, String> data) {

        //Pending Requests
        DatabaseReference friend_request = FirebaseDatabase.getInstance()
                .getReference(Common.USER_INFORMATION)
                .child(data.get(Common.TO_UID))
                .child(Common.FRIEND_REQUEST);

        User user = new User();
        user.setUid(data.get(Common.FROM_UID));
        user.setEmail(data.get(Common.FROM_NAME));

        friend_request.child(user.getUid()).setValue(user);


    }

    private void sendNotification(RemoteMessage message) {
        Map<String,String> data = message.getData();
        String title = "Friend Requests";
        String content = "New Friend Request from "+data.get(Common.FROM_NAME);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder= new NotificationCompat.Builder(this,NotificationHelper.KD_CHANNEL_ID);
                //setting notification icon
                builder.setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(title)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setPriority(Notification.PRIORITY_MAX)
                .setContentText(content)
                .setSound(defaultSound)
                .setAutoCancel(false);

        NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1,builder.build());




    }

    private void sendNotificationWithChannel(RemoteMessage message) {
        Map<String,String> data = message.getData();
        String title = "Friend Requests";
        String content = "New Friend Request from "+data.get(Common.FROM_NAME);


        NotificationHelper helper;
        Notification.Builder builder = null;

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        helper = new NotificationHelper(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = helper.getRealTimeTrackingNotification(title,content,defaultSound);
        }

        helper.getManager().notify(new Random().nextInt(),builder.build());


    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            final DatabaseReference tokens = FirebaseDatabase.getInstance()
                    .getReference(Common.TOKENS);
            tokens.child(user.getUid()).setValue(token);
        }
    }
}