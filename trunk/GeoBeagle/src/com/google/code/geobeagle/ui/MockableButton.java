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
