package com.google.code.geobeagle.actions;

import com.google.code.geobeagle.CacheFilter;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.Refresher;
import com.google.code.geobeagle.database.CachesProvider;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.widget.CheckBox;
import android.widget.EditText;

/** Show a dialog to let the user decide which geocaches to show */
public class MenuActionChooseFilter extends MenuActionBase {
    private final Activity mActivity;
    private final CacheFilter mFilter;
    private final CachesProvider mCachesProvider;
    private final Refresher mRefresher;
    
    public MenuActionChooseFilter(Activity activity,
            CacheFilter filter, CachesProvider cachesProvider,
            Refresher refresher) {
        super(R.string.menu_choose_filter);
        mActivity = activity;
        mFilter = filter;
        mCachesProvider  = cachesProvider;
        mRefresher = refresher;
    }

    private class DialogSettingsProvider implements CacheFilter.SettingsProvider {
        private Dialog mDialog;
        public DialogSettingsProvider(Dialog dialog) {
            mDialog = dialog;
        }
        @Override
        public boolean getBoolean(int id) {
            return ((CheckBox)mDialog.findViewById(id)).isChecked();
        }
        @Override
        public String getString(int id) {
            return ((EditText)mDialog.findViewById(R.id.FilterString)).getText().toString();
        }
        @Override
        public void setBoolean(int id, boolean value) {
            ((CheckBox)mDialog.findViewById(id)).setChecked(value);
        }
        @Override
        public void setString(int id, String value) {
            ((EditText)mDialog.findViewById(R.id.FilterString)).setText(value);
        }
    };
    
    @Override
    public void act() {
        mFilter.loadFromPrefs(mActivity);

        final Dialog dialog = new Dialog(mActivity);
        final DialogSettingsProvider provider = new DialogSettingsProvider(dialog);
        
        OnDismissListener dismissListener = new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface arg0) {
                mFilter.setFromProvider(provider);
                mFilter.saveToPrefs(mActivity);
                mCachesProvider.setExtraCondition(mFilter.getSqlWhereClause());
                mRefresher.forceRefresh();
            }
        };

        dialog.setContentView(R.layout.filter);
        mFilter.pushToProvider(provider);
        dialog.setOnDismissListener(dismissListener);
        dialog.show();
    }

}
