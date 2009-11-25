package com.google.code.geobeagle.actions;

import com.google.code.geobeagle.Geocache;

public class MenuActionFromCacheAction implements MenuAction {
    private CacheAction mCacheAction;
    private Geocache mTarget;
    
    public MenuActionFromCacheAction(CacheAction cacheAction, Geocache target) {
        mCacheAction = cacheAction;
        mTarget = target;
    }
    
    @Override
    public void act() {
        mCacheAction.act(mTarget);
    }

    public String getLabel() {
        return mCacheAction.getLabel(mTarget);
    }
}
