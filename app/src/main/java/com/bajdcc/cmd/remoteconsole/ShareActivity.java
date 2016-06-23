package com.bajdcc.cmd.remoteconsole;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShareActivity extends AppCompatActivity {

    private LinearLayout layout;
    private LinearLayout.LayoutParams layoutParams;
    private boolean needToShowImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        // Config
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        needToShowImage = sp.getBoolean("general_image", false);

        layout = (LinearLayout) findViewById(R.id.shareView);
        layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 10, 0, 10);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        Log.d(this.getLocalClassName(), action);
        Log.d(this.getLocalClassName(), type);

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("text/")) {
                handleSendText(intent);
            } else if (type.startsWith("image/")) {
                handleSendImage(intent);
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendMultipleImages(intent);
            }
        } else {
            handleNone(intent);
        }
    }

    TextView createTextView(final String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setClickable(true);
        tv.setFocusable(true);
        tv.setGravity(Gravity.CENTER);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setPrimaryClip(ClipData.newPlainText("shared_text", text));
            }
        });
        return tv;
    }

    void handleSendText(Intent intent) {
        String sharedTitle = intent.getStringExtra(Intent.EXTRA_TITLE);
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        TextView tv;
        if (sharedText != null) {
            if (sharedTitle != null) {
                Log.d(this.getLocalClassName(), sharedTitle);
                tv = createTextView(sharedTitle);
                layout.addView(tv, layoutParams);
            }
            Log.d(this.getLocalClassName(), sharedText);
            tv = createTextView(sharedText);
            layout.addView(tv, layoutParams);
        } else {
            Uri textUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            if (textUri != null) {
                Log.d(this.getLocalClassName(), textUri.toString());
                try {
                    InputStream inputStream = this.getContentResolver().openInputStream(textUri);
                    tv = createTextView(inputStream2Byte(inputStream));
                    layout.addView(tv, layoutParams);
                } catch (Exception e) {
                    Log.e(ShareActivity.this.getLocalClassName(), e.getMessage());
                }
            }
        }
    }

    private String inputStream2Byte(InputStream inputStream) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return new String(bos.toByteArray(), "UTF-8");
    }

    void handleSendImage(Intent intent) {
        Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            Log.d(this.getLocalClassName(), imageUri.toString());
            TextView tv;
            tv = createTextView(imageUri.toString());
            layout.addView(tv, layoutParams);
            ImageView iv = new ImageView(this);
            iv.setImageURI(imageUri);
            layout.addView(iv, layoutParams);
        }
    }

    void handleSendMultipleImages(Intent intent) {
        final List<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (imageUris != null) {
            if (!needToShowImage) {
                ArrayList<HashMap<String, Object>> mapList = new ArrayList<>();
                for (Uri imageUri : imageUris) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("text", imageUri.toString());
                    mapList.add(map);
                }
                ListView listView = new ListView(this);
                listView.setPadding(20, 20, 20, 20);
                listView.setAdapter(new SimpleAdapter(this, mapList, R.layout.share_text_cell,
                        new String[]{
                                "text"
                        },
                        new int[]{
                                R.id.textView
                        }));
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            final int position, long id) {
                        Log.d(ShareActivity.this.getLocalClassName(), imageUris.get(position).toString());
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(imageUris.get(position), "image/*");
                        startActivity(intent);
                    }
                });
                setContentView(listView);
            } else {
                ArrayList<HashMap<String, Object>> mapList = new ArrayList<>();
                for (Uri imageUri : imageUris) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("uri", imageUri);
                    map.put("text", imageUri.toString());
                    mapList.add(map);
                }
                GridView gridView = new GridView(this);
                gridView.setPadding(20, 20, 20, 20);
                gridView.setGravity(Gravity.CENTER);
                gridView.setColumnWidth(250);
                gridView.setNumColumns(GridView.AUTO_FIT);
                gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
                gridView.setVerticalSpacing(20);
                gridView.setHorizontalSpacing(20);
                gridView.setAdapter(new SimpleAdapter(this, mapList, R.layout.share_image_cell,
                        new String[]{
                                "uri", "text"
                        },
                        new int[]{
                                R.id.imageView, R.id.textView
                        }));
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            final int position, long id) {
                        Log.d(ShareActivity.this.getLocalClassName(), imageUris.get(position).toString());
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(imageUris.get(position), "image/*");
                        startActivity(intent);
                    }
                });
                setContentView(gridView);
            }
        }
    }

    void handleNone(Intent intent) {
        String type = intent.getType();
        TextView tv = createTextView(type);
        layout.addView(tv, layoutParams);
    }
}