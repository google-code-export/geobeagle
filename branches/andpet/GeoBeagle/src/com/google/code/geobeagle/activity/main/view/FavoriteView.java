package com.google.code.geobeagle.activity.main.view;

import com.google.code.geobeagle.Labels;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.database.DbFrontend;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

/** Handles the star graphics showing if the geocache is a favorite */
public class FavoriteView extends ImageView {

    private class OnFavoriteClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            setFavorite(!mIsFavorite);
        }
    }

    private DbFrontend mDbFrontend;
    private CharSequence mGeocacheId;
    private boolean mIsFavorite;
    
    public FavoriteView(Context context) {
        super(context);
        setOnClickListener(new OnFavoriteClick());
    }
    public FavoriteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnClickListener(new OnFavoriteClick());
    }
    public FavoriteView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOnClickListener(new OnFavoriteClick());
    }

    public void setGeocache(DbFrontend dbFrontend, CharSequence geocacheId) {
        mDbFrontend = dbFrontend;
        mGeocacheId = geocacheId;
        mIsFavorite = mDbFrontend.geocacheHasLabel(mGeocacheId, Labels.FAVORITES);
        updateImage();
    }
    
    private void updateImage() {
        setImageResource(mIsFavorite ? R.drawable.btn_rating_star_on_normal :
            R.drawable.btn_rating_star_off_normal);
    }

    private void setFavorite(boolean favorite) {
        mIsFavorite = favorite;
        if (favorite)
            mDbFrontend.setGeocacheLabel(mGeocacheId, Labels.FAVORITES);
        else
            mDbFrontend.unsetGeocacheLabel(mGeocacheId, Labels.FAVORITES);
        updateImage();
    }
    
}
