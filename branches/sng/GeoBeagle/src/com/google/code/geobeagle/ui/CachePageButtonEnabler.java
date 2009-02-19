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

package com.google.code.geobeagle.ui;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.ResourceProvider;
import com.google.code.geobeagle.data.Destination;

import android.view.View;

public class CachePageButtonEnabler {
    private final View mCachePageButton;
    private final String[] mContentPrefixes;
    private final TooString mTooString;

    public CachePageButtonEnabler(TooString editText, View cachePageButton,
            ResourceProvider resourceProvider) {
        mTooString = editText;
        mCachePageButton = cachePageButton;
        mContentPrefixes = resourceProvider.getStringArray(R.array.content_prefixes);
    }

    public void check() {
        final String description = (String)Destination.extractDescription(mTooString.tooString());

        for (String contentPrefix : mContentPrefixes) {
            if (description.startsWith(contentPrefix)) {
                mCachePageButton.setEnabled(true);
                return;
            }
        }
        mCachePageButton.setEnabled(false);
    }
}
