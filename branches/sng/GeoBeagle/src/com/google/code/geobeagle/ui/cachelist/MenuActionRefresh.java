
package com.google.code.geobeagle.ui.cachelist;

public class MenuActionRefresh implements MenuAction {

    private final GeocacheListPresenter mGeocacheListPresenter;

    public MenuActionRefresh(GeocacheListPresenter geocacheListPresenter) {
        mGeocacheListPresenter = geocacheListPresenter;
    }

    public void act() {
        mGeocacheListPresenter.onResume();
    }
}
