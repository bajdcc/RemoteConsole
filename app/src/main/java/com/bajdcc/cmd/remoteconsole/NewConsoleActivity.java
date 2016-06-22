package com.bajdcc.cmd.remoteconsole;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.net.URLEncoder;

import cz.msebera.android.httpclient.Header;

public class NewConsoleActivity extends AppCompatActivity {

    private String prefix = null;
    private EditText edtMsg = null;
    private EditText edtUser = null;
    private EditText edtText = null;
    private CheckBox checkWarn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_console);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String ip = sp.getString("general_ip", "192.168.1.100");
        String port = sp.getString("general_port", "80");
        prefix = String.format("http://%s:%s/rpc/", ip, port);

        edtMsg = (EditText) findViewById(R.id.editMsg);
        edtUser = (EditText) findViewById(R.id.editUser);
        edtText = (EditText) findViewById(R.id.editMessage);
        checkWarn = (CheckBox) findViewById(R.id.checkWarn);

        final AsyncHttpResponseHandler defaultHandler = new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                Toast.makeText(getApplicationContext(), "发送成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                if (errorResponse != null) {
                    Toast.makeText(getApplicationContext(), new String(errorResponse), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        };

        findViewById(R.id.btnMsg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AsyncHttpClient client = new AsyncHttpClient();
                    client.setMaxRetriesAndTimeout(1, 1000);
                    client.get(prefix.concat("alert.exe/").concat(URLEncoder.encode(edtMsg.getText().toString(), "UTF-8")), defaultHandler);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        findViewById(R.id.btnLock).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AsyncHttpClient client = new AsyncHttpClient();
                    client.setMaxRetriesAndTimeout(1, 1000);
                    client.get(prefix.concat("lock.exe"), defaultHandler);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        findViewById(R.id.btnSendPopup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AsyncHttpClient client = new AsyncHttpClient();
                    client.setMaxRetriesAndTimeout(1, 1000);
                    RequestParams params = new RequestParams();
                    params.put("user", edtUser.getText().toString());
                    params.put("text", edtText.getText().toString());
                    params.put("warn", checkWarn.isChecked());
                    client.get(prefix.concat("danmuku.exe"), params, defaultHandler);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        findViewById(R.id.btnTogglePopup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AsyncHttpClient client = new AsyncHttpClient();
                    client.setMaxRetriesAndTimeout(1, 1000);
                    client.get(prefix.concat("danmuku.full"), defaultHandler);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        findViewById(R.id.btnClosePopup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AsyncHttpClient client = new AsyncHttpClient();
                    client.setMaxRetriesAndTimeout(1, 1000);
                    client.get(prefix.concat("danmuku.shutdown"), defaultHandler);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
