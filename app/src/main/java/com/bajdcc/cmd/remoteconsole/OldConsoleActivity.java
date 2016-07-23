package com.bajdcc.cmd.remoteconsole;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OldConsoleActivity extends AppCompatActivity {

    @BindView(R.id.edit_IP)
    EditText editHost = null;
    @BindView(R.id.edit_PORT)
    EditText editPort = null;
    @BindView(R.id.button_exit)
    Button btnExit = null;
    @BindView(R.id.button_conn)
    Button btnConn = null;
    @BindView(R.id.button_input)
    Button btnInput = null;
    @BindView(R.id.textView_status)
    TextView txvConn = null;
    @BindView(R.id.editText_msg)
    EditText txvMsg = null;
    @BindView(R.id.editText_input)
    EditText txvInput = null;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            switch (msg.what) {
                case 1:
                    txvConn.setText(data.getString("conn"));
                    txvMsg.setText(data.getString("msg"));
                    break;
                case 2:
                    txvConn.setText(data.getString("conn"));
                    txvMsg.setText(data.getString("msg"));
                    String err = data.getString("err");
                    Log.d(getLocalClassName(), err);
                    Toast.makeText(getApplicationContext(),
                            err == null ? "出现错误" : err, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_console);

        ButterKnife.bind(this);

        btnExit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnConn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SendMessage(getString(R.string.msg_IR));
            }
        });


        btnInput.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SendMessage(getString(R.string.static_str_secret_prefix).concat(txvInput.getText().toString()));
            }
        });
    }

    @BindString(R.string.msg_00)
    String strMsg00;
    @BindString(R.string.msg_01)
    String strMsg01;
    @BindString(R.string.msg_02)
    String strMsg02;
    @BindString(R.string.msg_03)
    String strMsg03;
    @BindString(R.string.msg_04)
    String strMsg04;
    @BindString(R.string.msg_05)
    String strMsg05;
    @BindString(R.string.msg_06)
    String strMsg06;
    @BindString(R.string.msg_07)
    String strMsg07;
    @BindString(R.string.msg_F1)
    String strMsgf1;
    @BindString(R.string.msg_F3)
    String strMsgf3;
    @BindString(R.string.msg_F6)
    String strMsgf6;
    @BindString(R.string.msg_d1)
    String strMsgd1;
    @BindString(R.string.msg_d2)
    String strMsgd2;
    @BindString(R.string.msg_d3)
    String strMsgd3;
    @BindString(R.string.msg_d4)
    String strMsgd4;
    @BindString(R.string.msg_ST)
    String strMsgst;

    @OnClick(R.id.Button00)
    void onClick00() {
        SendMessage(getString(R.string.msg_00));
    }

    @OnClick(R.id.Button01)
    void onClick01() {
        SendMessage(getString(R.string.msg_01));
    }

    @OnClick(R.id.Button02)
    void onClick02() {
        SendMessage(getString(R.string.msg_02));
    }

    @OnClick(R.id.Button03)
    void onClick03() {
        SendMessage(getString(R.string.msg_03));
    }

    @OnClick(R.id.Button04)
    void onClick04() {
        SendMessage(getString(R.string.msg_04));
    }

    @OnClick(R.id.Button05)
    void onClick05() {
        SendMessage(getString(R.string.msg_05));
    }

    @OnClick(R.id.Button06)
    void onClick06() {
        SendMessage(getString(R.string.msg_06));
    }

    @OnClick(R.id.Button07)
    void onClick07() {
        SendMessage(getString(R.string.msg_07));
    }

    @OnClick(R.id.ButtonF1)
    void onClickf1() {
        SendMessage(getString(R.string.msg_F1));
    }

    @OnClick(R.id.ButtonF3)
    void onClickf3() {
        SendMessage(getString(R.string.msg_F3));
    }

    @OnClick(R.id.ButtonF6)
    void onClickf6() {
        SendMessage(getString(R.string.msg_F6));
    }

    @OnClick(R.id.Button_d1)
    void onClickd1() {
        SendMessage(getString(R.string.msg_d1));
    }

    @OnClick(R.id.Button_d2)
    void onClickd2() {
        SendMessage(getString(R.string.msg_d2));
    }

    @OnClick(R.id.Button_d3)
    void onClickd3() {
        SendMessage(getString(R.string.msg_d3));
    }

    @OnClick(R.id.Button_d4)
    void onClickd4() {
        SendMessage(getString(R.string.msg_d4));
    }

    @OnClick(R.id.Button_lock)
    void onClickST() {
        SendMessage(getString(R.string.msg_ST));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    private void SendMessage(String strSend) {
        final String strHost = editHost.getText().toString();
        final int port = Integer.parseInt(editPort.getText().toString());
        final String txtSend = strSend;
        Log.d(this.getLocalClassName(), txtSend);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket(strHost, port);
                    socket.setSoTimeout(3000);
                    socket.setSoLinger(true, 0);
                    OutputStream outputStream = socket.getOutputStream();
                    InputStream inputStream = socket.getInputStream();
                    outputStream.write(txtSend.getBytes());
                    byte[] buffer = new byte[255];
                    inputStream.read(buffer);
                    String recv = new String(buffer);
                    socket.close();
                    Message msg = new Message();
                    msg.what = 1;
                    Bundle data = new Bundle();
                    data.putString("msg", recv);
                    data.putString("conn", getString(R.string.static_str_conn));
                    msg.setData(data);
                    handler.sendMessage(msg);
                } catch (ConnectException e) {
                    Message msg = new Message();
                    msg.what = 2;
                    Bundle data = new Bundle();
                    data.putString("msg", getString(R.string.static_str_no_status));
                    data.putString("conn", getString(R.string.static_str_conn));
                    data.putString("err", "连接错误！");
                    msg.setData(data);
                    handler.sendMessage(msg);
                    Log.e(OldConsoleActivity.this.getLocalClassName(), e.getMessage());
                } catch (IOException e) {
                    Message msg = new Message();
                    msg.what = 2;
                    Bundle data = new Bundle();
                    data.putString("msg", getString(R.string.static_str_no_status));
                    data.putString("conn", getString(R.string.static_str_conn));
                    data.putString("err", "I/O 错误！");
                    msg.setData(data);
                    handler.sendMessage(msg);
                    Log.e(OldConsoleActivity.this.getLocalClassName(), e.getMessage());
                }
            }
        }).start();
    }
}
