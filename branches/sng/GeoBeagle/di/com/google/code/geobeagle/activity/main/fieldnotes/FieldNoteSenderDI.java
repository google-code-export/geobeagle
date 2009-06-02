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

package com.google.code.geobeagle.activity.main.fieldnotes;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.main.GeoBeagle;
import com.google.code.geobeagle.activity.main.fieldnotes.FieldNoteSender;
import com.google.code.geobeagle.activity.main.fieldnotes.SmsSender;
import com.google.code.geobeagle.activity.main.fieldnotes.SmsSender.SmsBroadcastReceiverDelegate;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.telephony.gsm.SmsManager;
import android.view.LayoutInflater;

public class FieldNoteSenderDI {
    static class SmsBroadcastReceiver extends BroadcastReceiver {
        private SmsSender.SmsBroadcastReceiverDelegate mSentMessageReceiverDelegate;

        SmsBroadcastReceiver(SmsSender.SmsBroadcastReceiverDelegate smsBroadcastReceiverDelegate) {
            mSentMessageReceiverDelegate = smsBroadcastReceiverDelegate;
        }

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            mSentMessageReceiverDelegate.receiveMessage(getResultCode());
        }
    }

    public static FieldNoteSender build(GeoBeagle parent, LayoutInflater layoutInflater) {
        final int sendFailureMessages[] = {
                R.string.sms_canceled, R.string.generic_failure, R.string.radio_off,
                R.string.null_pdu, R.string.no_service, R.string.unknown_error
        };
        final int deliveryFailureMessages[] = {
                R.string.sms_canceled, R.string.unknown_error
        };

        final SmsBroadcastReceiverDelegate sendSmsDelegate = new SmsBroadcastReceiverDelegate(
                parent.getBaseContext(), R.string.sms_sent, sendFailureMessages);
        final SmsBroadcastReceiverDelegate deliveredSmsDelegate = new SmsBroadcastReceiverDelegate(
                parent.getBaseContext(), R.string.sms_delivered, deliveryFailureMessages);

        final FieldNoteSenderDI.SmsBroadcastReceiver smsBroadcastReceiver = new FieldNoteSenderDI.SmsBroadcastReceiver(
                sendSmsDelegate);
        final FieldNoteSenderDI.SmsBroadcastReceiver deliveredMessageReceiver = new FieldNoteSenderDI.SmsBroadcastReceiver(
                deliveredSmsDelegate);

        final SmsManager sms = SmsManager.getDefault();

        final SmsSender smsSender = new SmsSender(parent, sms, new Intent[] {
                new Intent(SmsSender.SMS_SENT), new Intent(SmsSender.SMS_DELIVERED)
        }, new IntentFilter[] {
                new IntentFilter(SmsSender.SMS_SENT), new IntentFilter(SmsSender.SMS_DELIVERED)
        }, new BroadcastReceiver[] {
                smsBroadcastReceiver, deliveredMessageReceiver
        });
        final AlertDialog.Builder builder = new AlertDialog.Builder(parent);
        final FieldNoteSender.DialogHelper dialogHelper = new FieldNoteSender.DialogHelper();
        final Resources resources = parent.getResources();
        return new FieldNoteSender(layoutInflater, smsSender, builder, dialogHelper, resources);
    }

}
