
package com.google.code.geobeagle.ui.di;

import com.google.code.geobeagle.ResourceProvider;
import com.google.code.geobeagle.data.di.GeocacheFromTextFactory;
import com.google.code.geobeagle.ui.EditCacheActivityDelegate;

import android.app.Activity;
import android.os.Bundle;

public class EditCacheActivity extends Activity {
    private EditCacheActivityDelegate mEditCacheActivityDelegate;

    public EditCacheActivity() {
        super();

        final ResourceProvider resourceProvider = new ResourceProvider(this);
        final GeocacheFromTextFactory geocacheFromTextFactory = new GeocacheFromTextFactory(
                resourceProvider);
        final EditCacheActivityDelegate.CancelButtonOnClickListener cancelButtonOnClickListener = new EditCacheActivityDelegate.CancelButtonOnClickListener(
                this);
        mEditCacheActivityDelegate = new EditCacheActivityDelegate(this, geocacheFromTextFactory,
                cancelButtonOnClickListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEditCacheActivityDelegate.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mEditCacheActivityDelegate.onResume();
    }
}
