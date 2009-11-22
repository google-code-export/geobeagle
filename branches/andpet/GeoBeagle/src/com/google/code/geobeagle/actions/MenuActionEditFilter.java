package com.google.code.geobeagle.actions;

import com.google.code.geobeagle.CacheFilter;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.Refresher;
import com.google.code.geobeagle.activity.filterlist.FilterTypeCollection;
import com.google.code.geobeagle.database.CachesProviderDb;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

/** Show a dialog to let the user edit the current filter 
 * for which geocaches to display */
public class MenuActionEditFilter implements MenuAction {
    private final Activity mActivity;
    private final FilterTypeCollection mFilterTypeCollection;
    private final CachesProviderDb[] mCachesProviderDb;
    private final Refresher mRefresher;
    private CacheFilter mFilter;
    
    public MenuActionEditFilter(Activity activity,
            CachesProviderDb[] cachesProviderArea,
            Refresher refresher, FilterTypeCollection filterTypeCollection) {
        mActivity = activity;
        mCachesProviderDb = cachesProviderArea;
        mRefresher = refresher;
        mFilterTypeCollection = filterTypeCollection;
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
                mFilter.saveToPreferences();
                dialog.dismiss();
                for (CachesProviderDb provider : mCachesProviderDb) {
                    provider.setFilter(mFilter);
                }
                mRefresher.forceRefresh();
            }
        };

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.filter);
        mFilter = mFilterTypeCollection.getActiveFilter();
        TextView title = (TextView)dialog.findViewById(R.id.TextFilterTitle);
        title.setText("Editing filter \"" + mFilter.getName() + "\"");
        mFilter.pushToGui(gui);
        Button apply = (Button) dialog.findViewById(R.id.ButtonApplyFilter);
        apply.setOnClickListener(mOnApply);
        dialog.show();
    }

}
