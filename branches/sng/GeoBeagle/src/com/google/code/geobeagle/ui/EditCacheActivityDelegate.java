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
import com.google.code.geobeagle.Util;
import com.google.code.geobeagle.data.Geocache;
import com.google.code.geobeagle.data.di.GeocacheFromTextFactory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class EditCacheActivityDelegate {
    public static class CancelButtonOnClickListener implements OnClickListener {
        private final Activity mActivity;

        public CancelButtonOnClickListener(Activity activity) {
            mActivity = activity;
        }

        public void onClick(View v) {
            // TODO: replace magic number.
            mActivity.setResult(-1, null);
            mActivity.finish();
        }
    }

    public static class GeocacheView {
        private final EditText mId;
        private final EditText mLatitude;
        private final EditText mLongitude;
        private final EditText mName;

        public GeocacheView(EditText id, EditText name, EditText latitude, EditText longitude) {
            mId = id;
            mName = name;
            mLatitude = latitude;
            mLongitude = longitude;
        }

        Geocache get() {
            return new Geocache(mId.getText(), mName.getText(), Util.parseCoordinate(mLatitude
                    .getText()), Util.parseCoordinate(mLongitude.getText()));
        }

        void set(Geocache geocache) {
            mId.setText(geocache.getId());
            mName.setText(geocache.getName());
            mLatitude.setText(Util.formatDegreesAsDecimalDegreesString(geocache.getLatitude()));
            mLongitude.setText(Util.formatDegreesAsDecimalDegreesString(geocache.getLongitude()));

            mLatitude.requestFocus();
        }
    }

    public static class SetButtonOnClickListener implements OnClickListener {
        private final Activity mActivity;
        private final GeocacheView mGeocacheView;

        public SetButtonOnClickListener(Activity activity, GeocacheView geocacheView) {
            mActivity = activity;
            mGeocacheView = geocacheView;
        }

        public void onClick(View v) {
            Intent i = new Intent();
            i.setAction(CacheListDelegate.SELECT_CACHE);
            i.putExtra("geocache", mGeocacheView.get());
            mActivity.setResult(0, i);
            mActivity.finish();
        }
    }

    private final CancelButtonOnClickListener mCancelButtonOnClickListener;
    private final GeocacheFromTextFactory mGeocacheFactory;
    private final Activity mParent;

    public EditCacheActivityDelegate(Activity parent,
            GeocacheFromTextFactory geocacheFromTextFactory,
            CancelButtonOnClickListener cancelButtonOnClickListener) {
        mParent = parent;
        mGeocacheFactory = geocacheFromTextFactory;
        mCancelButtonOnClickListener = cancelButtonOnClickListener;
    }

    public void onCreate(Bundle savedInstanceState) {
        mParent.setContentView(R.layout.cache_edit);
    }

    public void onResume() {
        Intent intent = mParent.getIntent();
        String stringExtra = intent.getStringExtra("cache");
        Geocache geocache = mGeocacheFactory.create(stringExtra);

        GeocacheView geocacheView = new GeocacheView((EditText)mParent.findViewById(R.id.edit_id),
                (EditText)mParent.findViewById(R.id.edit_name), (EditText)mParent
                        .findViewById(R.id.edit_latitude), (EditText)mParent
                        .findViewById(R.id.edit_longitude));
        geocacheView.set(geocache);

        SetButtonOnClickListener setButtonOnClickListener = new SetButtonOnClickListener(mParent,
                geocacheView);
        ((Button)mParent.findViewById(R.id.edit_set)).setOnClickListener(setButtonOnClickListener);

        Button cancel = (Button)mParent.findViewById(R.id.edit_cancel);
        cancel.setOnClickListener(mCancelButtonOnClickListener);
    }
}
