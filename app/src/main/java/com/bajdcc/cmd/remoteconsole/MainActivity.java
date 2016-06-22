package com.bajdcc.cmd.remoteconsole;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    private Drawer result = null;
    private TextView txtStatus = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.ic_launcher);
        toolbar.setTitle(getResources().getString(R.string.app_name));
        toolbar.setSubtitle(getResources().getString(R.string.app_author));
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                /*if (item instanceof Nameable) {
                    Toast.makeText(MainActivity.this, ((Nameable) item).getName().getText(MainActivity.this), Toast.LENGTH_SHORT).show();
                }*/

                switch (item.getItemId()) {
                    case R.id.menu_exit:
                        System.exit(0);
                        break;
                    case R.id.menu_test:
                        testServer();
                        break;
                }

                return false;
            }
        });

        // Create the drawer
        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHeader(R.layout.drawer_header)
                .withHasStableIds(true)
                .withSavedInstance(savedInstanceState)
                .withDisplayBelowStatusBar(true)
                .withTranslucentStatusBar(true)
                .withDrawerLayout(R.layout.material_drawer_fits_not)
                .addDrawerItems(
                        new PrimaryDrawerItem().withIdentifier(1001).withName(R.string.drawer_item_home).withDescription(R.string.drawer_item_home_desc).withIcon(FontAwesome.Icon.faw_home),
                        new PrimaryDrawerItem().withIdentifier(1002).withName(R.string.drawer_item_settings).withDescription(R.string.drawer_item_settings_desc).withIcon(FontAwesome.Icon.faw_cog),
                        new PrimaryDrawerItem().withIdentifier(1003).withName(R.string.drawer_item_about).withDescription(R.string.drawer_item_about_desc).withIcon(FontAwesome.Icon.faw_user),
                        new SectionDrawerItem().withName(R.string.drawer_item_section_header),
                        new PrimaryDrawerItem().withIdentifier(2001).withName(R.string.drawer_item_console_old).withDescription(R.string.drawer_item_console_old_desc).withIcon(FontAwesome.Icon.faw_paper_plane),
                        new PrimaryDrawerItem().withIdentifier(2002).withName(R.string.drawer_item_console_new).withDescription(R.string.drawer_item_console_new_desc).withIcon(FontAwesome.Icon.faw_paper_plane)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        /*if (drawerItem instanceof Nameable) {
                            Toast.makeText(MainActivity.this, ((Nameable) drawerItem).getName().getText(MainActivity.this), Toast.LENGTH_SHORT).show();
                        }*/

                        if (drawerItem != null) {
                            Intent intent = null;
                            switch (drawerItem.getIdentifier()) {
                                case 1001:
                                    //intent = new Intent(MainActivity.this, MainActivity.class);
                                    break;
                                case 1002:
                                    intent = new Intent(MainActivity.this, SettingsActivity.class);
                                    break;
                                case 1003:
                                    Toast.makeText(getApplicationContext(), getString(R.string.about), Toast.LENGTH_SHORT).show();
                                    break;
                                case 2001:
                                    intent = new Intent(MainActivity.this, OldConsoleActivity.class);
                                    break;
                                case 2002:
                                    intent = new Intent(MainActivity.this, NewConsoleActivity.class);
                                    break;
                            }

                            if (intent != null) {
                                result.closeDrawer();
                                MainActivity.this.startActivity(intent);
                                return true;
                            }
                        }

                        return false;
                    }
                }).build();

        txtStatus = (TextView) findViewById(R.id.txtLabel);
    }

    private void testServer() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String ip = sp.getString("general_ip", "192.168.1.100");
        String port = sp.getString("general_port", "80");
        String address = String.format("http://%s:%s/cmd/update.php", ip, port);
        final String host = String.format("%s:%s", ip, port);

        Log.d(this.getLocalClassName(), address);
        try {
            AsyncHttpClient client = new AsyncHttpClient();
            client.setMaxRetriesAndTimeout(2, 1000);
            client.get(address, new AsyncHttpResponseHandler() {

                @Override
                public void onStart() {
                    txtStatus.setText(String.format(getString(R.string.text_test_start), host));
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                    txtStatus.setText(new String(response));
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                    if (errorResponse != null) {
                        txtStatus.setText(new String(errorResponse));
                    } else {
                        txtStatus.setText(e.getMessage());
                    }
                }

                @Override
                public void onRetry(int retryNo) {
                    txtStatus.setText(String.format(getString(R.string.text_test_retry), retryNo));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(getApplication());
        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = result.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }
}
