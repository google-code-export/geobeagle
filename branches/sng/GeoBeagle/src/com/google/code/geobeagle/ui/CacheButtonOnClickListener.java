
package com.google.code.geobeagle.ui;

import com.google.code.geobeagle.intents.IntentStarter;

import android.content.ActivityNotFoundException;
import android.view.View;
import android.view.View.OnClickListener;

public class CacheButtonOnClickListener implements OnClickListener {
    private final ErrorDisplayer mErrorDisplayer;
    private final String mErrorMessage;
    private final IntentStarter mDestinationToIntentFactory;

    public CacheButtonOnClickListener(IntentStarter intentStarter, ErrorDisplayer errorDisplayer,
            String errorMessage) {
        mDestinationToIntentFactory = intentStarter;
        mErrorDisplayer = errorDisplayer;
        mErrorMessage = errorMessage;
    }

    public void onClick(View view) {
        try {
            mDestinationToIntentFactory.startIntent();
        } catch (final ActivityNotFoundException e) {
            mErrorDisplayer.displayError("Error: " + e.getMessage() + mErrorMessage);
        } catch (final Exception e) {
            mErrorDisplayer.displayError("Error: " + e.getMessage());
        }
    }
}
