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

package com.google.code.geobeagle.gpsstatuswidget;

import com.google.code.geobeagle.R;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

public class InflatedGpsStatusWidget extends LinearLayout {
    private GpsStatusWidgetDelegate mGpsStatusWidgetDelegate;

    public InflatedGpsStatusWidget(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.gps_widget, this, true);
    }

    public InflatedGpsStatusWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.gps_widget, this, true);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        mGpsStatusWidgetDelegate.paint();
    }

    public void setDelegate(GpsStatusWidgetDelegate gpsStatusWidgetDelegate) {
        mGpsStatusWidgetDelegate = gpsStatusWidgetDelegate;
    }

    public GpsStatusWidgetDelegate getDelegate() {
        return mGpsStatusWidgetDelegate;
    }
}
