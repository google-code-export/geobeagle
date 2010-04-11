/*
 ** Licensed under the Apache License, Version 2.0 (the "License");
 ** you may not use this file except in compliance with the License.
 ** You may obtain a copy of the License at
 **
 **     http://www.apache.org/licenses/LICENSE-2.0
 **
 ** Unless required by applicable law or agreed to in writing, software
 ** distributed under the License is distributed on an "AS IS" BASIS,
 ** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ** See the License for the specific language governing permissions and
 ** limitations under the License.
 */

package com.google.code.geobeagle.activity.main.view;

import com.google.code.geobeagle.ErrorDisplayer;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.main.intents.IntentStarter;
import com.google.code.geobeagle.activity.main.intents.IntentStarterGeo;
import com.google.code.geobeagle.activity.main.intents.IntentStarterViewUri;
import com.google.code.geobeagle.activity.main.intents.IntentStarterGeo.IntentStarterMap;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import android.content.ActivityNotFoundException;
import android.view.View;
import android.view.View.OnClickListener;

public class CacheButtonOnClickListener implements OnClickListener {
    private final IntentStarter mDestinationToIntentFactory;
    private final ErrorDisplayer mErrorDisplayer;
    private final String mActivityNotFoundErrorMessage;

    public static class MapsButtonOnClickListener extends CacheButtonOnClickListener {
        @Inject
        public MapsButtonOnClickListener(@IntentStarterMap IntentStarterGeo intentStarter,
                @Named("OpenMapError") String errorMessage, ErrorDisplayer errorDisplayer) {
            super(intentStarter, errorMessage, errorDisplayer);
        }
    }

    public static class CachePageButtonOnClickListener extends CacheButtonOnClickListener {
        @Inject
        public CachePageButtonOnClickListener(
                @Named("IntentStarterViewCachePage") IntentStarterViewUri intentStarter,
                @Named("OpenUriError") String errorMessage, ErrorDisplayer errorDisplayer) {
            super(intentStarter, errorMessage, errorDisplayer);
        }
    }
    
    public CacheButtonOnClickListener(IntentStarter intentStarter, String errorMessage,
            ErrorDisplayer errorDisplayer) {
        mDestinationToIntentFactory = intentStarter;
        mErrorDisplayer = errorDisplayer;
        mActivityNotFoundErrorMessage = errorMessage;
    }

    public void onClick(View view) {
        try {
            mDestinationToIntentFactory.startIntent();
        } catch (final ActivityNotFoundException e) {
            mErrorDisplayer.displayError(R.string.error2, e.getMessage(),
                    mActivityNotFoundErrorMessage);
        }
    }
}
