package edu.utexas.utmpc.beaconobserver.utility;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanRecord;

public interface Beacon {
    String getDeviceAddress();
    String getName();
    byte[] getBeacon();
}
