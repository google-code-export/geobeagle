package com.google.code.geobeagle.actions;

import com.google.code.geobeagle.CacheFilter;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.Refresher;
import com.google.code.geobeagle.database.CachesProviderArea;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

/** Show a dialog to let the user decide which geocaches to show */
public class MenuActionChooseFilter implements MenuAction {
    private final Activity mActivity;
    private final CacheFilter mFilter;
    private final CachesProviderArea[] mCachesProviderArea;
    private final Refresher mRefresher;
    
    public MenuActionChooseFilter(Activity activity,
            CacheFilter filter, CachesProviderArea[] cachesProviderArea,
            Refresher refresher) {
        mActivity = activity;
        mFilter = filter;
        mCachesProviderArea = cachesProviderArea;
        mRefresher = refresher;
    }

    @Override
    public String getLabel() {
        return mActivity.getResources().getString(R.string.menu_choose_filter);
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
        
        final OnClickListener mOnApply = new OnClickListener() {
            @Override
            public void onClick(View v) {
                mFilter.loadFromGui(gui);
                mFilter.saveToPrefs();
                dialog.dismiss();
                for (CachesProviderArea provider : mCachesProviderArea) {
                    provider.reloadFilter();
                }
                mRefresher.forceRefresh();
            }
        };

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.filter);
        //dialog.setTitle("Cache types to show");
        mFilter.reload();
        mFilter.pushToGui(gui);
        Button apply = (Button) dialog.findViewById(R.id.ButtonApplyFilter);
        apply.setOnClickListener(mOnApply);
        dialog.show();
    }

}
