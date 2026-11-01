package itbs.chaima.findfriends;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MySmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        String messageBody,phoneNumber;
        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED"))
        {
            Bundle bundle =intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                final SmsMessage[] messages = new SmsMessage[pdus.length];
                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }
                if (messages.length > -1) {
                    messageBody = messages[0].getMessageBody();
                    phoneNumber = messages[0].getDisplayOriginatingAddress();

                    Toast.makeText(context,
                            "Message : "+messageBody+"Reçu de la part de;"+ phoneNumber,
                            Toast.LENGTH_LONG )
                            .show();
                    if(messageBody.contains("FindFriends")){
                        //afficher une notification

                        NotificationCompat.Builder mynotif= new NotificationCompat.Builder(context,"canal_findfriends");
                        mynotif.setContentTitle("FindFriends App");
                        mynotif.setContentText("new position:"+messageBody);
                        mynotif.setSmallIcon(android.R.drawable.ic_dialog_map);
                        mynotif.setAutoCancel(true);

                        //action sur la notification : lancer myavtivity
                        String [] t=messageBody.split("#");
                        String lon=t[1];
                        String lat=t[2];


                        Intent i=new Intent(context, MapsActivity.class);
                        i.putExtra("longitude",lon);
                        i.putExtra("latitude",lat);
                        i.putExtra("numero",phoneNumber);

                        PendingIntent pi= PendingIntent.getActivity(context,0,i,PendingIntent.FLAG_MUTABLE);
                        mynotif.setContentIntent(pi);    // action sur la notification

                        mynotif.setVibrate(new long[]{0,1000,200,2000}); //permission dans le manifest

                        Uri son= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                        mynotif.setSound(son);

                        NotificationManagerCompat manager = NotificationManagerCompat.from(context);  //recuperer une instance du manager
                        //tester si la version android est >= 0 (oreo) ,alors crée un canal
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        {
                            //creation canal
                            NotificationChannel canal = new NotificationChannel("canal_findfriends","find friends channel", NotificationManager.IMPORTANCE_HIGH);
                            AudioAttributes attr =new AudioAttributes.Builder()
                                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                    .setUsage(AudioAttributes.USAGE_ALARM)
                                    .build();
                            canal.setSound(son,attr);
                            manager.createNotificationChannel(canal); //creation du canal dans le smart phone

                        }
                        manager.notify(1, mynotif.build());// elle vas lancer la notification
                    }

                }
            }
        }
    }

}