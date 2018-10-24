package edu.charusat.scheduleit.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

import edu.charusat.scheduleit.R;

/**
 * Created by HP on 11-10-2018.
 */

public class SmsReceiver extends BroadcastReceiver {
    private String tlp_receiver;
    private String sms_text;
    private String name;
    Context context;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        //Log.v("Pending intent: ","Received");
        sms_text = intent.getStringExtra("message");
        tlp_receiver = intent.getStringExtra("phonenumber");
        name = intent.getStringExtra("contactname");
        send_sms();
        build_notification();
    }

    public void send_sms(){
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(tlp_receiver,null,sms_text,null,null);
    }

    public void build_notification(){
        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher_timecard)
                .setContentTitle("Pending intent")
                .setContentText("SMS sent successfully to "+name)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("SMS sent successfully to "+name))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, mBuilder.build());
    }
}
