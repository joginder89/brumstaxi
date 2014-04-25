package com.anaadih.brumstaxi;

import com.anaadih.brumstaxi.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    static final String TAG = "BrumsTaxi";

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString(),"Error");
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " +
                        extras.toString(),"Deleted messages on server");
            // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.
                for (int i=0; i<1; i++) {
                    Log.i(TAG, "Working... " + (i+1)
                            + "/5 @ " + SystemClock.elapsedRealtime());
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                    }
                }
                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                // Post notification of received message.
                //Need to Review.
                if(extras.getString("mydata") != null) {
                	sendNotification(extras.toString(),extras.getString("mydata"));
                	Log.i(TAG, "Received: " + extras.getString("mydata"));
            	} else {
            		driveratpickuppt(extras.getString("driveratpickuppoint"));
                	Log.i(TAG, "Notification Received: " + extras.getString("driveratpickuppoint"));
            	}
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    
    
    private void driveratpickuppt(String driveratpickuppoint) {
        mNotificationManager = (NotificationManager)
        this.getSystemService(Context.NOTIFICATION_SERVICE);
        Log.e("InSideFunction2",driveratpickuppoint);
        Intent myintent1 = new Intent(this, DriverAtPickupPoint.class);
        myintent1.putExtra("driveratpickuppoint", driveratpickuppoint);
        
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
        		myintent1, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.ic_launcher)
        .setContentTitle("Brums Taxi Notification")
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText("Driver Reached at Pickup Point"))
        .setContentText("Driver Reached at Pickup Point");

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
    
    private void sendNotification(String msg,String mydata) {
        mNotificationManager = (NotificationManager)
        this.getSystemService(Context.NOTIFICATION_SERVICE);
        Log.e("ReceivedData=>",msg);
        Intent myintent = new Intent(this, Quote.class);
        myintent.putExtra("message", msg);
        myintent.putExtra("mydata", mydata);
        
/*      PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, myintent), 0);*/
        
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
        		myintent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.ic_launcher)
        .setContentTitle("Brums Taxi Notification")
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText("Cab company has provided the details. Please check"))
        .setContentText("Cab company has provided the details. Please check");

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}