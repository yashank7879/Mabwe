package mabwe.com.mabwe.fcmServices;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import mabwe.com.mabwe.R;
import mabwe.com.mabwe.activity.Activity_profile;
import mabwe.com.mabwe.activity.Activity_profileNotification;
import mabwe.com.mabwe.activity.DetailsActivity;
import mabwe.com.mabwe.activity.MainActivity;
import mabwe.com.mabwe.activity.ShowGroupDetailsActivity;

import static android.content.ContentValues.TAG;

/**
 * Created by mindiii on 15/5/18.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private String body = "";
    private String title = "";
    private String type = "";
    private String sound = "";
    private String userId = "";
    private String click_action = "";
    private String profileImage = "";
    private String notify_id = "";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.e("data: " ,""+remoteMessage.getData());
        Log.e("notification: " ,""+remoteMessage.getNotification().toString());

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message: " + remoteMessage.getData());

            userId = remoteMessage.getData().get("userId");
            body = remoteMessage.getData().get("body");
            type = remoteMessage.getData().get("type");
            sound = remoteMessage.getData().get("sound");
            title = remoteMessage.getData().get("title");
            click_action = remoteMessage.getData().get("click_action");
            profileImage = remoteMessage.getData().get("profileImage");
            notify_id = remoteMessage.getData().get("notify_id");
        }

        sendNotification(body,type,title,sound,userId,click_action,profileImage,notify_id);
    }

    private void sendNotification(String body, String type, String title, String sound, String userId, String click_action, String profileImage,String notify_id) {
        PendingIntent pendingIntent =null;
        switch (click_action) {
            case "ShowGroupDetailsActivity": {
                Intent intent = new Intent(this, ShowGroupDetailsActivity.class);
                intent.putExtra("body", body);
                intent.putExtra("type", type);
                intent.putExtra("title", title);
                intent.putExtra("sound", sound);
                intent.putExtra("userId", userId);
                intent.putExtra("click_action", click_action);
                intent.putExtra("notify_id", notify_id);
                intent.putExtra("profileImage", profileImage);

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

                break;
            }
            case "DetailsActivity": {
                Intent intent = new Intent(this, DetailsActivity.class);
                intent.putExtra("body", body);
                intent.putExtra("type", type);
                intent.putExtra("title", title);
                intent.putExtra("sound", sound);
                intent.putExtra("userId", userId);
                intent.putExtra("click_action", click_action);
                intent.putExtra("notify_id", notify_id);
                intent.putExtra("profileImage", profileImage);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

                break;
            }
           // case "connection_declined":
          //  case "connection_accept":
          //  case "connection_request":
            case "Activity_profileNotification":
                {
                Intent intent = new Intent(this, Activity_profileNotification.class);
                intent.putExtra("body", body);
                intent.putExtra("type", type);
                intent.putExtra("title", title);
                intent.putExtra("sound", sound);
                intent.putExtra("userId", userId);
                intent.putExtra("click_action", click_action);
                intent.putExtra("notify_id", notify_id);
                intent.putExtra("profileImage", profileImage);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
                break;
            }

            default: {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("body", body);
                intent.putExtra("type", type);
                intent.putExtra("title", title);
                intent.putExtra("sound", sound);
                intent.putExtra("userId", userId);
                intent.putExtra("click_action", click_action);
                intent.putExtra("notify_id", notify_id);
                intent.putExtra("profileImage", profileImage);

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
                break;
            }
        }

        String CHANNEL_ID = "mabwe.com.mabwe";// The id of the channel.
        CharSequence name = "MyChannal";// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = null;


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(this.title)
                .setContentText(body)
                .setPriority(Notification.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.drawable.mabwe);
            notificationBuilder.setColor(getResources().getColor(R.color.colorPrimary));
        } else {
            notificationBuilder.setSmallIcon(R.drawable.mabwe);
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(mChannel);
        }
        assert notificationManager != null;
        notificationManager.notify(1, notificationBuilder.build());
    }
}
