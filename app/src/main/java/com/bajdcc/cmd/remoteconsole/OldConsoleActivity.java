package com.bajdcc.cmd.remoteconsole;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
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

public class OldConsoleActivity extends AppCompatActivity {

    private EditText editHost = null;
    private EditText editPort = null;
    private Button btnExit = null;
    private Button btnConn = null;
    private Button btnInput = null;
    private TextView txvConn = null;
    private EditText txvMsg = null;
    private EditText txvInput = null;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            switch (msg.what) {
                case 1:
                    txvConn.setText(data.getString("conn"));
                    txvMsg.setText(data.getString("msg"));
                case 2:
                    txvConn.setText(data.getString("conn"));
                    txvMsg.setText(data.getString("msg"));
                    Toast.makeText(getApplicationContext(),
                            data.getString("err"), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_console);

        editHost = (EditText) findViewById(R.id.edit_IP);
        editPort = (EditText) findViewById(R.id.edit_PORT);
        btnExit = (Button) findViewById(R.id.button_exit);
        btnConn = (Button) findViewById(R.id.button_conn);
        btnInput = (Button) findViewById(R.id.button_input);
        txvConn = (TextView) findViewById(R.id.textView_status);
        txvMsg = (EditText) findViewById(R.id.editText_msg);
        txvInput = (EditText) findViewById(R.id.editText_input);

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

        findViewById(R.id.Button00).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SendMessage(getString(R.string.msg_00));
            }
        });

        findViewById(R.id.Button01).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SendMessage(getString(R.string.msg_01));
            }
        });

        findViewById(R.id.Button02).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SendMessage(getString(R.string.msg_02));
            }
        });

        findViewById(R.id.Button03).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SendMessage(getString(R.string.msg_03));
            }
        });

        findViewById(R.id.Button04).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SendMessage(getString(R.string.msg_04));
            }
        });

        findViewById(R.id.Button05).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SendMessage(getString(R.string.msg_05));
            }
        });

        findViewById(R.id.Button06).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SendMessage(getString(R.string.msg_06));
            }
        });

        findViewById(R.id.Button07).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SendMessage(getString(R.string.msg_07));
            }
        });

        findViewById(R.id.ButtonF1).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SendMessage(getString(R.string.msg_F1));
            }
        });

        findViewById(R.id.ButtonF3).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SendMessage(getString(R.string.msg_F3));
            }
        });

        findViewById(R.id.ButtonF6).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SendMessage(getString(R.string.msg_F6));
            }
        });

        findViewById(R.id.Button_d1).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SendMessage(getString(R.string.msg_d1));
            }
        });

        findViewById(R.id.Button_d2).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SendMessage(getString(R.string.msg_d2));
            }
        });

        findViewById(R.id.Button_d3).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SendMessage(getString(R.string.msg_d3));
            }
        });

        findViewById(R.id.Button_d4).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SendMessage(getString(R.string.msg_d4));
            }
        });

        findViewById(R.id.Button_lock).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SendMessage(getString(R.string.msg_ST));
            }
        });
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
                    e.printStackTrace();
                } catch (IOException e) {
                    Message msg = new Message();
                    msg.what = 2;
                    Bundle data = new Bundle();
                    data.putString("msg", getString(R.string.static_str_no_status));
                    data.putString("conn", getString(R.string.static_str_conn));
                    data.putString("err", "I/O 错误！");
                    msg.setData(data);
                    handler.sendMessage(msg);
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
