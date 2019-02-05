package edu.utexas.utmpc.beaconobserver.utility;

import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;

import java.sql.Timestamp;
import java.util.BitSet;

import static edu.utexas.utmpc.beaconobserver.utility.Constant.CONTEXT_TYPE_SIZE;
import static edu.utexas.utmpc.beaconobserver.utility.Constant.STACON_TASK_OFFSET;
import static edu.utexas.utmpc.beaconobserver.utility.Converter.bytesToBitSet;
import static edu.utexas.utmpc.beaconobserver.utility.Converter.bytesToFloat;
import static edu.utexas.utmpc.beaconobserver.utility.Converter.bytesToInt;

public class StaconBeacon implements Beacon {

    // these are the first four bytes expected in any BLEnd packet
    public static final byte[] beaconPrefix = {0x02, 0x01, 0x04, 0x1B, (byte) 0xFE, (byte) 0x8B};
    private static final int NODE_ID_OFFSET = 6;
    private static final int NODE_ID_LEN = 1;
    private static final int NODE_CAPABILITY_OFFSET = 7;
    private static final int NODE_CAPABILITY_LEN = 2;
    private static final int NODE_DESIRE_OFFSET = 9;
    private static final int NODE_DESIRE_LEN = 2;
    private static final int CONTEXT_TYPE_OFFSET = 11;
    private static final int CONTEXT_VALUE1_OFFSET = 12;
    private static final int CONTEXT_VALUE2_OFFSET = 16;
    private static final int CONTEXT_VALUE_LEN = 4;

    String displayName;
    String deviceAddress;
    byte[] beaconContent;
    BitSet capabilities;
    BitSet desires;
    ContextInformation contextInformation;
    Timestamp timestamp;


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
                            bytesToFloat(beaconContent, CONTEXT_VALUE1_OFFSET, CONTEXT_VALUE_LEN),
                            bytesToFloat(beaconContent, CONTEXT_VALUE2_OFFSET, CONTEXT_VALUE_LEN));
        }

        this.timestamp = new Timestamp(System.currentTimeMillis());
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

    public ContextInformation getContextInformation() {
        return contextInformation;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getCapabilityString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Device Capability: ");
        for (int i = 0; i < CONTEXT_TYPE_SIZE; ++i) {
            sb.append(i).append(':').append(capabilities.get(i)).append(',');
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
