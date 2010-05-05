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

package com.google.code.geobeagle.test;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.ContextMenu.ContextMenuInfo;

class MenuActionStub implements MenuItem {

    @Override
    public char getAlphabeticShortcut() {
        return 0;
    }

    @Override
    public int getGroupId() {
        return 0;
    }

    @Override
    public Drawable getIcon() {
        return null;
    }

    @Override
    public Intent getIntent() {
        return null;
    }

    @Override
    public int getItemId() {
        return 7;
    }

    @Override
    public ContextMenuInfo getMenuInfo() {
        return null;
    }

    @Override
    public char getNumericShortcut() {
        return 0;
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public SubMenu getSubMenu() {
        return null;
    }

    @Override
    public CharSequence getTitle() {
        return null;
    }

    @Override
    public CharSequence getTitleCondensed() {
        return null;
    }

    @Override
    public boolean hasSubMenu() {
        return false;
    }

    @Override
    public boolean isCheckable() {
        return false;
    }

    @Override
    public boolean isChecked() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public boolean isVisible() {
        return false;
    }

    @Override
    public MenuItem setAlphabeticShortcut(char alphaChar) {
        return null;
    }

    @Override
    public MenuItem setCheckable(boolean checkable) {
        return null;
    }

    @Override
    public MenuItem setChecked(boolean checked) {
        return null;
    }

    @Override
    public MenuItem setEnabled(boolean enabled) {
        return null;
    }

    @Override
    public MenuItem setIcon(Drawable icon) {
        return null;
    }

    @Override
    public MenuItem setIcon(int iconRes) {
        return null;
    }

    @Override
    public MenuItem setIntent(Intent intent) {
        return null;
    }

    @Override
    public MenuItem setNumericShortcut(char numericChar) {
        return null;
    }

    @Override
    public MenuItem setOnMenuItemClickListener(
            OnMenuItemClickListener menuItemClickListener) {
        return null;
    }

    @Override
    public MenuItem setShortcut(char numericChar, char alphaChar) {
        return null;
    }

    @Override
    public MenuItem setTitle(CharSequence title) {
        return null;
    }

    @Override
    public MenuItem setTitle(int title) {
        return null;
    }

    @Override
    public MenuItem setTitleCondensed(CharSequence title) {
        return null;
    }

    @Override
    public MenuItem setVisible(boolean visible) {
        return null;
    }
}