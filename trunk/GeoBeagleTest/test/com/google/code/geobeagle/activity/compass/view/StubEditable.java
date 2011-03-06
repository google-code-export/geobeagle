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

package com.google.code.geobeagle.activity.compass.view;

import android.text.Editable;
import android.text.InputFilter;

class StubEditable implements Editable {
    private final String mString;

    public StubEditable(String s) {
        mString = s;
    }

    public Editable append(char arg0) {
        return null;
    }

    public Editable append(CharSequence arg0) {
        return null;
    }

    public Editable append(CharSequence arg0, int arg1, int arg2) {
        return null;
    }

    public char charAt(int index) {
        return mString.charAt(index);
    }

    public void clear() {
    }

    public void clearSpans() {
    }

    public Editable delete(int arg0, int arg1) {
        return null;
    }

    public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
        mString.getChars(srcBegin, srcEnd, dst, dstBegin);
    }

    public InputFilter[] getFilters() {
        return null;
    }

    public int getSpanEnd(Object arg0) {
        return 0;
    }

    public int getSpanFlags(Object arg0) {
        return 0;
    }

    public <T> T[] getSpans(int arg0, int arg1, Class<T> arg2) {
        return null;
    }

    public int getSpanStart(Object arg0) {
        return 0;
    }

    public Editable insert(int arg0, CharSequence arg1) {
        return null;
    }

    public Editable insert(int arg0, CharSequence arg1, int arg2, int arg3) {
        return null;
    }

    public int length() {
        return mString.length();
    }

    @SuppressWarnings("unchecked")
    public int nextSpanTransition(int arg0, int arg1, Class arg2) {
        return 0;
    }

    public void removeSpan(Object arg0) {
    }

    public Editable replace(int arg0, int arg1, CharSequence arg2) {
        return null;
    }

    public Editable replace(int arg0, int arg1, CharSequence arg2, int arg3, int arg4) {
        return null;
    }

    public void setFilters(InputFilter[] arg0) {
    }

    public void setSpan(Object arg0, int arg1, int arg2, int arg3) {
    }

    public CharSequence subSequence(int start, int end) {
        return mString.subSequence(start, end);
    }

    @Override
    public String toString() {
        return mString;
    }
}
