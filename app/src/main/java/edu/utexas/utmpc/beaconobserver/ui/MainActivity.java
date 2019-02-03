package edu.utexas.utmpc.beaconobserver.ui;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;

import edu.utexas.utmpc.beaconobserver.R;
import edu.utexas.utmpc.beaconobserver.service.BTScanService;

import static edu.utexas.utmpc.beaconobserver.service.BTScanService.ENABLE_SCAN;
import static edu.utexas.utmpc.beaconobserver.service.BTScanService.DISABLE_SCAN;
import static edu.utexas.utmpc.beaconobserver.utility.Constant.REQUEST_ENABLE_BT;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    /* Variables for the bound service */
    private BTScanService mScanService;
    boolean mBound = false;
    BluetoothAdapter mBTAdapter;

    /* UI components */
    private Switch mScanSwitch;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        setContentView(R.layout.activity_main);
        mScanSwitch = (Switch) findViewById(R.id.scan_switch);
        mScanSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!mBound) {
                    return;
                }
                checkBTPermission();
                mScanService.scan(isChecked ? ENABLE_SCAN : DISABLE_SCAN);
            }
        });
    }

    @Override protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        if (!mBound) {
            // Bind to the service
            bindService(new Intent(this, BTScanService.class), mConnection,
                    Context.BIND_AUTO_CREATE);
        }
    }

    @Override protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    private void checkBTPermission() {
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBTAdapter == null) {
            throw new RuntimeException("Device does not support Bluetooth.");
        }
        if (!mBTAdapter.isEnabled() || ContextCompat.checkSelfPermission(getBaseContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            Log.d(TAG, "Requesting permission.");
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ENABLE_BT);
        }
    }

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override public void onServiceConnected(ComponentName className, IBinder service) {
            BTScanService.LocalBinder binder = (BTScanService.LocalBinder) service;
            mScanService = binder.getService();
            mBound = true;
        }

        @Override public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
}
