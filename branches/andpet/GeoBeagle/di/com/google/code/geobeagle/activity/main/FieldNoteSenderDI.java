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

package com.google.code.geobeagle.activity.main;

import com.google.code.geobeagle.activity.main.FieldNoteSender;
import com.google.code.geobeagle.activity.main.GeoBeagle;

import android.app.AlertDialog;
import android.view.LayoutInflater;

public class FieldNoteSenderDI {
    public static FieldNoteSender build(GeoBeagle parent, LayoutInflater layoutInflater) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(parent);
        final FieldNoteSender.DialogHelper dialogHelper = new FieldNoteSender.DialogHelper();
        return new FieldNoteSender(layoutInflater, builder, dialogHelper);
    }

}
