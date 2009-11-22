package com.google.code.geobeagle.activity.filterlist;

import com.google.code.geobeagle.CacheFilter;
import com.google.code.geobeagle.R;
//import com.google.code.geobeagle.R;
import android.app.ListActivity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


public class FilterListActivityDelegate {

    private static class FilterListAdapter extends BaseAdapter {
        private final FilterTypeCollection mFilterTypeCollection;
        private LayoutInflater mInflater;
        private Bitmap mIconSelected;
        private Bitmap mIconUnselected;
        /** -1 means no row is selected */
        private int mSelectionIndex = -1;

        public FilterListAdapter(Context context, FilterTypeCollection filterTypeCollection) {
            mFilterTypeCollection = filterTypeCollection;
            // Cache the LayoutInflate to avoid asking for a new one each time.
            mInflater = LayoutInflater.from(context);

            Resources resources = context.getResources();
            mIconSelected = BitmapFactory.decodeResource(resources, 
                    R.drawable.btn_radio_on);
                    //android.R.drawable.radiobutton_on_background);
            mIconUnselected = BitmapFactory.decodeResource(resources, 
                    R.drawable.btn_radio_off);
            //android.R.drawable.radiobutton_off_background);
        }
        @Override
        public int getCount() {
            return mFilterTypeCollection.getCount();
        }
        @Override
        public Object getItem(int position) {
            return mFilterTypeCollection.get(position);
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = mInflater.inflate(R.layout.filterlist_row, null);
            ImageView image = (ImageView) view.findViewById(R.id.icon);
            if (position == mSelectionIndex)
                image.setImageBitmap(mIconSelected);
            else
                image.setImageBitmap(mIconUnselected);
            TextView text = (TextView) view.findViewById(R.id.txt_filter);
            text.setText(mFilterTypeCollection.get(position).getName());
            return view;
        }
        public void setSelection(int index) {
            mSelectionIndex = index;
            notifyDataSetChanged();
        }
    }
    
    private FilterTypeCollection mFilterTypeCollection;
    private FilterListAdapter mAdapter;
    
    public void onCreate(ListActivity activity) {
        mFilterTypeCollection = new FilterTypeCollection(activity);
        mAdapter = new FilterListAdapter(activity, mFilterTypeCollection);
        int ix = mFilterTypeCollection.getIndexOf(mFilterTypeCollection.getActiveFilter());
        mAdapter.setSelection(ix);
        activity.setListAdapter(mAdapter);
    }

    protected void onListItemClick(ListView l, View v, int position, long id) {
        CacheFilter cacheFilter = mFilterTypeCollection.get(position);
        mFilterTypeCollection.setActiveFilter(cacheFilter);
        mAdapter.setSelection(position);
    }
}
