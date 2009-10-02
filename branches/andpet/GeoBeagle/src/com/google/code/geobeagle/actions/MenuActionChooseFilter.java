package com.google.code.geobeagle.actions;

import com.google.code.geobeagle.CacheTypeFilter;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.Refresher;
import com.google.code.geobeagle.database.CachesProvider;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;

/** Show a dialog to let the user decide which geocaches to show */
public class MenuActionChooseFilter extends MenuActionBase {
    private final Activity mActivity;
    private final CacheTypeFilter mFilter;
    private final CachesProvider mCachesProvider;
    private final Refresher mRefresher;
    
    public MenuActionChooseFilter(Activity activity,
            CacheTypeFilter filter, CachesProvider cachesProvider,
            Refresher refresher) {
        super(R.string.menu_choose_filter);
        mActivity = activity;
        mFilter = filter;
        mCachesProvider  = cachesProvider;
        mRefresher = refresher;
    }

    @Override
    public void act() {
        mFilter.loadFromPrefs(mActivity);

        OnMultiChoiceClickListener clickListener = new OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int ix, boolean enable) {
                mFilter.setEnabled(ix, enable);
            }
        };
        
        OnDismissListener dismissListener = new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface arg0) {
                mFilter.saveToPrefs(mActivity);
                mCachesProvider.setExtraCondition(mFilter.getSqlWhereClause());
                mRefresher.forceRefresh();
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
