package com.google.code.geobeagle.actions;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

/** Adds a confirm dialog before carrying out the nested MenuAction */
public class MenuActionConfirm implements MenuAction {

    private final Activity mActivity;
    private final MenuAction mMenuAction;
    //May Builder only be used once?
    private final AlertDialog.Builder mBuilder;
    private final String mTitle;
    private final String mBody;
    
    public MenuActionConfirm(Activity activity, AlertDialog.Builder builder,
            MenuAction menuAction, String title, String body) {
        mActivity = activity;
        mBuilder = builder;
        mMenuAction = menuAction;
        mTitle = title;
        mBody = body;
    }

    private AlertDialog buildAlertDialog() {
        mBuilder.setTitle(mTitle);
        mBuilder.setMessage(mBody)
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                mMenuAction.act();
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
    public void act() {
        AlertDialog alertDialog = buildAlertDialog();
        alertDialog.show();
    }

    @Override
    public String getLabel() {
        return mMenuAction.getLabel();
    }

}
