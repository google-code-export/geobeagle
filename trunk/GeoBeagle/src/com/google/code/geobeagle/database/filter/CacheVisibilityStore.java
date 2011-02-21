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

package com.google.code.geobeagle.database.filter;

import com.google.code.geobeagle.database.DbFrontend;
import com.google.code.geobeagle.database.Tag;
import com.google.inject.Inject;

class CacheVisibilityStore {
    private final DbFrontend dbFrontEnd;

    @Inject
    public CacheVisibilityStore(DbFrontend dbFrontend) {
        this.dbFrontEnd = dbFrontend;
    }

    void setInvisible(String cache) {
        dbFrontEnd.getDatabase().execSQL("UPDATE CACHES SET Visible = 0 WHERE ID = ?", cache);
    }

    void setAllVisible() {
        dbFrontEnd.getDatabase().execSQL("UPDATE CACHES SET Visible = 1");
    }

    public void hideUnavailableCaches() {
        dbFrontEnd.getDatabase().execSQL("UPDATE CACHES SET Visible = 0 WHERE Available = 0");
    }

    public void hideWaypoints() {
        dbFrontEnd.getDatabase().execSQL("UPDATE CACHES SET Visible = 0 WHERE CacheType >= 20");
    }

    public void hideFoundCaches(boolean hideFound, boolean hideDnf) {
        String whereString = "";
        if (hideFound && hideDnf) {
            whereString = "";
        } else if (hideFound && !hideDnf) {
            whereString = "WHERE Id = " + Tag.FOUND.ordinal();
        } else if (!hideFound && hideDnf) {
            whereString = "WHERE Id = " + Tag.DNF.ordinal();
        } else {
            return;
        }
        dbFrontEnd.getDatabase().execSQL(
                "UPDATE CACHES SET Visible = 0 WHERE Id IN " + "(SELECT Cache FROM TAGS "
                        + whereString + ")");
    }
}
