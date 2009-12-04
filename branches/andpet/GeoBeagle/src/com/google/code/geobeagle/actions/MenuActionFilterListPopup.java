package com.google.code.geobeagle.actions;

import com.google.code.geobeagle.CacheFilter;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.Refresher;
import com.google.code.geobeagle.activity.filterlist.FilterTypeCollection;
import android.app.Activity;
import android.app.Dialog;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/** Show a popup dialog to let the user choose what filter to use */
public class MenuActionFilterListPopup implements MenuAction {
    private final Activity mActivity;
    private final FilterTypeCollection mFilterTypeCollection;
    private final CacheFilterUpdater mCacheFilterUpdater;
    private final Refresher mRefresher;
    
    public MenuActionFilterListPopup(Activity activity,
            CacheFilterUpdater cacheFilterUpdater,
            Refresher refresher, FilterTypeCollection filterTypeCollection) {
        mActivity = activity;
        mCacheFilterUpdater = cacheFilterUpdater;
        mRefresher = refresher;
        mFilterTypeCollection = filterTypeCollection;
    }

    @Override
    public String getLabel() {
        return mActivity.getResources().getString(R.string.menu_choose_filter);
    }
    
    
    @Override
    public void act() {
        final Dialog dialog = new Dialog(mActivity);

        //TODO: Inflate the view and findView R.id.filterlist_radiogroup instead?
        final RadioGroup radioGroup = new RadioGroup(mActivity);
        LinearLayout.LayoutParams layoutParams = new RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.WRAP_CONTENT,
                RadioGroup.LayoutParams.WRAP_CONTENT);
        
        final OnClickListener mOnSelect = new OnClickListener() {
            @Override
            public void onClick(View v) {
                int ix = radioGroup.getCheckedRadioButtonId();
                CacheFilter cacheFilter = mFilterTypeCollection.get(ix);
                Log.d("GeoBeagle", "Setting active filter to " + cacheFilter.getName() + 
                        " with id " + cacheFilter.mId);
                mFilterTypeCollection.setActiveFilter(cacheFilter);
                dialog.dismiss();
                mCacheFilterUpdater.loadActiveFilter();
                mRefresher.forceRefresh();
            }
        };
        radioGroup.setOnClickListener(mOnSelect);
        
        for (int i = 0; i < mFilterTypeCollection.getCount(); i++) {
            CacheFilter cacheFilter = mFilterTypeCollection.get(i);
            RadioButton newRadioButton = new RadioButton(mActivity);
            newRadioButton.setOnClickListener(mOnSelect);
            newRadioButton.setText(cacheFilter.getName());
            newRadioButton.setId(i);
            radioGroup.addView(newRadioButton, layoutParams);
        }
        int selected = mFilterTypeCollection.getIndexOf(mFilterTypeCollection.
                getActiveFilter());
        radioGroup.check(selected);
        
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setTitle("Choose filter!");
        
        //dialog.setContentView(R.layout.filterlist);
        dialog.setContentView(radioGroup);
        
        //TextView title = (TextView)dialog.findViewById(R.id.TextFilterTitle);
        //title.setText(getLabel());
        
        dialog.show();
    }

}
