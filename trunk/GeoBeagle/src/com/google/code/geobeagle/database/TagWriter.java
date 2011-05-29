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

package com.google.code.geobeagle.database;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.database.filter.Filter;
import com.google.inject.Inject;
import com.google.inject.Injector;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class TagWriter {
    private final Filter filter;
    private final TagStore tagStore;
    private final Context context;

    public TagWriter(Filter filter, TagStore tagStore, Context context) {
        this.filter = filter;
        this.context = context;
        this.tagStore = tagStore;
    }

    @Inject
    public TagWriter(Injector injector) {
        this.filter = injector.getInstance(Filter.class);
        this.tagStore = injector.getInstance(TagStore.class);
        this.context = injector.getInstance(Context.class);
    }

    public void add(CharSequence geocacheId, Tag tag, boolean interactive) {
        Log.d("GeoBeagle", "TagWriter: " + geocacheId + ", " + tag);
        tagStore.addTag(geocacheId, tag);

        if (interactive
                && (!filter.showBasedOnFoundState(tag == Tag.FOUND) || !filter
                        .showBasedOnDnfState(geocacheId))) {
            Toast.makeText(context, R.string.removing_found_cache_from_cache_list,
                    Toast.LENGTH_LONG).show();
            tagStore.hideCache(geocacheId);
        }
    }
}
