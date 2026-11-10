package com.uenkerlabs.phone;

import android.telecom.Call;
import android.telecom.CallScreeningService;
import java.util.Collections;
import java.util.Set;

public final class LocalScreeningService extends CallScreeningService {
    @Override public void onScreenCall(Call.Details details) {
        String raw=details.getHandle()==null?"":details.getHandle().getSchemeSpecificPart();
        String number=raw==null?"":raw.replaceAll("[^+0-9]","");
        Set<String> blocked=getSharedPreferences("call_rules",MODE_PRIVATE)
            .getStringSet("blocked",Collections.emptySet());
        boolean reject=!number.isEmpty() && blocked.contains(number)
            && details.getCallDirection()==Call.Details.DIRECTION_INCOMING;
        CallResponse response=new CallResponse.Builder()
            .setDisallowCall(reject)
            .setRejectCall(reject)
            .setSkipCallLog(false)
            .setSkipNotification(false)
            .build();
        respondToCall(details,response);
    }
}

