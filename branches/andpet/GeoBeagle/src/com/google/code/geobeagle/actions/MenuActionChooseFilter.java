package com.google.code.geobeagle.actions;

import com.google.code.geobeagle.CacheFilter;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.Refresher;
import com.google.code.geobeagle.database.CachesProviderArea;

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
    private final CachesProviderArea mCachesProviderArea;
    private final Refresher mRefresher;
    
    public MenuActionChooseFilter(Activity activity,
            CacheFilter filter, CachesProviderArea cachesProviderArea,
            Refresher refresher) {
        super(R.string.menu_choose_filter);
        mActivity = activity;
        mFilter = filter;
        mCachesProviderArea = cachesProviderArea;
        mRefresher = refresher;
    }

    private class DialogFilterGui implements CacheFilter.FilterGui {
        private Dialog mDialog;
        public DialogFilterGui(Dialog dialog) {
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
        final Dialog dialog = new Dialog(mActivity);
        final DialogFilterGui gui = new DialogFilterGui(dialog);
        
        OnDismissListener dismissListener = new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface arg0) {
                mFilter.loadFromGui(gui);
                mFilter.saveToPrefs();
                if (mCachesProviderArea != null)
                    mCachesProviderArea.reloadFilter();
                mRefresher.forceRefresh();
            }
        };

        dialog.setContentView(R.layout.filter);
        mFilter.reload();
        mFilter.pushToGui(gui);
        dialog.setOnDismissListener(dismissListener);
        dialog.show();
    }

}
