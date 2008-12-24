
package com.google.code.geobeagle.intents;

import com.google.code.geobeagle.Destination;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.ResourceProvider;

import android.content.Intent;

public class IntentStarterCachePage implements IntentStarterGotoCache {
    private final ResourceProvider mResourceProvider;

    public IntentStarterCachePage(ResourceProvider resourceProvider) {
        mResourceProvider = resourceProvider;
    }

    public void startIntent(ActivityStarter activityStarter, IntentFactory intentFactory,
            Destination destination) {
        activityStarter.startActivity(intentFactory
                .createIntent(Intent.ACTION_VIEW, String.format(mResourceProvider
                        .getString(R.string.cache_page_url), destination.getDescription())));
    }
}
