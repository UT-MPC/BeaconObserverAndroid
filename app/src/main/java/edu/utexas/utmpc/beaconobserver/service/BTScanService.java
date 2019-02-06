package edu.utexas.utmpc.beaconobserver.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import edu.utexas.utmpc.beaconobserver.utility.Beacon;
import edu.utexas.utmpc.beaconobserver.utility.BeaconCache;
import edu.utexas.utmpc.beaconobserver.utility.StaconBeacon;

import static edu.utexas.utmpc.beaconobserver.utility.Constant.BEACON_LIST_INTENT;
import static edu.utexas.utmpc.beaconobserver.utility.Constant.OPERATION_FAIL;
import static edu.utexas.utmpc.beaconobserver.utility.Constant.OPERATION_SUCCEED;
import static edu.utexas.utmpc.beaconobserver.utility.Constant.SCAN_INTERVAL_MS;
import static edu.utexas.utmpc.beaconobserver.utility.Constant.SCAN_PERIOD_MS;
import static edu.utexas.utmpc.beaconobserver.utility.Constant.UPDATE_INTENT_NAME;
import static edu.utexas.utmpc.beaconobserver.utility.StaconBeacon.verifyBeacon;

public class BTScanService extends Service {
    private static final String TAG = "BTScanService";

    public static final int ENABLE_SCAN = 0;
    public static final int DISABLE_SCAN = 1;

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    // Random number generator
    private final Random mGenerator = new Random();

    private boolean mServiceEnabled = false;

    private boolean mScanning = false;

    private Handler mHandler = new Handler();

    private BluetoothAdapter mBTAdapter;

    private BluetoothLeScanner mBTLeScanner;

    private List<ScanFilter> mScanFilters;

    private ScanSettings mScanSettings;

    private BeaconCache cache;

    private ScanCallback mScanCallback;

    private List<Beacon> prevResult; // For updating the UI

    private BeaconNameComparator beaconNameComparator;

    private LocalBroadcastManager mLocalBroadcastManager;

    public class LocalBinder extends Binder {
        public BTScanService getService() {
            // Return this instance of BTScanService so clients can call public methods
            return BTScanService.this;
        }
    }

    public BTScanService() {
        super();
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
        ScanSettings.Builder scanSettingsBuilder = new ScanSettings.Builder();
        scanSettingsBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);    // scan mode
        this.mScanSettings = scanSettingsBuilder.build();
        this.mScanFilters = new ArrayList<>();
        this.beaconNameComparator = new BeaconNameComparator();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        cache = BeaconCache.getInstance();
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        return mBinder;
    }

    public int scan(int command) {
        switch (command) {
            case ENABLE_SCAN:
                Log.d(TAG, "Enable Scan");
                startScan();
                mServiceEnabled = true;
                break;
            case DISABLE_SCAN:
                if (mServiceEnabled) {
                    Log.d(TAG, "Disable Scan");
                    mServiceEnabled = false;
                    stopScan();
                }
                break;
            default:
                Log.e(TAG, "receives unknown command (" + command + ").");
                return OPERATION_FAIL;
        }
        return OPERATION_SUCCEED;
    }

    private int startScan() {
        Log.d(TAG, "Start scanning.");
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        mScanCallback = new BTScanCallback();
        mBTLeScanner = mBTAdapter.getBluetoothLeScanner();
        mBTLeScanner.startScan(mScanFilters, mScanSettings, mScanCallback);
        mScanning = true;
        mHandler.postDelayed(this::stopScan, SCAN_PERIOD_MS);
        return OPERATION_SUCCEED;
    }

    private int stopScan() {
        Log.d(TAG, "Stop scanning.");
        if (mScanning && mBTAdapter != null && mBTAdapter.isEnabled() && mBTLeScanner != null) {
            mBTLeScanner.stopScan(mScanCallback);
            finishScan();
        }
        mScanCallback = null;
        mScanning = false;
        if (mServiceEnabled) {
            mHandler.postDelayed(this::startScan, SCAN_INTERVAL_MS - SCAN_PERIOD_MS);
        }
        return OPERATION_SUCCEED;
    }

    private void finishScan() {
        List<Beacon> curResult = new ArrayList<>(cache.values());
        if (prevResult != null && prevResult.equals(curResult)) {
            return;
        }
        prevResult = curResult;
        ArrayList<Beacon> beaconList = cache.isEmpty() ? new ArrayList<>() : prevResult.stream()
                .sorted(beaconNameComparator).collect(
                        Collectors.toCollection(ArrayList::new));

//        Log.d(TAG, "Beacon list changed: cache size = " + cache.size());
        Intent updateIntent = new Intent(UPDATE_INTENT_NAME);
        updateIntent.putExtra(BEACON_LIST_INTENT, beaconList);
        mLocalBroadcastManager.sendBroadcast(updateIntent);

    }

    private class BTScanCallback extends ScanCallback {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            addScanResult(result);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult result : results) {
                addScanResult(result);
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e(TAG, "BLE Scan Failed with code " + errorCode);
        }

        private void addScanResult(ScanResult result) {
            if (verifyBeacon(result)) {
                StaconBeacon sBcn =
                        new StaconBeacon(result.getScanRecord(), result.getDevice().getAddress());
                cache.put(result.getDevice().getAddress(), sBcn);
            }
        }
    }

    class BeaconNameComparator implements Comparator<Beacon> {
        public int compare(Beacon b1, Beacon b2) {
            return b1.getName().compareTo(b2.getName());
        }
    }
}
