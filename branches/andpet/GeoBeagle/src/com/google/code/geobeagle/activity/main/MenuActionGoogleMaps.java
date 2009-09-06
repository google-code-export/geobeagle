
package com.google.code.geobeagle.activity.main;

import com.google.code.geobeagle.activity.MenuAction;
import com.google.code.geobeagle.activity.main.intents.IntentStarterViewUri;

public class MenuActionGoogleMaps implements MenuAction {
    private final IntentStarterViewUri mIntentStarterViewUri;

    public MenuActionGoogleMaps(IntentStarterViewUri intentStarterViewUri) {
        mIntentStarterViewUri = intentStarterViewUri;
    }

    @Override
    public void act() {
        mIntentStarterViewUri.startIntent();
    }
}
