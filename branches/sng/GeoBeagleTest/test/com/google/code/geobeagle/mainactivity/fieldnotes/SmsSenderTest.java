
package com.google.code.geobeagle.mainactivity.fieldnotes;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.mainactivity.fieldnotes.SmsSender.SmsBroadcastReceiverDelegate;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.gsm.SmsManager;
import android.widget.Toast;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        PendingIntent.class, Intent.class, SmsSender.class, SmsManager.class, Toast.class,
        BroadcastReceiver.class
})
public class SmsSenderTest {

    @Test
    public void testSendSMS() throws Exception {
        Activity activity = PowerMock.createMock(Activity.class);
        Intent sentIntent = PowerMock.createMock(Intent.class);
        Intent deliveryIntent = PowerMock.createMock(Intent.class);
        PendingIntent sentPI = PowerMock.createMock(PendingIntent.class);
        PendingIntent deliveryPI = PowerMock.createMock(PendingIntent.class);
        IntentFilter sentIntentFilter = PowerMock.createMock(IntentFilter.class);
        IntentFilter deliveredIntentFilter = PowerMock.createMock(IntentFilter.class);
        FieldNoteSenderDI.SmsBroadcastReceiver smsBroadcastReceiver = PowerMock
                .createMock(FieldNoteSenderDI.SmsBroadcastReceiver.class);
        FieldNoteSenderDI.SmsBroadcastReceiver deliveredMessageReceiver = PowerMock
                .createMock(FieldNoteSenderDI.SmsBroadcastReceiver.class);
        SmsManager smsManager = PowerMock.createMock(SmsManager.class);

        PowerMock.mockStatic(PendingIntent.class);
        EasyMock.expect(PendingIntent.getBroadcast(activity, 0, sentIntent, 0)).andReturn(sentPI);
        EasyMock.expect(PendingIntent.getBroadcast(activity, 0, deliveryIntent, 0)).andReturn(
                deliveryPI);
        EasyMock.expect(activity.registerReceiver(smsBroadcastReceiver, sentIntentFilter))
                .andReturn(null);
        EasyMock.expect(activity.registerReceiver(deliveredMessageReceiver, deliveredIntentFilter))
                .andReturn(null);
        smsManager.sendTextMessage("41411", null, "test", sentPI, deliveryPI);

        PowerMock.replayAll();
        new SmsSender(activity, smsManager, new Intent[] {
                sentIntent, deliveryIntent
        }, new IntentFilter[] {
                sentIntentFilter, deliveredIntentFilter
        }, new BroadcastReceiver[] {
                smsBroadcastReceiver, deliveredMessageReceiver
        }).sendSMS("41411", "test");
        PowerMock.verifyAll();
    }

    @Test
    public void testMessageReceiverDelegate() {
        Context context = PowerMock.createMock(Context.class);
        Toast toast = PowerMock.createMock(Toast.class);
        int deliveryFailureMessages[] = {
                R.string.sms_canceled, R.string.unknown_error
        };

        PowerMock.mockStatic(Toast.class);
        EasyMock.expect(Toast.makeText(context, R.string.sms_sent, Toast.LENGTH_SHORT)).andReturn(
                toast);
        toast.show();

        PowerMock.replayAll();
        new SmsBroadcastReceiverDelegate(context, R.string.sms_sent, deliveryFailureMessages)
                .receiveMessage(-1);
        PowerMock.verifyAll();
    }

    @Test
    public void testMessageReceiverDelegateError() {
        Context context = PowerMock.createMock(Context.class);
        Toast toast = PowerMock.createMock(Toast.class);
        int deliveryFailureMessages[] = {
                R.string.sms_canceled, R.string.unknown_error
        };

        PowerMock.mockStatic(Toast.class);
        EasyMock.expect(Toast.makeText(context, R.string.sms_canceled, Toast.LENGTH_LONG))
                .andReturn(toast);
        toast.show();

        PowerMock.replayAll();
        new SmsBroadcastReceiverDelegate(context, R.string.sms_sent, deliveryFailureMessages)
                .receiveMessage(0);
        PowerMock.verifyAll();
    }

    @Test
    public void testMessageReceiverDelegateUnknownError() {
        Context context = PowerMock.createMock(Context.class);
        Toast toast = PowerMock.createMock(Toast.class);
        int sendFailureMessages[] = {
                R.string.sms_canceled, R.string.generic_failure, R.string.radio_off,
                R.string.null_pdu, R.string.no_service, R.string.unknown_error
        };
        PowerMock.mockStatic(Toast.class);
        EasyMock.expect(Toast.makeText(context, R.string.unknown_error, Toast.LENGTH_LONG))
                .andReturn(toast);
        toast.show();

        PowerMock.replayAll();
        new SmsBroadcastReceiverDelegate(context, R.string.sms_sent, sendFailureMessages)
                .receiveMessage(6);
        PowerMock.verifyAll();
    }
}
