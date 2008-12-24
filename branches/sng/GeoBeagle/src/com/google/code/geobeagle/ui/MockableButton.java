
package com.google.code.geobeagle.ui;

import android.view.View.OnClickListener;
import android.widget.Button;

public class MockableButton extends MockableTextView {
    private Button mButton;

    public MockableButton(Button button) {
        super(button);
        this.mButton = button;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        mButton.setOnClickListener(onClickListener);
    }

    public void setTextColor(int red) {
        mButton.setTextColor(red);
    }
}
