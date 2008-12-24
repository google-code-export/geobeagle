
package com.google.code.geobeagle.intents;

import android.content.Context;
import android.content.Intent;

public class ActivityStarter {
    private final Context mContext;

    public ActivityStarter(Context context) {
        mContext = context;
    }

    public void startActivity(Intent intent) {
        mContext.startActivity(intent);
    }

}
