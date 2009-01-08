
package com.google.code.geobeagle.ui;

import android.widget.EditText;

public class TooString {
    private final EditText mEditText;

    public TooString(EditText editText) {
        this.mEditText = editText;
    }

    public CharSequence tooString() {
        return mEditText.getText().toString();
    }
}
