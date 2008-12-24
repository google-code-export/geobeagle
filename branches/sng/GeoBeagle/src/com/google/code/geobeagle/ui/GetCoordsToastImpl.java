
package com.google.code.geobeagle.ui;

import com.google.code.geobeagle.R;
import android.content.Context;
import android.widget.Toast;

public class GetCoordsToastImpl implements GetCoordsToast {

    private final Toast mToast;

    public GetCoordsToastImpl(Context context) {
        mToast = Toast.makeText(context, R.string.get_coords_toast, Toast.LENGTH_LONG);
    }

    public void show() {
        mToast.show();
    }

}
