
package com.google.code.geobeagle.activity.main.menuactions;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.actions.MenuAction;
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

    @Override
    public int getId() {
        return R.string.menu_google_maps;
    }
}
