package com.google.code.geobeagle.activity.cachelist.actions.menu;

import com.google.code.geobeagle.R;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListActivity;

public class HoneycombCompassFrameHider implements CompassFrameHider {
    @Override
    public void hideCompassFrame(Activity activity) {
        ListActivity listActivity = (ListActivity)activity;
        FragmentManager fragmentManager = listActivity.getFragmentManager();
        Fragment compassFragment = fragmentManager.findFragmentById(R.id.compass_frame);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.hide(compassFragment);
        transaction.commit();
    }
}