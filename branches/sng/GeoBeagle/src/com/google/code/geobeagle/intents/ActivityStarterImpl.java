
package com.google.code.geobeagle.intents;

import android.content.Context;
import android.content.Intent;

public class ActivityStarterImpl implements ActivityStarter {
    private final Context mContext;

    public ActivityStarterImpl(Context context) {
        mContext = context;
    }

    public void startActivity(Intent intent) {
        mContext.startActivity(intent);
    }

}
