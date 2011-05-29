/*
 ** Licensed under the Apache License, Version 2.import com.google.code.geobeagle.activity.cachelist.GeoBeagleTest;

import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
.apache.org/licenses/LICENSE-2.0
 **
 ** Unless required by applicable law or agreed to in writing, software
 ** distributed under the License is distributed on an "AS IS" BASIS,
 ** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ** See the License for the specific language governing permissions and
 ** limitations under the License.
 */

package com.google.code.geobeagle.database.filter;

import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import com.google.code.geobeagle.activity.cachelist.GeoBeagleTest;
import com.google.code.geobeagle.activity.cachelist.presenter.filter.UpdateFilterHandler;
import com.google.code.geobeagle.activity.preferences.Preferences;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import android.content.SharedPreferences;

@RunWith(PowerMockRunner.class)
public class UpdateFilterWorkerTest extends GeoBeagleTest {

    private CacheVisibilityStore cacheVisibilityStore;
    private SharedPreferences sharedPreferences;
    private UpdateFilterHandler updateFilterHandler;

    @Before
    public void setUp() {
        cacheVisibilityStore = createMock(CacheVisibilityStore.class);
        sharedPreferences = createMock(SharedPreferences.class);
        updateFilterHandler = createMock(UpdateFilterHandler.class);
    }

    @Test
    public void showAllCachesShouldDismissDialogBox() {
        cacheVisibilityStore.setAllVisible();
        expect(sharedPreferences.getBoolean(Preferences.SHOW_WAYPOINTS, false)).andReturn(true);
        expect(sharedPreferences.getBoolean(Preferences.SHOW_FOUND_CACHES, false)).andReturn(
                true);
        expect(sharedPreferences.getBoolean(Preferences.SHOW_DNF_CACHES, true)).andReturn(true);
        expect(sharedPreferences.getBoolean(Preferences.SHOW_UNAVAILABLE_CACHES, false))
                .andReturn(true);
        updateFilterHandler.endFiltering();
        replayAll();

        new UpdateFilterWorker(sharedPreferences, updateFilterHandler, cacheVisibilityStore).run();
        verifyAll();
    }
}
