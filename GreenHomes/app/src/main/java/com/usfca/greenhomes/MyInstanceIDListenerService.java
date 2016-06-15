package com.usfca.greenhomes;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.iid.InstanceIDListenerService;
import java.io.IOException;

public class MyInstanceIDListenerService extends InstanceIDListenerService {
    final String PROJECT_ID = "918094878188";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Log.i("Token: ", token);
        }catch(IOException e){
            Log.i("Exception: ", e.toString());
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
