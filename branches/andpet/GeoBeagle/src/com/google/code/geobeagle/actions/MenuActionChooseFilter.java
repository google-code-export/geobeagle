package com.google.code.geobeagle.actions;

import com.google.code.geobeagle.CacheTypeFilter;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh;
import com.google.code.geobeagle.database.CachesProviderToggler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.util.Log;
import android.widget.Toast;

public class MenuActionChooseFilter extends MenuActionBase {
    private final Activity mActivity;
    private final CacheTypeFilter mFilter;
    private final CachesProviderToggler mCachesProviderToggler;
    private final CacheListRefresh mCacheListRefresh;
    
    public MenuActionChooseFilter(Activity activity,
            CacheTypeFilter filter, CachesProviderToggler cachesProviderToggler,
            CacheListRefresh cacheListRefresh) {
        super(R.string.menu_choose_filter);
        mActivity = activity;
        mFilter = filter;
        mCachesProviderToggler  = cachesProviderToggler;
        mCacheListRefresh = cacheListRefresh;
    }

    @Override
    public void act() {
        mFilter.loadFromPrefs(mActivity.getSharedPreferences("Filter", 0));

        OnMultiChoiceClickListener clickListener = new OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int ix, boolean enable) {
                mFilter.setEnabled(ix, enable);
            }
        };
        
        OnDismissListener dismissListener = new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface arg0) {
                mFilter.saveToPrefs(mActivity.getSharedPreferences("Filter", 0));
                Toast.makeText(mActivity.getApplicationContext(), "Saved filter", 
                        Toast.LENGTH_SHORT).show();
                mCachesProviderToggler.setExtraCondition(mFilter.getSqlWhereClause());
                mCacheListRefresh.forceRefresh();
            }
        };
        
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("Cache types to show");
        final CharSequence[] items = mFilter.getOptionLabels();
        final boolean[] selected = mFilter.getSelection();
        builder.setMultiChoiceItems(items, selected, clickListener);
        AlertDialog alert = builder.create();
        alert.setOnDismissListener(dismissListener);

        alert.show();

        /*
        Context context = mActivity;
        Dialog dialog = new Dialog(context);
        //setOwnerActivity(Activity)
        dialog.setContentView(R.layout.filter);
        dialog.setTitle("Custom Dialog");
        dialog.show();
        */
    }

}
