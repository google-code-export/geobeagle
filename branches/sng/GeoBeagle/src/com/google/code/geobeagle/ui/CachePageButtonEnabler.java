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
import com.google.code.geobeagle.data.di.GeocacheFactory;

import android.text.TextUtils;
import android.view.View;

public class CachePageButtonEnabler {
    public static class MockableTextUtils {
        int indexOf(CharSequence s, char ch) {
            return TextUtils.indexOf(s, ch);
        }
    }

    public static CachePageButtonEnabler create(TooString editText, View cachePageButton,
            View detailsButton, ResourceProvider resourceProvider) {
        MockableTextUtils textUtils = new MockableTextUtils();
        return new CachePageButtonEnabler(editText, cachePageButton, detailsButton,
                resourceProvider, textUtils);
    }

    private final View mCachePageButton;
    private final String[] mContentPrefixes;
    private final View mDetailsButton;

    private final MockableTextUtils mTextUtils;

    private final TooString mTooString;

    public CachePageButtonEnabler(TooString editText, View cachePageButton, View detailsButton,
            ResourceProvider resourceProvider, MockableTextUtils textUtils) {
        mTooString = editText;
        mCachePageButton = cachePageButton;
        mDetailsButton = detailsButton;
        mContentPrefixes = resourceProvider.getStringArray(R.array.content_prefixes);
        mTextUtils = textUtils;
    }

    public void check() {
        final CharSequence s = mTooString.tooString();
        final String description = (String)GeocacheFactory.extractDescription(s);
        mDetailsButton.setEnabled(-1 != mTextUtils.indexOf(s, ':'));
        for (String contentPrefix : mContentPrefixes) {
            if (description.startsWith(contentPrefix)) {
                mCachePageButton.setEnabled(true);
                return;
            }
        }
        mCachePageButton.setEnabled(false);
    }
}
