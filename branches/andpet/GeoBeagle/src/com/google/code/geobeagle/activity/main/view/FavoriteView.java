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

import com.google.code.geobeagle.Tags;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.database.DbFrontend;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

/** Handles the star graphics showing if the geocache is a favorite */
public class FavoriteView extends ImageView {

    static class OnFavoriteClick implements OnClickListener {
        private final FavoriteView mFavoriteView;
        
        OnFavoriteClick(FavoriteView favoriteView) {
            mFavoriteView = favoriteView;
        }
        
        @Override
        public void onClick(View v) {
            mFavoriteView.toggleFavorite();
        }
    }

    private DbFrontend mDbFrontend;
    private CharSequence mGeocacheId;
    private boolean mIsFavorite;
    
    public FavoriteView(Context context) {
        super(context);
        setOnClickListener(new OnFavoriteClick(this));
    }
    public FavoriteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnClickListener(new OnFavoriteClick(this));
    }
    public FavoriteView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOnClickListener(new OnFavoriteClick(this));
    }

    public void setGeocache(DbFrontend dbFrontend, CharSequence geocacheId) {
        mDbFrontend = dbFrontend;
        mGeocacheId = geocacheId;
        mIsFavorite = mDbFrontend.geocacheHasTag(mGeocacheId, Tags.FAVORITES);
        updateImage();
    }
    
    private void updateImage() {
        setImageResource(mIsFavorite ? R.drawable.btn_rating_star_on_normal :
            R.drawable.btn_rating_star_off_normal);
    }

    void toggleFavorite() {
        mIsFavorite = !mIsFavorite;
        mDbFrontend.setGeocacheTag(mGeocacheId, Tags.FAVORITES, mIsFavorite);
        updateImage();
    }
    
}
