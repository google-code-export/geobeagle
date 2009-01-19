
package com.google.code.geobeagle;

import android.content.Context;

public class ResourceProvider {

    private final Context mContext;

    public ResourceProvider(Context context) {
        mContext = context;
    }

    public String getString(int resourceId) {
        return mContext.getString(resourceId);
    }

    public String[] getStringArray(int resourceId) {
        return mContext.getResources().getStringArray(resourceId);
    }

}
