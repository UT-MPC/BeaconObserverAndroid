package edu.utexas.utmpc.beaconobserver.ui;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import edu.utexas.utmpc.beaconobserver.R;
import edu.utexas.utmpc.beaconobserver.utility.StaconBeacon;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.BeaconViewHolder> {

    static class BeaconViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView deviceName;
        TextView deviceCap;

        BeaconViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            deviceName = (TextView) itemView.findViewById(R.id.device_name);
            deviceCap = (TextView) itemView.findViewById(R.id.device_cap);
        }
    }

    List<StaconBeacon> beaconList;

    public RVAdapter(List<StaconBeacon> beaconList) {
        this.beaconList = beaconList;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public BeaconViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.beacon_row, viewGroup, false);
        BeaconViewHolder pvh = new BeaconViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(BeaconViewHolder beaconViewHolder, int i) {
        beaconViewHolder.deviceName.setText(beaconList.get(i).getName());
        beaconViewHolder.deviceCap.setText(beaconList.get(i).getCapabilityString());
    }

    @Override
    public int getItemCount() {
        return beaconList.size();
    }
}
