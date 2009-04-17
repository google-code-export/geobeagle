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

package com.google.code.geobeagle.ui.cachelist.row;

import android.view.View;

public class RowInflaterStrategy {
    private final RowInflater[] mInflaters;

    public RowInflaterStrategy(RowInflater[] inflaters) {
        mInflaters = inflaters;
    }

    public View getView(int position, View convertView) {
        for (RowInflater rowInflater : mInflaters) {
            if (rowInflater.match(position)) {
                final View view = rowInflater.inflate(convertView);
                rowInflater.setData(view, position);
                return view;
            }
        }
        return convertView;
    }
}
