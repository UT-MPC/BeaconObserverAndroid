package edu.utexas.utmpc.beaconobserver.utility;

public class Constant {
    public static final int OPERATION_SUCCEED = 0;
    public static final int OPERATION_FAIL = 1;
    public static final int REQUEST_ENABLE_BT = 1;

    public static final int BLEND_LAMBDA_MS = 4000;
    public static final int BEACON_VALID_DURATION_MS = BLEND_LAMBDA_MS * 2;

    public static final int SCAN_PERIOD_MS = 5000;
    public static final int SCAN_INTERVAL_MS = 10000;

    public static final int CONVERSION_FAIL = -11;

    public static final int CONTEXT_TYPE_SIZE = 32;

    public static final String UPDATE_INTENT_NAME = "UI_UPDATE";
    public static final String BEACON_LIST_INTENT = "beacon_list";
}