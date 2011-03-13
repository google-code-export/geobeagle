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

package com.google.code.geobeagle.activity.compass;

import com.google.code.geobeagle.GraphicsGenerator.RatingsArray;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.compass.fieldnotes.ActivityWithGeocache;
import com.google.code.geobeagle.activity.compass.fieldnotes.FragmentWithGeocache;
import com.google.code.geobeagle.activity.compass.fieldnotes.HasGeocache;
import com.google.code.geobeagle.activity.compass.view.GeocacheViewer.AttributeViewer;
import com.google.code.geobeagle.activity.compass.view.GeocacheViewer.LabelledAttributeViewer;
import com.google.code.geobeagle.activity.compass.view.GeocacheViewer.UnlabelledAttributeViewer;
import com.google.inject.Provides;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import roboguice.config.AbstractAndroidModule;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.widget.ImageView;
import android.widget.TextView;

public class CompassActivityModule extends AbstractAndroidModule {
    @Override
    protected void configure() {
        bind(ChooseNavDialog.class).toProvider(ChooseNavDialogProvider.class);
        int sdkVersion = Integer.parseInt(Build.VERSION.SDK);
        if (sdkVersion >= Build.VERSION_CODES.HONEYCOMB) {
            bind(CompassFragtivityOnCreateHandler.class).to(CompassFragmentOnCreateHandler.class);
            bind(HasGeocache.class).to(FragmentWithGeocache.class);
        } else {
            bind(CompassFragtivityOnCreateHandler.class).to(CompassActivityOnCreateHandler.class);
            bind(HasGeocache.class).to(ActivityWithGeocache.class);
        }
    }

    private static UnlabelledAttributeViewer getImagesOnDifficulty(final Drawable[] pawDrawables,
            ImageView imageView, RatingsArray ratingsArray) {
        return new UnlabelledAttributeViewer(imageView, ratingsArray.getRatings(pawDrawables, 10));
    }

    @Provides
    RadarView providesRadarView(Activity activity) {
        RadarView radarView = (RadarView)activity.findViewById(R.id.radarview);
        radarView.setUseImperial(false);
        radarView.setDistanceView((TextView)activity.findViewById(R.id.radar_distance),
                (TextView)activity.findViewById(R.id.radar_bearing), (TextView)activity
                        .findViewById(R.id.radar_accuracy));
        return radarView;
    }

    @Provides
    XmlPullParser providesXmlPullParser() throws XmlPullParserException {
        return XmlPullParserFactory.newInstance().newPullParser();
    }

    static AttributeViewer getLabelledAttributeViewer(HasViewById activity, Resources resources,
            RatingsArray ratingsArray, int[] resourceIds, int difficultyId, int labelId) {
        final ImageView imageViewTerrain = (ImageView)activity.findViewById(difficultyId);

        final Drawable[] pawDrawables = {
                resources.getDrawable(resourceIds[0]), resources.getDrawable(resourceIds[1]),
                resources.getDrawable(resourceIds[2]),
        };
        final AttributeViewer pawImages = getImagesOnDifficulty(pawDrawables, imageViewTerrain,
                ratingsArray);
        final AttributeViewer gcTerrain = new LabelledAttributeViewer((TextView)activity
                .findViewById(labelId), pawImages);
        return gcTerrain;
    }
}
