package com.google.code.geobeagle.actions;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.Tags;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListAdapter;
import com.google.code.geobeagle.database.DbFrontend;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import java.util.Map;

public class CacheActionAssignTags implements CacheAction {

    private final Activity mActivity;
    private final DbFrontend mDbFrontend;
    private final CacheListAdapter mCacheListAdapter;

    public CacheActionAssignTags(Activity activity, DbFrontend dbFrontend, 
            CacheListAdapter cacheListAdapter) {
        mActivity = activity;
        mDbFrontend = dbFrontend;
        mCacheListAdapter = cacheListAdapter;
    }

    private boolean hasChanged = false;
    
    @Override
    public void act(final Geocache cache) {
        final Dialog dialog = new Dialog(mActivity);

        View rootView = mActivity.getLayoutInflater().inflate(R.layout.assign_tags, 
                    null);
        LinearLayout linearLayout =
            (LinearLayout)rootView.findViewById(R.id.AssignTagsLinearLayout);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        
        final OnClickListener mOnSelect = new OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkbox = (CheckBox)v;
                int tagId = v.getId();
                boolean checked = checkbox.isChecked();
                Log.d("GeoBeagle", "Setting tag " + tagId + 
                        " to " + (checked?"true":"false"));
                mDbFrontend.setGeocacheTag(cache.getId(), tagId, checked);
                hasChanged = true;
                cache.flushIcons();
            }
        };
        
        final OnDismissListener onDismiss = new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (hasChanged)
                    mCacheListAdapter.notifyDataSetChanged();
            }
        };
        dialog.setOnDismissListener(onDismiss);
        
        Map<Integer, String> allTags = Tags.GetAllTags();
        for (Integer i : allTags.keySet()) {
            String tagName = allTags.get(i);
            boolean hasTag = mDbFrontend.geocacheHasTag(cache.getId(), i);
            CheckBox checkbox = new CheckBox(mActivity);
            checkbox.setChecked(hasTag);
            checkbox.setOnClickListener(mOnSelect);
            checkbox.setText(tagName);
            checkbox.setId(i);
            linearLayout.addView(checkbox, layoutParams);
        }
        
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setTitle("Assign tags to " + cache.getId());
        
        dialog.setContentView(linearLayout);
        
        dialog.show();
    }

    @Override
    public String getLabel(Geocache geocache) {
        return mActivity.getResources().getString(R.string.assign_tags);
    }

}
