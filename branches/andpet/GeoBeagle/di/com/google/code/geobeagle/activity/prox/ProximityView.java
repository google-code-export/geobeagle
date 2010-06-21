package com.google.code.geobeagle.activity.prox;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;

public class ProximityView extends SurfaceView {

    public ProximityView(Context context) {
        super(context);
        setFocusable(true);
    }

    public ProximityView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
    }

    public ProximityView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFocusable(true);
    }

}
