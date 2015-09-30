package uha.ensisa.android.wishalert;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context k1, Intent k2) {

        String name = k2.getStringExtra("name").toString();
        String type = k2.getStringExtra("type").toString();

        //Show toast
        Toast.makeText(k1, name + "'s " + type, Toast.LENGTH_LONG).show();
        //Ring a tone
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        Ringtone r = RingtoneManager.getRingtone(k1.getApplicationContext(), notification);
        r.play();
    }

}