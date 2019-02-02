package edu.utexas.utmpc.beaconobserver.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.Random;

import static edu.utexas.utmpc.beaconobserver.utility.Constant.OPERATION_FAIL;
import static edu.utexas.utmpc.beaconobserver.utility.Constant.OPERATION_SUCCEED;

public class BTScanService extends Service {
    private static final String TAG = "BTScanService";

    public static final int START_SCAN = 0;
    public static final int STOP_SCAN = 1;

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    // Random number generator
    private final Random mGenerator = new Random();

    private boolean serviceRunning = false;

    private Handler handler = new Handler();

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
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return mBinder;
    }

    public int scan(int command) {
        switch (command) {
            case START_SCAN:
                Log.d(TAG, "startBeaconScan");
                handler.post(dummyTask);
                serviceRunning = true;
                break;
            case STOP_SCAN:
                if (serviceRunning) {
                    Log.d(TAG, "stopBeaconScan");
                    handler.removeCallbacks(dummyTask);
                    serviceRunning = false;
                }
                break;
            default:
                Log.e(TAG, "receives unknown command (" + command + ").");
                return OPERATION_FAIL;
        }
        return OPERATION_SUCCEED;
    }

    public boolean isServiceRunning() {
        return serviceRunning;
    }

    private Runnable dummyTask = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "Dummy task: " + mGenerator.nextInt(100));
            // Repeat this the same runnable code block again another 2 seconds
            // 'this' is referencing the Runnable object
            handler.postDelayed(this, 2000);
        }
    };


}
