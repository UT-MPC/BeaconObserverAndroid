package edu.utexas.utmpc.beaconobserver.ui;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.utexas.utmpc.beaconobserver.R;
import edu.utexas.utmpc.beaconobserver.service.BTScanService;
import edu.utexas.utmpc.beaconobserver.utility.Beacon;
import edu.utexas.utmpc.beaconobserver.utility.ContextInformation;
import edu.utexas.utmpc.beaconobserver.utility.StaconBeacon;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.view.PieChartView;

import static edu.utexas.utmpc.beaconobserver.service.BTScanService.DISABLE_SCAN;
import static edu.utexas.utmpc.beaconobserver.service.BTScanService.ENABLE_SCAN;
import static edu.utexas.utmpc.beaconobserver.utility.Constant.BEACON_LIST_INTENT;
import static edu.utexas.utmpc.beaconobserver.utility.Constant.CONTEXT_TYPE_SIZE;
import static edu.utexas.utmpc.beaconobserver.utility.Constant.REQUEST_ENABLE_BT;
import static edu.utexas.utmpc.beaconobserver.utility.Constant.UPDATE_INTENT_NAME;
import static edu.utexas.utmpc.beaconobserver.utility.ContextInformation.ContextColorMap;
import static edu.utexas.utmpc.beaconobserver.utility.Matching.localSchedule;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    /* Variables for the bound service */
    private BTScanService mScanService;
    boolean mBound = false;
    BluetoothAdapter mBTAdapter;

    /* UI components */
    private Switch mScanSwitch;
    private RecyclerView mRecyclerView;
    private BeaconViewAdapter mRecyclerViewAdapter;
    private PieChartView mPieChartView;
    private PieChartData mPieChartData;
    private LineChartView mLineChartView;
    private TextView mResultTextView;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        setContentView(R.layout.activity_main);
        mScanSwitch = findViewById(R.id.scan_switch);
        mScanSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!mBound) {
                    return;
                }
                checkBTPermission();
                mScanService.scan(isChecked ? ENABLE_SCAN : DISABLE_SCAN);
            }
        });

        mRecyclerView = findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(llm);

        mPieChartView = findViewById(R.id.pie_chart);
        mPieChartData = new PieChartData(generatePieData(null)).setHasLabels(true);
        mPieChartData.setHasCenterCircle(true).setCenterText1("Nearby Capability")
                .setCenterText1FontSize(10).setCenterText1Color(Color.parseColor("#0097A7"));
        mPieChartView.setPieChartData(mPieChartData);

        mLineChartView = findViewById(R.id.context_model);
        generateContextModel();

        mResultTextView = findViewById(R.id.local_result);

        // Hook up the RV mRecyclerViewAdapter with the cache
        mRecyclerViewAdapter = new BeaconViewAdapter();
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        // Register broadcast receiver to pass the content updates to UI
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mBroadcastReceiver, new IntentFilter(UPDATE_INTENT_NAME));
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
        Log.d(TAG, "onDestroy");
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
        if (mBroadcastReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
            mBroadcastReceiver = null;
        }
        super.onDestroy();
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

    private List<SliceValue> generatePieData(List<Beacon> beaconList) {
        List<SliceValue> sliceData = new ArrayList<>();
        if (beaconList == null || beaconList.isEmpty()) {
            sliceData.add(new SliceValue(100, Color.GRAY).setLabel("None"));
            return sliceData;
        }
        int sum = 0;
        Map<ContextInformation.ContextType, Integer> resourceMap = new HashMap<>();
        for (Beacon staconBeacon : beaconList) {
            if (staconBeacon instanceof StaconBeacon) {
                for (int ctype = 0; ctype < CONTEXT_TYPE_SIZE; ++ctype) {
                    if (((StaconBeacon) staconBeacon).getCapabilities().get(ctype)) {
                        ContextInformation.ContextType contextType =
                                ContextInformation.ContextType.values()[ctype];
                        if (resourceMap.containsKey(contextType)) {
                            resourceMap.put(contextType, resourceMap.get(contextType) + 1);
                        } else {
                            resourceMap.put(contextType, 1);
                        }
                        ++sum;
                    }
                }
            }
        }
        for (ContextInformation.ContextType key : resourceMap.keySet()) {
            sliceData.add(new SliceValue((resourceMap.get(key) * 100) / sum,
                    ContextColorMap.get(key)).setLabel(key.name()));
        }
        return sliceData;
    }


    private void generateContextModel() {
        List<Line> lines = new ArrayList<>();
        List<PointValue> values = new ArrayList<>();
        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        int numCtx = ContextInformation.ContextType.values().length - 1;

        for (int i = 0; i < numCtx; ++i) {
            ContextInformation.ContextType contextType = ContextInformation.ContextType.values()[i];
            values.add(new PointValue(i, numCtx - i));
            axisValues.add(new AxisValue(i, contextType.name().toCharArray()));
        }
        Line line = new Line(values);
        line.setColor(Color.parseColor("#7d7793"));
        line.setCubic(true);
        line.setFilled(true);
        line.setHasLabelsOnlyForSelected(true);
        line.setHasLines(true);
        line.setHasPoints(true);
        lines.add(line);
        LineChartData data = new LineChartData(lines);

        Axis axisX = new Axis(axisValues);
        Axis axisY = new Axis().setHasLines(true);

        axisY.setName("Context Importance");

        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);

        data.setBaseValue(Float.NEGATIVE_INFINITY);
        mLineChartView.setLineChartData(data);
    }

    private void updateScheduleTextView(List<Beacon> beaconList) {
        if (beaconList == null || beaconList.isEmpty()) {
            mResultTextView.setText(R.string.result_schedule_default);
            return;
        }
        mResultTextView.setText(localSchedule(beaconList));
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<Beacon> beaconArrayList =
                    (ArrayList<Beacon>) intent.getSerializableExtra(BEACON_LIST_INTENT);
            // Update beacon status list.
            if (mRecyclerViewAdapter != null) {
                mRecyclerViewAdapter.setBeaconList(beaconArrayList);
                mRecyclerViewAdapter.notifyDataSetChanged();
            }
            // Update nearby cap. pie chart.
            if (mPieChartView != null) {
                mPieChartData.setValues(generatePieData(beaconArrayList));
                mPieChartView.setPieChartData(mPieChartData);
            }
            if (mResultTextView != null) {
                updateScheduleTextView(beaconArrayList);
            }
        }
    };

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
