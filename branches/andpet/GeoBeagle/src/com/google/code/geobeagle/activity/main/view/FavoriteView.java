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

    public static class FavoriteState {
        private final DbFrontend mDbFrontend;
        private final CharSequence mGeocacheId;
        private boolean mIsFavorite;

        public FavoriteState(DbFrontend dbFrontend, CharSequence geocacheId) {
            mDbFrontend = dbFrontend;
            mGeocacheId = geocacheId;
            mIsFavorite = dbFrontend.geocacheHasTag(geocacheId, Tags.FAVORITES);
        }

        public boolean isFavorite() {
            return mIsFavorite;
        }

        public void toggleFavorite() {
            mIsFavorite = !mIsFavorite;
            mDbFrontend
                    .setGeocacheTag(mGeocacheId, Tags.FAVORITES, mIsFavorite);
        }
    }

    public static class FavoriteViewDelegate {
        private final FavoriteState mFavoriteState;
        private final FavoriteView mFavoriteView;

        public FavoriteViewDelegate(FavoriteView favoriteView,
                FavoriteState favoriteState) {
            mFavoriteView = favoriteView;
            mFavoriteState = favoriteState;
        }

        void toggleFavorite() {
            mFavoriteState.toggleFavorite();
            updateImage();
        }

        void updateImage() {
            mFavoriteView
                    .setImageResource(mFavoriteState.isFavorite() ? R.drawable.btn_rating_star_on_normal
                            : R.drawable.btn_rating_star_off_normal);
        }
    }

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

    private FavoriteViewDelegate mFavoriteViewDelegate;

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

    public void setGeocache(FavoriteViewDelegate favoriteViewDelegate) {
        mFavoriteViewDelegate = favoriteViewDelegate;
        mFavoriteViewDelegate.updateImage();
    }

    void toggleFavorite() {
        mFavoriteViewDelegate.toggleFavorite();
    }

}
