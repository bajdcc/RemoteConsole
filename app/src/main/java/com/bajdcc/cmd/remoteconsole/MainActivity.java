package com.bajdcc.cmd.remoteconsole;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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

import com.bajdcc.cmd.remoteconsole.entity.InfoEntity;
import com.bajdcc.cmd.remoteconsole.event.ApiEvent;
import com.bajdcc.cmd.remoteconsole.service.ApiService;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private Drawer result;
    private TextView txtStatus;
    private String host;

    private static String getIP() throws SocketException {
        for (Enumeration<NetworkInterface> en = NetworkInterface
                .getNetworkInterfaces(); en.hasMoreElements(); ) {
            NetworkInterface intf = en.nextElement();
            if (intf.getName().toLowerCase().equals("eth0") || intf.getName().toLowerCase().equals("wlan0")) {
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String ipaddress = inetAddress.getHostAddress();
                        if (!ipaddress.contains("::")) {
                            return ipaddress;
                        }
                    }
                }
            }
        }
        return "unknown";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Config
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String ip = sp.getString("general_ip", "192.168.1.100");
        String port = sp.getString("general_port", "80");
        host = String.format("%s:%s", ip, port);

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
                        EventBus.getDefault().post(new ApiEvent("http://" + host));
                        break;
                    case R.id.menu_update:
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse(String.format("http://%s/cmd/latest.php", host))));
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
                        new PrimaryDrawerItem().withIdentifier(2002).withName(R.string.drawer_item_console_new).withDescription(R.string.drawer_item_console_new_desc).withIcon(FontAwesome.Icon.faw_paper_plane),
                        new PrimaryDrawerItem().withIdentifier(2003).withName(R.string.drawer_item_console_mqtt).withDescription(R.string.drawer_item_console_mqtt_desc).withIcon(FontAwesome.Icon.faw_paper_plane))
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
                                    intent = new Intent(MainActivity.this, AboutActivity.class);
                                    break;
                                case 2001:
                                    intent = new Intent(MainActivity.this, OldConsoleActivity.class);
                                    break;
                                case 2002:
                                    intent = new Intent(MainActivity.this, NewConsoleActivity.class);
                                    break;
                                case 2003:
                                    intent = new Intent(MainActivity.this, PushingActivity.class);
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

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe
    public void testServer(ApiEvent event) {

        final String ENDPOINT = event.getHost();

        Log.d(this.getLocalClassName(), ENDPOINT);
        try {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ENDPOINT)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
            ApiService apiService = retrofit.create(ApiService.class);
            apiService.getInfo(getIP())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<InfoEntity>() {
                        private InfoEntity entity;

                        @Override
                        public void onCompleted() {
                            txtStatus.setText(entity.toString());
                        }

                        @Override
                        public void onError(Throwable e) {
                            txtStatus.setText(e.getMessage());
                            Log.e(MainActivity.this.getLocalClassName(), e.getMessage());
                        }

                        @Override
                        public void onNext(InfoEntity infoResponse) {
                            entity = infoResponse;
                        }
                    });
        } catch (Exception e) {
            Log.e(MainActivity.this.getLocalClassName(), e.getMessage());
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
