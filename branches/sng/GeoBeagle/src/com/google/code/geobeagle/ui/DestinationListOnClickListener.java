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

import com.google.code.geobeagle.DescriptionsAndLocations;
import com.google.code.geobeagle.R;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DestinationListOnClickListener implements OnClickListener {
    public static class DestinationListDialogOnClickListener implements
            DialogInterface.OnClickListener {
        private final LocationSetter mLocationSetter;
        private final List<CharSequence> mPreviousLocations;
        private final ErrorDisplayer mErrorDisplayer;
        private final CachePageButtonEnabler mCachePageButtonEnabler;

        public DestinationListDialogOnClickListener(List<CharSequence> previousLocations,
                LocationSetter locationSetter, ErrorDisplayer errorDisplayer,
                CachePageButtonEnabler cachePageButtonEnabler) {
            this.mPreviousLocations = previousLocations;
            this.mLocationSetter = locationSetter;
            this.mErrorDisplayer = errorDisplayer;
            this.mCachePageButtonEnabler = cachePageButtonEnabler;
        }

        public void onClick(DialogInterface dialog, int which) {
            mLocationSetter.setLocation(mPreviousLocations.get(which), mErrorDisplayer);
            mCachePageButtonEnabler.check();
        }
    }

    public static final String MY_LOCATION = "My Current Location";
    final private DescriptionsAndLocations mDescriptionsAndLocations;
    final private Builder mDialogBuilder;
    final private LocationSetter mLocationSetter;
    final private ErrorDisplayer mErrorDisplayer;
    final private CachePageButtonEnabler mCachePageButtonEnabler;
    public DestinationListOnClickListener(DescriptionsAndLocations descriptionsAndLocations,
            LocationSetter locationSetter, AlertDialog.Builder dialogBuilder,
            ErrorDisplayer errorDisplayer, CachePageButtonEnabler cachePageButtonEnabler) {
        this.mDialogBuilder = dialogBuilder;
        this.mLocationSetter = locationSetter;
        this.mDescriptionsAndLocations = descriptionsAndLocations;
        this.mErrorDisplayer = errorDisplayer;
        this.mCachePageButtonEnabler = cachePageButtonEnabler;
    }

    protected DialogInterface.OnClickListener createDestinationListDialogOnClickListener(
            List<CharSequence> previousLocations, CachePageButtonEnabler cachePageButtonEnabler) {
        return new DestinationListDialogOnClickListener(previousLocations, mLocationSetter,
                mErrorDisplayer, cachePageButtonEnabler);
    }

    private List<CharSequence> getDisplayableList(List<CharSequence> list, String myLocation) {
        List<CharSequence> dialogPreviousDescriptions = new ArrayList<CharSequence>();
        dialogPreviousDescriptions.addAll(list);
        dialogPreviousDescriptions.add(myLocation);
        Collections.reverse(dialogPreviousDescriptions);
        return dialogPreviousDescriptions;
    }

    public void onClick(View v) {
        showDestinationListDialog();
    }

    private void showDestinationListDialog() {
        // Display "My Location" followed by descriptions in reverse order (most
        // recent descriptions first).
        List<CharSequence> dialogPreviousDescriptions = getDisplayableList(
                mDescriptionsAndLocations.getPreviousDescriptions(),
                DestinationListOnClickListener.MY_LOCATION);
        List<CharSequence> dialogPreviousLocations = getDisplayableList(mDescriptionsAndLocations
                .getPreviousLocations(), null);
        final CharSequence[] prevDescriptionsArray = dialogPreviousDescriptions
                .toArray(new CharSequence[dialogPreviousDescriptions.size()]);
        mDialogBuilder.setTitle(R.string.select_destination);
        mDialogBuilder.setItems(prevDescriptionsArray,
                createDestinationListDialogOnClickListener(dialogPreviousLocations, mCachePageButtonEnabler)).create()
                .show();
    }
}
