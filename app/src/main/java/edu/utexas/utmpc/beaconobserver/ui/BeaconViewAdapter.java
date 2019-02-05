package edu.utexas.utmpc.beaconobserver.ui;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.utexas.utmpc.beaconobserver.R;
import edu.utexas.utmpc.beaconobserver.utility.Beacon;
import edu.utexas.utmpc.beaconobserver.utility.ContextInformation;
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
        beaconViewHolder.deviceNameTextView.setText(sb.getName());
        beaconViewHolder.deviceAddrTextView.setText(sb.getDeviceAddress());
        beaconViewHolder.deviceCapTextView.setText(sb.getCapabilityString());
        ContextInformation contextInformation = sb.getContextInformation();
        if (contextInformation == null) {
            beaconViewHolder.contextInfoTextView.setText("Idle");
            beaconViewHolder.iconImageView.setImageResource(
                    ContextInformation.ContextIconMap.get(ContextInformation.ContextType.IDLE));
        } else {
            beaconViewHolder.contextInfoTextView.setText(contextInformation.toString());
            beaconViewHolder.iconImageView.setImageResource(
                    ContextInformation.ContextIconMap.get(contextInformation.getContextType()));
        }
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
        CardView cardView;
        TextView deviceNameTextView;
        TextView deviceAddrTextView;
        TextView deviceCapTextView;
        TextView contextInfoTextView;
        ImageView iconImageView;

        BeaconViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cv);
            deviceNameTextView = itemView.findViewById(R.id.device_name);
            deviceAddrTextView = itemView.findViewById(R.id.device_addr);
            deviceCapTextView = itemView.findViewById(R.id.device_cap);
            contextInfoTextView = itemView.findViewById(R.id.context_info);
            iconImageView = itemView.findViewById(R.id.context_icon);
        }
    }
}
