package com.bajdcc.cmd.remoteconsole;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.net.URLEncoder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

public class NewConsoleActivity extends AppCompatActivity {

    private String prefix = null;
    private boolean init = false;
    @BindView(R.id.editMsg)
    EditText edtMsg = null;
    @BindView(R.id.editUser)
    EditText edtUser = null;
    @BindView(R.id.editMessage)
    EditText edtText = null;
    @BindView(R.id.checkWarn)
    CheckBox checkWarn = null;
    @BindView(R.id.checkNotify)
    CheckBox checkNotify = null;

    final AsyncHttpResponseHandler defaultHandler = new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] response) {
            Message msg = new Message();
            msg.what = 3;
            Bundle data = new Bundle();
            data.putString("msg", "发送成功");
            msg.setData(data);
            handler.sendMessage(msg);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
            String message = errorResponse != null ? new String(errorResponse) : e.getMessage();
            Message msg = new Message();
            msg.what = 3;
            Bundle data = new Bundle();
            data.putString("msg", message == null ? "出现错误" : message);
            msg.setData(data);
            handler.sendMessage(msg);
        }
    };

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final Bundle data = msg.getData();
            switch (msg.what) {
                case 1:
                    Log.d(NewConsoleActivity.this.getLocalClassName(), data.getString("url"));
                    try {
                        AsyncHttpClient client = new AsyncHttpClient();
                        client.setTimeout(1000);
                        client.setMaxRetriesAndTimeout(1, 1000);
                        client.get(data.getString("url"), defaultHandler);
                    } catch (Exception e) {
                        Log.e(NewConsoleActivity.this.getLocalClassName(), e.getMessage());
                    }
                    break;
                case 2:
                    Log.d(NewConsoleActivity.this.getLocalClassName(), data.getString("url"));
                    try {
                        AsyncHttpClient client = new AsyncHttpClient();
                        client.setMaxRetriesAndTimeout(1, 1000);
                        RequestParams params = new RequestParams();
                        params.put("user", data.getString("user"));
                        params.put("text", data.getString("text"));
                        params.put("warn", data.getBoolean("warn"));
                        client.get(data.getString("url"), params, defaultHandler);
                    } catch (Exception e) {
                        Log.e(NewConsoleActivity.this.getLocalClassName(), e.getMessage());
                    }
                    break;
                case 3:
                    Toast.makeText(getApplicationContext(), data.getString("msg"), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_console);

        ButterKnife.bind(this);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String ip = sp.getString("general_ip", "192.168.1.100");
        String port = sp.getString("general_port", "80");
        prefix = String.format("http://%s:%s/rpc/", ip, port);

        init = false;
        checkNotify.setChecked(isNotificationListenerServiceEnabled());
        init = true;
    }

    private boolean isNotificationListenerServiceEnabled() {
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (String name : names) {
                final ComponentName cn = ComponentName.unflattenFromString(name);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @OnCheckedChanged(R.id.checkNotify)
    void onCheckNotify(boolean checked) {
        if (!init)
            return;
        startActivityForResult(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"), 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            boolean checked = isNotificationListenerServiceEnabled();
            if (checkNotify.isChecked() == checked)
                return;
            if (checked) {
                Toast.makeText(getApplicationContext(), "开启消息推送", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "关闭消息推送", Toast.LENGTH_SHORT).show();
            }
            init = false;
            checkNotify.setChecked(checked);
            init = true;
        }
    }

    @OnClick(R.id.btnMsg)
    void onClickMsg() {
        if (edtMsg.getText().toString().isEmpty()) return;
        try {
            Message msg = new Message();
            msg.what = 1;
            Bundle data = new Bundle();
            data.putString("url", prefix.concat("alert.exe/").concat(URLEncoder.encode(edtMsg.getText().toString(), "UTF-8")));
            msg.setData(data);
            handler.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.btnLock)
    void onClickLock() {
        try {
            Message msg = new Message();
            msg.what = 1;
            Bundle data = new Bundle();
            data.putString("url", prefix.concat("lock.exe"));
            msg.setData(data);
            handler.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.btnSendPopup)
    void onClickSP() {
        try {
            Message msg = new Message();
            msg.what = 2;
            Bundle data = new Bundle();
            data.putString("url", prefix.concat("danmuku.exe"));
            data.putString("user", edtUser.getText().toString());
            data.putString("text", edtText.getText().toString());
            data.putBoolean("warn", checkWarn.isChecked());
            msg.setData(data);
            handler.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @OnClick(R.id.btnTogglePopup)
    void onClickTP() {
        try {
            Message msg = new Message();
            msg.what = 1;
            Bundle data = new Bundle();
            data.putString("url", prefix.concat("danmuku.full"));
            msg.setData(data);
            handler.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @OnClick(R.id.btnClosePopup)
    void onClickCP() {
        try {
            Message msg = new Message();
            msg.what = 1;
            Bundle data = new Bundle();
            data.putString("url", prefix.concat("danmuku.shutdown"));
            msg.setData(data);
            handler.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
