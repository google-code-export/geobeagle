package com.google.code.geobeagle.actions;

import com.google.code.geobeagle.Geocache;

public class MenuActionFromCacheAction extends MenuActionBase {
    private CacheAction mCacheAction;
    private Geocache mTarget;
    
    public MenuActionFromCacheAction(CacheAction cacheAction, Geocache target) {
        super(cacheAction.getId());
        mCacheAction = cacheAction;
        mTarget = target;
    }
    
    @Override
    public void act() {
        mCacheAction.act(mTarget);
    }

}
