package com.bajdcc.cmd.remoteconsole;

import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bajdcc.cmd.remoteconsole.entity.BroadcastMsg;
import com.bajdcc.cmd.remoteconsole.service.PushService;

public class PushingActivity extends AppCompatActivity {

    private String mDeviceID;
    private TextView textView;
    private Button[] btnGroup;
    private BroadcastReceiver receiver;
    private SharedPreferences sharedPref;
    public static final String DYNAMIC_ACTION = "com.bajdcc.cmd.remotecontrol.pushing.dynamic";
    public static final String PREF_MESSAGE = "com.bajdcc.cmd.remotecontrol.pushing.message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pushing);
        textView = (TextView) findViewById(R.id.textMessage);
        textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = textView.getText().toString();
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setPrimaryClip(ClipData.newPlainText("pushing_text", text));
            }
        });
        final Button startButton = ((Button) findViewById(R.id.btnStart));
        final Button stopButton = ((Button) findViewById(R.id.btnStop));
        startButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Editor editor = getSharedPreferences(PushService.TAG, MODE_PRIVATE).edit();
                editor.putString(PushService.PREF_DEVICE_ID, mDeviceID);
                editor.apply();
                PushService.actionStart(getApplicationContext());
                startButton.setEnabled(false);
                enableButtonGroup(true);
                stopButton.setEnabled(true);
            }
        });
        stopButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                PushService.actionStop(getApplicationContext());
                startButton.setEnabled(true);
                enableButtonGroup(false);
                stopButton.setEnabled(false);
            }
        });
        sharedPref = getSharedPreferences(this.getLocalClassName(), MODE_PRIVATE);
        final int[] btnList = new int[]{
                R.id.btnNavPrev,
                R.id.btnNavNext,
                R.id.btnPlay,
                R.id.btnCycle,
                R.id.btnDanmuku,
                R.id.btnLeft,
                R.id.btnRight,
        };
        final String[] commandList = new String[]{
                "prev",
                "next",
                "space",
                "single",
                "danmuku",
                "left",
                "right"
        };
        btnGroup = new Button[btnList.length];
        for (int i = 0; i < btnList.length; i++) {
            btnGroup[i] = (Button) findViewById(btnList[i]);
            final String command = commandList[i];
            btnGroup[i].setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    PushService.actionCommand(getApplicationContext(), command);
                }
            });
        }
        sharedPref = getSharedPreferences(PushService.TAG, MODE_PRIVATE);
        boolean started = sharedPref.getBoolean(PushService.PREF_STARTED, false);
        startButton.setEnabled(!started);
        stopButton.setEnabled(started);
        enableButtonGroup(started);
    }

    private void enableButtonGroup(boolean enabled) {
        for (Button btn : btnGroup) {
            btn.setEnabled(enabled);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        String msg = sharedPref.getString(PREF_MESSAGE, null);
        if (msg != null) {
            textView.setText(msg);
        }
        mDeviceID = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);
        IntentFilter filter_dynamic = new IntentFilter();
        filter_dynamic.addAction(DYNAMIC_ACTION);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(DYNAMIC_ACTION)) {
                    BroadcastMsg msg = intent.getParcelableExtra("msg");
                    textView.setText(msg.getMsg());
                }
            }
        };
        registerReceiver(receiver, filter_dynamic);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }
}
