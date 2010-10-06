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

import android.database.Cursor;

import java.util.Iterator;

public class CursorIterator implements Iterator<String> {
    private final Cursor cursor;

    CursorIterator(Cursor cursor) {
        this.cursor = cursor;
    }

    @Override
    public boolean hasNext() {
        return !cursor.isAfterLast();
    }

    @Override
    public String next() {
        String cache = cursor.getString(0);
        cursor.moveToNext();
        return cache;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
