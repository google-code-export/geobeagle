
package com.google.code.geobeagle.intents;

import com.google.code.geobeagle.UriParser;

import android.content.Intent;

public class IntentFromActionUriFactory {
    private final UriParser mUriParser;

    public IntentFromActionUriFactory(UriParser uriParser) {
        mUriParser = uriParser;
    }

    public Intent createIntent(String action, String uri) {
        return new Intent(action, mUriParser.parse(uri));
    }
}
