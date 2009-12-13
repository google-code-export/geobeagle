package com.google.code.geobeagle.actions;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

/** Adds a confirm dialog before carrying out the nested CacheAction */
public class CacheActionConfirm implements CacheAction {

    private final Activity mActivity;
    private final CacheAction mCacheAction;
    //May Builder only be used once?
    private final AlertDialog.Builder mBuilder;
    private final String mTitle;
    private final String mBody;
    
    public CacheActionConfirm(Activity activity, AlertDialog.Builder builder,
            CacheAction cacheAction, String title, String body) {
        mActivity = activity;
        mBuilder = builder;
        mCacheAction = cacheAction;
        mTitle = title;
        mBody = body;
    }

    private AlertDialog buildAlertDialog(final Geocache cache) {
        final String title = String.format(mTitle, cache.getId());
        final String message = String.format(mBody, cache.getId(), cache.getName());
        mBuilder.setTitle(title);
        mBuilder.setMessage(message)
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                mCacheAction.act(cache);
            }
        })
        .setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                 dialog.cancel();
            }
        });
        AlertDialog alertDialog = mBuilder.create();
        alertDialog.setOwnerActivity(mActivity);
        return alertDialog;
    }
    
    @Override
    public void act(Geocache cache) {
        AlertDialog alertDialog = buildAlertDialog(cache);
        alertDialog.show();
    }

    @Override
    public String getLabel(Geocache geocache) {
        return mCacheAction.getLabel(geocache);
    }

}
