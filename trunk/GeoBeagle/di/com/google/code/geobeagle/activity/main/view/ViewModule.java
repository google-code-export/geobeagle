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

package com.google.code.geobeagle.activity.main.view;

import com.google.code.geobeagle.activity.main.view.WebPageAndDetailsButtonEnabler.CheckButton;
import com.google.code.geobeagle.activity.main.view.WebPageAndDetailsButtonEnabler.CheckButtons;
import com.google.code.geobeagle.activity.main.view.WebPageAndDetailsButtonEnabler.CheckDetailsButton;
import com.google.code.geobeagle.activity.main.view.WebPageAndDetailsButtonEnabler.CheckWebPageButton;
import com.google.inject.Provides;

import roboguice.config.AbstractAndroidModule;

import android.app.Activity;
import android.view.View;

public class ViewModule extends AbstractAndroidModule {
    @Provides
    public WebPageAndDetailsButtonEnabler providesWebPageAndDetailsButtonEnabler(
            Activity geoBeagle, View webPageButton, View detailsButton) {
        final CheckWebPageButton checkWebPageButton = new CheckWebPageButton(webPageButton);
        final CheckDetailsButton checkDetailsButton = new CheckDetailsButton(detailsButton);
        final CheckButtons checkButtons = new CheckButtons(new CheckButton[] {
                checkWebPageButton, checkDetailsButton
        });
        return new WebPageAndDetailsButtonEnabler(geoBeagle, checkButtons);
    }

    @Override
    protected void configure() {
    }
}
