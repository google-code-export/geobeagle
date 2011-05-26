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

package com.google.code.geobeagle;

import android.content.Context;
import android.widget.Toast;

public class Toaster  {
    static public class OneTimeToaster implements IToaster {
        public static class OneTimeToasterFactory implements Toaster.ToasterFactory {
            @Override
            public IToaster getToaster(Toaster toaster) {
                return new OneTimeToaster(toaster);
            }
        }

        private boolean mHasShownToaster = false;
        private final Toaster mToaster;

        public OneTimeToaster(Toaster toaster) {
            mToaster = toaster;
        }

        public void showToast(boolean fCondition) {
            if (fCondition && !mHasShownToaster) {
                mToaster.showToast();
                mHasShownToaster = true;
            }
        }
    }

    public static interface ToasterFactory {
        IToaster getToaster(Toaster toaster);
    }

    private final Context mContext;
    private final int mDuration;
    private final int mResId;

    public Toaster(Context context, int resId, int duration) {
        mContext = context;
        mResId = resId;
        mDuration = duration;
    }

    public void showToast() {
        Toast.makeText(mContext, mResId, mDuration).show();
    }
}
