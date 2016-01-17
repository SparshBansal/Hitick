package com.hitick.app.Broadcast_Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.hitick.app.R;

/**
 * Created by Sparsha on 10/18/2015.
 */
public class SmsReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = SmsReceiver.class.getSimpleName();
    private static Context context;

    public SmsReceiver(Context context){
        this.context = context;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        //Listen for incoming SMS and confirm validation
        if(intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)){
            Log.d("LOG_TAG" , "SMS_RECEIVED");
            Bundle bundle = intent.getExtras();
            SmsMessage[] messages = null;
            String msg_from;
            String messageBody = "";
            if(bundle!=null){
                //Retrieve the message
                try{
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    messages = new SmsMessage[pdus.length];
                    for(int i=0;i<messages.length;i++){
                        messages[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                        msg_from = messages[i].getOriginatingAddress();
                        messageBody += messages[i].getMessageBody().toString();
                        Log.d(LOG_TAG , messageBody);
                    }
                    if(messageBody!=null){
                        int VERIFICATION_CODE = Integer.parseInt(messageBody);
                        if(VERIFICATION_CODE == PreferenceManager.getDefaultSharedPreferences(context)
                                .getInt(context.getString(R.string.KEY_VERIFICATION_CODE), 0)){
                            Toast.makeText(context , "Verified!" ,Toast.LENGTH_SHORT ).show();
                        }
                    }
                }catch (Exception e){
                    Log.d(LOG_TAG , "Some exception occurred");
                }
            }
        }
    }
}
