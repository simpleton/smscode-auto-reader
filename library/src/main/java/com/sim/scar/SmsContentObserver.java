package com.sim.scar;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sun on 5/12/15.
 */
public class SmsContentObserver extends ContentObserver {

    private static final String TAG           = "SMS_CONETNT_OBSERVER";
    private static final long   SCAN_SMS_TIME = 5 * 60 * 1000;// 5 minutes

    private final Context         context;
    private final String[]        smsContents;
    private final ReceiveListener callback;
    private final Pattern pattern;

    public SmsContentObserver(Handler handler, Context context, String[] tags, ReceiveListener callback) {
        super(handler);
        if (context == null || callback == null || handler == null || Utils.isBlank(tags)) {
            throw new NullPointerException("SmsContentObserver Construction's Parameter should NOT be null");
        }
        this.context = context;
        this.smsContents = tags;
        this.callback = callback;
        this.pattern = Pattern.compile("\\d{4,8}");
    }

    public SmsContentObserver(Handler handler, Context context, String[] tags, ReceiveListener callback, Pattern pattern) {
        super(handler);
        if (context == null || callback == null || handler == null || Utils.isBlank(tags)) {
            throw new NullPointerException("SmsContentObserver Construction's Parameter should NOT be null");
        }
        this.context = context;
        this.smsContents = tags;
        this.callback = callback;
        this.pattern = pattern;
    }
    /**
     * register the sms observer, it's better to invoke in onCreate
     */
    public void registerSMSObserver() {
        context.getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, this);
    }

    /**
     * unregister the sms observer, it's better to invoke in onDestory
     */
    public void unregisterSMSObserver() {
        context.getContentResolver().unregisterContentObserver(this);
    }

    @Override
    public void onChange(final boolean selfChange) {
        super.onChange(selfChange);
        querySms();
    }

    private void querySms() {

        final Uri uri = Uri.parse("content://sms/inbox");
        ContentResolver contentResolver = context.getContentResolver();
        final String[] projection = { "body", "_id", "date" };
        String sqlWhere = getSqlWhere();
        if (sqlWhere == null || sqlWhere.equals("")) {
            return;
        }
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(uri, projection, sqlWhere, null, null);
            int i = -1;
            long time = 0;
            while (cursor.moveToNext()) {
                long smsDate = cursor.getLong(2);
                if (smsDate > time) {
                    time = smsDate;
                    i = cursor.getPosition();
                }
            }

            String observedVerifyNum;
            if (i >= 0) {
                cursor.moveToPosition(i);
                String smsBody = cursor.getString(cursor.getColumnIndex("body"));
                observedVerifyNum = getVerifyNumFromSms(smsBody);
                callback.onReceived(observedVerifyNum);
            } else {
                callback.onReceived(null);
            }

        } catch (Exception exception) {
            Log.e(TAG, "querySms error");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }



    private String getSqlWhere() {
        String sqlWhere = "( ";
        for (int i = 0; i < smsContents.length; i++) {
            if (i == smsContents.length - 1) {
                sqlWhere += " body like \"%" + smsContents[i] + "%\" ) ";
            } else {
                sqlWhere += "body like \"%" + smsContents[i] + "%\" or ";
            }
        }
        sqlWhere += " and date > " + (System.currentTimeMillis() - SCAN_SMS_TIME) + " ";
        Log.v(TAG, "sql where:" + sqlWhere);
        return sqlWhere;
    }

    private String getVerifyNumFromSms(String smsBody) {
        Matcher m = pattern.matcher(smsBody);
        if (m.find()) {
            return m.group();
        }
        return null;
    }
}

