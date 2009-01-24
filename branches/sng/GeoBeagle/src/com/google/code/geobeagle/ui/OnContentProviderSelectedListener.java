package com.google.code.geobeagle.ui;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.ResourceProvider;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

public class OnContentProviderSelectedListener implements OnItemSelectedListener {
    private final MockableTextView mContentProviderCaption;
    private final String[] mObjectNames;
    private final MockableTextView mGotoObjectCaption;

    public OnContentProviderSelectedListener(ResourceProvider resourceProvider,
            MockableTextView contentProviderCaption, MockableTextView gotoCacheCaption) {
        mObjectNames = resourceProvider.getStringArray(R.array.object_names);
        mContentProviderCaption = contentProviderCaption;
        mGotoObjectCaption = gotoCacheCaption;
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mContentProviderCaption.setText("Search for " + mObjectNames[position] + ":");
        mGotoObjectCaption.setText("Go to " + mObjectNames[position] + ":");
    }

    public void onNothingSelected(AdapterView<?> parent) {
    }
}
