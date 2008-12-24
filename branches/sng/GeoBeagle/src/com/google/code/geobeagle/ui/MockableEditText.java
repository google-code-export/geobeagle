
package com.google.code.geobeagle.ui;

import android.view.View.OnFocusChangeListener;
import android.widget.EditText;

public class MockableEditText {
    private final EditText mEditText;

    public MockableEditText(EditText editText) {
        this.mEditText = editText;
    }

    public void setText(CharSequence s) {
        mEditText.setText(s);
    }

    public CharSequence getText() {
        return mEditText.getText();
    }

    public void setOnFocusChangeListener(OnFocusChangeListener l) {
        mEditText.setOnFocusChangeListener(l);
    }
}
