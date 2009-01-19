package com.google.code.geobeagle.ui;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.ResourceProvider;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

public class OnContentProviderSelectedListener implements OnItemSelectedListener {
    private final String[] objectNames;
    private final MockableTextView mContentProviderCaption;

    public OnContentProviderSelectedListener(ResourceProvider resourceProvider,
            MockableTextView contentProviderCaption) {
        this.objectNames = resourceProvider.getStringArray(R.array.object_names);
        mContentProviderCaption = contentProviderCaption;
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mContentProviderCaption.setText("Select " + objectNames[position] + ":");
    }

    public void onNothingSelected(AdapterView<?> parent) {
    }
}
