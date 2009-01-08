
package com.google.code.geobeagle.ui;

import com.google.code.geobeagle.R;
import android.content.Context;
import android.widget.Toast;

public class GetCoordsToast {

    private final Toast mToast;

    public GetCoordsToast(Context context) {
        mToast = Toast.makeText(context, R.string.get_coords_toast, Toast.LENGTH_LONG);
    }

    public void show() {
        mToast.show();
    }

}
