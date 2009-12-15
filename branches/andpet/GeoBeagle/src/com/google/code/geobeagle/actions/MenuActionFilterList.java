package com.google.code.geobeagle.actions;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.filterlist.FilterListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;

public class MenuActionFilterList extends ActionStaticLabel implements MenuAction {
    private final Context mContext;
    
    public MenuActionFilterList(Context context, Resources resources) {
        super(resources, R.string.menu_filterlist);
        mContext = context;
    }
    
    @Override
    public void act() {
        final Intent intent = new Intent(mContext, FilterListActivity.class);
        mContext.startActivity(intent);
    }

    @Override
    public String getLabel() {
        return mContext.getString(R.string.menu_filterlist);
    }
}
