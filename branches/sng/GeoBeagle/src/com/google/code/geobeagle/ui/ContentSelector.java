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

import com.google.code.geobeagle.LifecycleManager;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.Spinner;

public class ContentSelector implements LifecycleManager {

    static final String CONTENT_PROVIDER = "ContentProvider";
    private final SharedPreferences mPreferences;
    private final Spinner mSpinner;

    public ContentSelector(Spinner spinner, SharedPreferences sharedPreferences) {
        mSpinner = spinner;
        mPreferences = sharedPreferences;
    }

    public int getIndex() {
        return mSpinner.getSelectedItemPosition();
    }

    public void onPause(Editor editor) {
        editor.putInt(CONTENT_PROVIDER, mSpinner.getSelectedItemPosition());
    }

    public void onResume(SharedPreferences preferences) {
        mSpinner.setSelection(mPreferences.getInt(CONTENT_PROVIDER, 1));
    }
}
