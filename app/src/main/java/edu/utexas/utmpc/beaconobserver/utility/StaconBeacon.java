package edu.utexas.utmpc.beaconobserver.utility;

import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;

import java.sql.Timestamp;
import java.util.BitSet;

import static edu.utexas.utmpc.beaconobserver.utility.Constant.CONTEXT_TYPE_SIZE;
import static edu.utexas.utmpc.beaconobserver.utility.Constant.STACON_TASK_OFFSET;
import static edu.utexas.utmpc.beaconobserver.utility.Converter.bytesToBitSet;
import static edu.utexas.utmpc.beaconobserver.utility.Converter.bytesToInt;

public class StaconBeacon implements Beacon {

    // these are the first four bytes expected in any BLEnd packet
    public static final byte[] beaconPrefix = {0x02, 0x01, 0x04, 0x1B, (byte) 0xFE, (byte) 0x8B};
    static final int NODE_ID_OFFSET = 6;
    static final int NODE_ID_LEN = 1;
    static final int NODE_CAPABILITY_OFFSET = 7;
    static final int NODE_CAPABILITY_LEN = 2;
    static final int NODE_DESIRE_OFFSET = 9;
    static final int NODE_DESIRE_LEN = 2;
    static final int CONTEXT_TYPE_OFFSET = 11;
    static final int CONTEXT_VALUE1_OFFSET = 12;
    static final int CONTEXT_VALUE2_OFFSET = 16;
    static final int CONTEXT_VALUE_LEN = 4;
    static final int BATTERY_LEVEL_OFFSET = 20;

    String displayName;
    String deviceAddress;
    byte[] beaconContent;
    BitSet capabilities;
    BitSet desires;
    ContextInformation contextInformation;
    Timestamp timestamp;
    int batteryLevel;

    public StaconBeacon(ScanRecord scanRecord, String deviceAddress) {
        this.deviceAddress = deviceAddress;
        this.beaconContent = scanRecord.getBytes();
        this.displayName = getNameFromBeacon();
        this.capabilities =
                bytesToBitSet(beaconContent, NODE_CAPABILITY_OFFSET, NODE_CAPABILITY_LEN);
        this.desires = bytesToBitSet(beaconContent, NODE_DESIRE_OFFSET, NODE_DESIRE_LEN);
        int contextType = bytesToInt(beaconContent, CONTEXT_TYPE_OFFSET, 1);
//        Log.d("StaconBeacon","raw: " + bytesToHex(scanRecord.getBytes()));
        if (contextType >= STACON_TASK_OFFSET) {
            int realType = contextType - STACON_TASK_OFFSET;
            this.contextInformation =
                    new ContextInformation(ContextInformation.ContextType.values()[realType],
                            beaconContent);
        }

        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.batteryLevel = (int) scanRecord.getBytes()[BATTERY_LEVEL_OFFSET];
    }

    public StaconBeacon() {
    }

    @Override
    public String getDeviceAddress() {
        return deviceAddress;
    }

    @Override
    public String getName() {
        return displayName;
    }

    @Override
    public byte[] getBeacon() {
        return beaconContent;
    }

    @Override
    public String toString() {
        return displayName + " (" + timestamp + ") " + contextInformation;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public void setBeaconContent(byte[] beaconContent) {
        this.beaconContent = beaconContent;
    }

    public void setCapabilities(BitSet capabilities) {
        this.capabilities = capabilities;
    }

    public void setDesires(BitSet desires) {
        this.desires = desires;
    }

    public void setContextInformation(
            ContextInformation contextInformation) {
        this.contextInformation = contextInformation;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public ContextInformation getContextInformation() {
        return contextInformation;
    }

    public String getDisplayName() {
        return displayName;
    }

    public byte[] getBeaconContent() {
        return beaconContent;
    }

    public BitSet getCapabilities() {
        return capabilities;
    }

    public BitSet getDesires() {
        return desires;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(int batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getCapabilityString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Device Capability: ");
        for (int i = 0; i < CONTEXT_TYPE_SIZE; ++i) {
            if (capabilities.get(i)) {
                sb.append(ContextInformation.ContextType.values()[i].name()).append(',');
            }
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
            sb.append('.');
        }
        return sb.toString();
    }

    public static boolean verifyBeacon(ScanResult scanResult) {
        byte[] rawBeacon = scanResult.getScanRecord().getBytes();
        for (int i = 0; i < beaconPrefix.length; ++i) {
            if (rawBeacon[i] != beaconPrefix[i]) {
                return false;
            }
        }
        return true;
    }

    private String getNameFromBeacon() {
        return "Node" + bytesToInt(beaconContent, NODE_ID_OFFSET, NODE_ID_LEN);
    }

}
