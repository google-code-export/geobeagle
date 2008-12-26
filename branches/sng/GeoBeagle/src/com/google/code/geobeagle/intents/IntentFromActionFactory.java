
package com.google.code.geobeagle.intents;

import android.content.Intent;

public class IntentFromActionFactory {
    public Intent createIntent(String action) {
        return new Intent(action);
    }
}
