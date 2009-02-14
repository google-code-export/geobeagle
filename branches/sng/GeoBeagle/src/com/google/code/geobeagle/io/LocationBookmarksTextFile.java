/*
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

package com.google.code.geobeagle.io;

import com.google.code.geobeagle.DescriptionsAndLocations;
import com.google.code.geobeagle.Destination;
import com.google.code.geobeagle.LifecycleManager;
import com.google.code.geobeagle.ui.ErrorDisplayer;
import com.google.code.geobeagle.ui.LocationSetter;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.regex.Pattern;

public class LocationBookmarksTextFile implements LifecycleManager {
    private final Context mContext;
    private final DescriptionsAndLocations mDescriptionsAndLocations;
    private final Pattern[] mDestinationPatterns;

    public LocationBookmarksTextFile(Context context, DescriptionsAndLocations descriptionsAndLocations,
            Pattern destinationPatterns[]) {
        mContext = context;
        mDescriptionsAndLocations = descriptionsAndLocations;
        mDestinationPatterns = destinationPatterns;
    }

    protected BufferedOutputStream createBufferedOutputStream(OutputStream outputStream) {
        return new BufferedOutputStream(outputStream);
    }

    protected BufferedReader createBufferedReader(InputStreamReader inputStreamReader) {
        return new BufferedReader(inputStreamReader);
    }

    protected InputStreamReader createInputStreamReader(FileInputStream fileInputStream) {
        return new InputStreamReader(fileInputStream);
    }

    private void readBookmarks() {
        try {
            mDescriptionsAndLocations.clear();
            final FileInputStream fileInputStream = mContext
                    .openFileInput(LocationSetter.FNAME_RECENT_LOCATIONS);
            final InputStreamReader inputStreamReader = createInputStreamReader(fileInputStream);
            final BufferedReader bufferedReader = createBufferedReader(inputStreamReader);
            CharSequence dataLine = null;
            while ((dataLine = bufferedReader.readLine()) != null) {
                saveLocation(dataLine);
            }
            bufferedReader.close();
            inputStreamReader.close();
            fileInputStream.close();
        } catch (final FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final Exception e) {
            e.printStackTrace();
        }
        
    }

    private void saveBookmarks() {
        try {
            final FileOutputStream fileOutputStream = mContext.openFileOutput(
                    LocationSetter.FNAME_RECENT_LOCATIONS, Context.MODE_PRIVATE);
            final BufferedOutputStream bufferedOutputStream = createBufferedOutputStream(fileOutputStream);

            for (final CharSequence location : mDescriptionsAndLocations.getPreviousLocations()) {
                bufferedOutputStream.write((location.toString() + "\n").getBytes());
            }
            bufferedOutputStream.close();
            fileOutputStream.close();
        } catch (final FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void onPause(Editor editor) {
        saveBookmarks();
    }

    public void onResume(SharedPreferences preferences, ErrorDisplayer errorDisplayer) {
        readBookmarks();
    }

    void saveLocation(final CharSequence location) {
        final Destination d = new Destination(location, mDestinationPatterns);
        final CharSequence description = d.getDescription();
        mDescriptionsAndLocations.add(description, location);
    }
}
