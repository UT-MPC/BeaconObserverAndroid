package edu.utexas.utmpc.beaconobserver;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import edu.utexas.utmpc.beaconobserver.utility.Beacon;
import edu.utexas.utmpc.beaconobserver.utility.ContextInformation;
import edu.utexas.utmpc.beaconobserver.utility.HungarianAlgorithm;
import edu.utexas.utmpc.beaconobserver.utility.Matching;
import edu.utexas.utmpc.beaconobserver.utility.StaconBeacon;

public class MatchingUnitTest {
    List<Beacon> beaconList;

    @Before
    public void generateTestData() {
        beaconList = new ArrayList<>();
        BitSet bs1 = new BitSet(ContextInformation.ContextType.values().length - 1);
        bs1.set(1);
        bs1.set(2);
        StaconBeacon sb1 = new StaconBeacon();
        sb1.setDisplayName("Node1");
        sb1.setCapabilities(bs1);
        beaconList.add(sb1);
        BitSet bs2 = new BitSet();
        bs2.set(1);
        bs2.set(5);
        StaconBeacon sb2 = new StaconBeacon();
        sb2.setDisplayName("Node2");
        sb2.setCapabilities(bs2);
        beaconList.add(sb2);
    }

    @Test
    public void matching_complex() {
        beaconList.clear();
        BitSet bs0 = new BitSet(ContextInformation.ContextType.values().length - 1);
        bs0.set(0);
        bs0.set(1);
        bs0.set(5);
        StaconBeacon sb0 = new StaconBeacon();
        sb0.setDisplayName("Node0");
        sb0.setCapabilities(bs0);
        beaconList.add(sb0);
        BitSet bs1 = new BitSet(ContextInformation.ContextType.values().length - 1);
        bs1.set(0);
        bs1.set(2);
        bs1.set(6);
        StaconBeacon sb1 = new StaconBeacon();
        sb1.setDisplayName("Node1");
        sb1.setCapabilities(bs1);
        beaconList.add(sb1);
        BitSet bs2 = new BitSet(ContextInformation.ContextType.values().length - 1);
        bs2.set(0);
        bs2.set(1);
        bs2.set(3);
        bs2.set(4);
        StaconBeacon sb2 = new StaconBeacon();
        sb2.setDisplayName("Node2");
        sb2.setCapabilities(bs2);
        beaconList.add(sb2);
        BitSet bs3 = new BitSet(ContextInformation.ContextType.values().length - 1);
        bs3.set(0);
        StaconBeacon sb3 = new StaconBeacon();
        sb3.setDisplayName("Node3");
        sb3.setCapabilities(bs3);
        beaconList.add(sb3);
        BitSet bs4 = new BitSet(ContextInformation.ContextType.values().length - 1);
        bs4.set(4);
        bs4.set(5);
        StaconBeacon sb4 = new StaconBeacon();
        sb4.setDisplayName("Node4");
        sb4.setCapabilities(bs4);
        beaconList.add(sb4);

        double[][] mat = Matching.convertToMatrix(beaconList);
        HungarianAlgorithm ha = new HungarianAlgorithm(mat);
        int[] tasks = ha.execute();
        for (int i = 0; i < beaconList.size(); ++i) {
            System.out.println(beaconList.get(i).getName() + " is assigned with " + tasks[i]);
        }
    }
}
