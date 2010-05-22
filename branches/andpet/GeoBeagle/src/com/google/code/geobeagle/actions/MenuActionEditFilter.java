package com.google.code.geobeagle.actions;

import com.google.code.geobeagle.CacheFilter;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.Refresher;
import com.google.code.geobeagle.activity.filterlist.FilterTypeCollection;
import android.app.Activity;
import android.app.Dialog;
import android.content.res.Resources;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

/** Show a dialog to let the user edit the current filter 
 * for which geocaches to display */
public class MenuActionEditFilter extends ActionStaticLabel implements MenuAction {
    private final Activity mActivity;
    private final FilterTypeCollection mFilterTypeCollection;
    private final CacheFilterUpdater mCacheFilterUpdater;
    private final Refresher mRefresher;
    private CacheFilter mFilter;
    
    public MenuActionEditFilter(Activity activity,
            CacheFilterUpdater cacheFilterUpdater,
            Refresher refresher, FilterTypeCollection filterTypeCollection, Resources resources) {
        super(resources, R.string.menu_edit_filter);
        mActivity = activity;
        mCacheFilterUpdater = cacheFilterUpdater;
        mRefresher = refresher;
        mFilterTypeCollection = filterTypeCollection;
    }

    @Override
    public String getLabel() {
        return mActivity.getResources().getString(R.string.menu_edit_filter);
    }
    
    private class DialogFilterGui implements CacheFilter.FilterGui {
        private Dialog mDialog;
        public DialogFilterGui(Dialog dialog) {
            mDialog = dialog;
        }
        @Override
        public boolean getBoolean(int id) {
            return ((CompoundButton)mDialog.findViewById(id)).isChecked();
        }
        @Override
        public String getString(int id) {
            return ((EditText)mDialog.findViewById(id)).getText().toString();
        }
        @Override
        public void setBoolean(int id, boolean value) {
            ((CompoundButton)mDialog.findViewById(id)).setChecked(value);
        }
        @Override
        public void setString(int id, String value) {
            ((EditText)mDialog.findViewById(id)).setText(value);
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
                mCacheFilterUpdater.loadActiveFilter();
                mRefresher.forceRefresh();
            }
        };

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.filter);

        SetOpposingCheckBoxes(dialog, R.id.CheckBoxRequireFavorites, 
                R.id.CheckBoxForbidFavorites);
        SetOpposingCheckBoxes(dialog, R.id.CheckBoxRequireFound, 
                R.id.CheckBoxForbidFound);
        SetOpposingCheckBoxes(dialog, R.id.CheckBoxRequireDNF, 
                R.id.CheckBoxForbidDNF);
        
        mFilter = mFilterTypeCollection.getActiveFilter();
        TextView title = (TextView)dialog.findViewById(R.id.TextFilterTitle);
        title.setText("Editing filter \"" + mFilter.getName() + "\"");
        mFilter.pushToGui(gui);
        Button apply = (Button) dialog.findViewById(R.id.ButtonApplyFilter);
        apply.setOnClickListener(mOnApply);
        dialog.show();
    }

    private static final OnClickListener OnCheck = new OnClickListener() {
        @Override
        public void onClick(View v) {
            final CheckBox checkBox = (CheckBox)v;
            if (checkBox.isChecked())
                ((CheckBox)checkBox.getTag()).setChecked(false);
        }
    };
    
    /** Registers two checkboxes to be opposing -- 
     * selecting one will unselect the other */
    private static void SetOpposingCheckBoxes(Dialog dialog, int id1, int id2) {
        CheckBox checkBox1 = (CheckBox)dialog.findViewById(id1);
        CheckBox checkBox2 = (CheckBox)dialog.findViewById(id2);
        checkBox1.setTag(checkBox2);
        checkBox1.setOnClickListener(OnCheck);
        checkBox2.setTag(checkBox1);
        checkBox2.setOnClickListener(OnCheck);
    }
    
}
