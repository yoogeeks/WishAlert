package uha.ensisa.android.wishalert;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.widget.Toast;

public class MessageReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context k1, Intent k2) {

        String strReceipent = k2.getStringExtra("phone").toString(); //Phone number
        String strSMSBody = k2.getStringExtra("msg").toString(); //Message to sent

        //Try to send SMS
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(strReceipent, null, strSMSBody, null, null);
            Toast.makeText(k1.getApplicationContext(), "Message Sent", Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(k1.getApplicationContext(), ex.getMessage().toString(), Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }
}