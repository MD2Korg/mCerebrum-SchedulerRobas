package org.md2k.schedulerrobas;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.md2k.datakitapi.time.DateTime;
import org.md2k.mcerebrum.commons.permission.PermissionInfo;
import org.md2k.mcerebrum.core.access.appinfo.AppInfo;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class ActivityMain extends AppCompatActivity {
    private static final String TAG = ActivityMain.class.getSimpleName();
    Subscription subscription;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PermissionInfo permissionInfo = new PermissionInfo();
        permissionInfo.getPermissions(this, result -> {
            if (!result) {
                Toast.makeText(getApplicationContext(), "!PERMISSION DENIED !!! Could not continue...", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Intent intent = new Intent();
                    String packageName = getPackageName();
                    PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
                    if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                        intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                        intent.setData(Uri.parse("package:" + packageName));
                        startActivity(intent);
                    }
                }

                load();
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            // Show alert dialog to the user saying a separate permission is needed
            // Launch the settings activity if the user prefers
            Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(myIntent);
        }
    }

    private void load() {
        final Button buttonService = (Button) findViewById(R.id.button_app_status);
        buttonService.setOnClickListener(v -> {
            Intent intent = new Intent(ActivityMain.this, ServiceScheduler.class);


            if (AppInfo.isServiceRunning(this, ServiceScheduler.class.getName())) {
                stopService(intent);
            } else {
                startService(intent);
            }
        });
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onResume() {
        subscription = getObservableTime().subscribe();
        super.onResume();
    }

    public void onPause() {
        if(subscription!=null) subscription.unsubscribe();
        super.onPause();
    }
    private Observable<Boolean> getObservableTime(){
        return Observable.interval(1000, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).onBackpressureLatest()

                .map(aLong -> {

                    long time = AppInfo.serviceRunningTime(ActivityMain.this, ServiceScheduler.class.getName());
                    if (time < 0) {
                        ((TextView) findViewById(R.id.button_app_status)).setText("START");
                        findViewById(R.id.button_app_status).setBackground(ContextCompat.getDrawable(ActivityMain.this, R.drawable.button_status_off));

                    } else {

                        ((TextView) findViewById(R.id.button_app_status)).setText(DateTime.convertTimestampToTimeStr(time));
                        findViewById(R.id.button_app_status).setBackground(ContextCompat.getDrawable(ActivityMain.this, R.drawable.button_status_on));
                    }
                    return true;
                });

    }
}
