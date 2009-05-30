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

package com.google.code.geobeagle.fieldnotes;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.gsm.SmsManager;
import android.widget.Toast;

class SmsSender {

    static final String SMS_DELIVERED = "SMS_DELIVERED";
    static final String SMS_SENT = "SMS_SENT";

    static class SmsBroadcastReceiverDelegate {
        private final Context mContext;
        private int mFailureMessages[];
        private final int mSuccessMessage;

        SmsBroadcastReceiverDelegate(Context context, int successMessage, int failureMessages[]) {
            mContext = context;
            mSuccessMessage = successMessage;
            mFailureMessages = failureMessages;
        }

        void receiveMessage(int resultCode) {
            int length = Toast.LENGTH_SHORT;
            int msg = mSuccessMessage;
            resultCode = Math.min(mFailureMessages.length - 1, resultCode);
            if (resultCode >= 0) {
                length = Toast.LENGTH_LONG;
                msg = mFailureMessages[resultCode];
            }
            Toast.makeText(mContext, msg, length).show();
        }
    }

    private final Activity mActivity;
    private final BroadcastReceiver mBroadcastReceivers[];
    private final Intent mIntents[];
    private final IntentFilter mIntentFilters[];
    private final SmsManager mSmsManager;

    SmsSender(Activity parent, SmsManager smsManager, Intent intents[],
            IntentFilter intentFilters[], BroadcastReceiver broadcastReceivers[]) {
        mActivity = parent;
        mBroadcastReceivers = broadcastReceivers;
        mSmsManager = smsManager;
        mIntents = intents;
        mIntentFilters = intentFilters;
    }

    void sendSMS(String phoneNumber, String message) {
        PendingIntent sentPI = PendingIntent.getBroadcast(mActivity, 0, mIntents[0], 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(mActivity, 0, mIntents[1], 0);

        mActivity.registerReceiver(mBroadcastReceivers[0], mIntentFilters[0]);
        mActivity.registerReceiver(mBroadcastReceivers[1], mIntentFilters[1]);

        mSmsManager.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
    }
}
