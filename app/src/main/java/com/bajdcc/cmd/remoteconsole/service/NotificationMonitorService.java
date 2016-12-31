package com.bajdcc.cmd.remoteconsole.service;

import android.app.Notification;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

public class NotificationMonitorService extends NotificationListenerService {
    public NotificationMonitorService() {
    }

    final AsyncHttpResponseHandler defaultHandler = new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] response) {
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
        }
    };

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final Bundle data = msg.getData();
            switch (msg.what) {
                case 1:
                    try {
                        AsyncHttpClient client = new AsyncHttpClient();
                        client.setTimeout(1000);
                        client.setMaxRetriesAndTimeout(1, 1000);
                        client.get(data.getString("url"), defaultHandler);
                    } catch (Exception e) {
                    }
                    break;
                case 2:
                    try {
                        AsyncHttpClient client = new AsyncHttpClient();
                        client.setMaxRetriesAndTimeout(1, 1000);
                        RequestParams params = new RequestParams();
                        params.put("user", data.getString("user"));
                        params.put("text", data.getString("text"));
                        params.put("warn", data.getBoolean("warn"));
                        client.get(data.getString("url"), params, defaultHandler);
                    } catch (Exception e) {
                    }
                    break;
                case 3:
                    Toast.makeText(getApplicationContext(), data.getString("msg"), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        Bundle extras = sbn.getNotification().extras;
        String notificationTitle = extras.getString(Notification.EXTRA_TITLE);
        String notificationText = extras.getString(Notification.EXTRA_TEXT);
        try {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            String ip = sp.getString("general_ip", "192.168.1.100");
            String port = sp.getString("general_port", "80");
            String prefix = String.format("http://%s:%s/rpc/", ip, port);
            Message msg = new Message();
            msg.what = 2;
            Bundle data = new Bundle();
            data.putString("url", prefix.concat("danmuku.exe"));
            data.putString("user", notificationTitle);
            data.putString("text", notificationText);
            data.putBoolean("warn", false);
            msg.setData(data);
            handler.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
