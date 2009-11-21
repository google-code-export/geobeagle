package com.google.code.geobeagle.activity.filterlist;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.filterlist.FilterTypeCollection.FilterType;
import android.app.ListActivity;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.Map;

public class FilterListActivityDelegate {
    
    private FilterTypeCollection mFilterTypeCollection;
    private ListActivity mListActivity;
    
    public void onCreate(ListActivity activity) {
        mListActivity = activity;
        mFilterTypeCollection = new FilterTypeCollection();
        
        //Set up the list adapter
        ArrayList<Map<String, String>> data = mFilterTypeCollection.getAdapterData();
        String[] from = new String[] { "label" };
        int[] to = new int[] { R.id.txt_filter };
        ListAdapter adapter = new SimpleAdapter(activity, data, R.layout.filterlist_row, from, to);
        activity.setListAdapter(adapter);

        int ix = mFilterTypeCollection.getIndexOf(mFilterTypeCollection.getActiveFilter(mListActivity));
        if (ix != -1)
            activity.setSelection(ix);
    }

    protected void onListItemClick(ListView l, View v, int position, long id) {
        FilterType filterType = mFilterTypeCollection.get(position);
        mFilterTypeCollection.setActiveFilter(mListActivity, filterType);
    }
}
