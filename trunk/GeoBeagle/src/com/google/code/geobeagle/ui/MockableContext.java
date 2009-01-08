
package com.google.code.geobeagle.ui;

import android.content.Context;

public class MockableContext {

    private Context context;

    public MockableContext(Context context) {
        this.context = context;
    }

    public String getString(int id) {
        return context.getString(id);
    }

}
