package edu.utexas.utmpc.beaconobserver.ui;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.utexas.utmpc.beaconobserver.R;
import edu.utexas.utmpc.beaconobserver.utility.Beacon;
import edu.utexas.utmpc.beaconobserver.utility.StaconBeacon;

public class BeaconViewAdapter extends RecyclerView.Adapter<BeaconViewAdapter.BeaconViewHolder> {

    private List<Beacon> beaconList;

    public BeaconViewAdapter() {
        beaconList = new ArrayList<>();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public BeaconViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.beacon_row, viewGroup, false);
        BeaconViewHolder pvh = new BeaconViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(BeaconViewHolder beaconViewHolder, int i) {
        StaconBeacon sb = (StaconBeacon) beaconList.get(i);
        beaconViewHolder.deviceName.setText(sb.getName());
        beaconViewHolder.deviceAddr.setText(sb.getDeviceAddress());
        beaconViewHolder.deviceCap.setText(sb.getCapabilityString());
        beaconViewHolder.contextInfo
                .setText((sb.getContextInformation() == null) ? "Idle" : sb.getContextInformation()
                        .toString());
    }

    @Override
    public int getItemCount() {
        return beaconList.size();
    }

    public List<Beacon> getBeaconList() {
        return beaconList;
    }

    public void setBeaconList(List<Beacon> beaconList) {
        this.beaconList = beaconList;
    }

    static class BeaconViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView deviceName;
        TextView deviceAddr;
        TextView deviceCap;
        TextView contextInfo;

        BeaconViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            deviceName = (TextView) itemView.findViewById(R.id.device_name);
            deviceAddr = (TextView) itemView.findViewById(R.id.device_addr);
            deviceCap = (TextView) itemView.findViewById(R.id.device_cap);
            contextInfo = (TextView) itemView.findViewById(R.id.context_info);
        }
    }
}
