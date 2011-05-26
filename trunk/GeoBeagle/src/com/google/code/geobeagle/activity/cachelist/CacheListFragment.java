/*
 ** Licensed under the Apache License, Version 2.0 (the "License");
 ** you may not use this file except in compliance with the License.
 ** You may obtain a copy of the License at
 **
 **     http://www.apache.org/licenses/LICENSE-2.0
 **
 ** Unless required by applicable law or agreed to in writing, software
 ** distributed under the License is distributed on an "AS IS" BASIS,
 ** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ** See the License for the specific language governing permissions and
 ** limitations under the License.
 */

package com.google.code.geobeagle.activity.cachelist;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.cachelist.model.GeocacheVectors;
import com.google.code.geobeagle.activity.cachelist.presenter.GeocacheListAdapter;
import com.google.code.geobeagle.activity.compass.CompassFragment;
import com.google.inject.Injector;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;

public class CacheListFragment extends ListFragment {
    private GeocacheVectors geocacheVectors;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getCacheListDelegate().onCreateFragment(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Injector injector = ((CacheListActivityHoneycomb)getActivity()).getInjector();
        geocacheVectors = injector.getInstance(GeocacheVectors.class);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.cache_list, container, false);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        HeaderViewListAdapter adapter = (HeaderViewListAdapter)l.getAdapter();
        GeocacheListAdapter wrappedAdapter = (GeocacheListAdapter)adapter.getWrappedAdapter();
        wrappedAdapter.setSelected(position-1);

        showDetails(position - 1);
    }

    void showDetails(int position) {
        int positionToShow = position;
        int cacheCount = geocacheVectors.size();
        if (cacheCount == 0)
            return;
        if (positionToShow >= cacheCount)
            positionToShow = cacheCount - 1;
        CompassFragment compassFragment = new CompassFragment();
        Bundle bundle = new Bundle();
        geocacheVectors.get(position).getGeocache().saveToBundle(bundle);

        compassFragment.setArguments(bundle);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.compass_frame, compassFragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return getCacheListDelegate().onOptionsItemSelected(item)
                || super.onOptionsItemSelected(item);
    }

    private CacheListDelegate getCacheListDelegate() {
        return ((CacheListActivityHoneycomb)getActivity()).getCacheListDelegate();
    }
}
