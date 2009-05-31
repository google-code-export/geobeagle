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

package com.google.code.geobeagle.mainactivity.ui;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.ResourceProvider;

import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class OnContentProviderSelectedListener implements OnItemSelectedListener {
    private final TextView mContentProviderCaption;
    private final ResourceProvider mResourceProvider;

    public OnContentProviderSelectedListener(ResourceProvider resourceProvider,
            TextView contentProviderCaption) {
        mResourceProvider = resourceProvider;
        mContentProviderCaption = contentProviderCaption;
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        final String[] objectNames = mResourceProvider.getStringArray(R.array.object_names);
        mContentProviderCaption.setText(mResourceProvider.getString(R.string.search_for) + " "
                + objectNames[position] + ":");
    }

    public void onNothingSelected(AdapterView<?> parent) {
    }
}
