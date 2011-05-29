package com.google.code.geobeagle.xmlimport;

import com.google.inject.Inject;
import com.google.inject.Provider;

import android.app.ProgressDialog;
import android.content.Context;

public class ProgressDialogWrapper {
        private final Provider<Context> mContextProvider;
        private ProgressDialog mProgressDialog;

        @Inject
        public ProgressDialogWrapper(Provider<Context> context) {
            mContextProvider = context;
        }

        public void dismiss() {
            if (mProgressDialog != null)
                mProgressDialog.dismiss();
        }

        public void setMessage(CharSequence message) {
            mProgressDialog.setMessage(message);
        }

        public void show(String title, String msg) {
            mProgressDialog = ProgressDialog.show(mContextProvider.get(), title, msg);
//            mProgressDialog.setCancelable(true);
        }
    }