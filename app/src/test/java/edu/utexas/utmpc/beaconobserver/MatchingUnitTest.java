package edu.utexas.utmpc.beaconobserver;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import edu.utexas.utmpc.beaconobserver.utility.Beacon;
import edu.utexas.utmpc.beaconobserver.utility.ContextInformation;
import edu.utexas.utmpc.beaconobserver.utility.Hungarian;
import edu.utexas.utmpc.beaconobserver.utility.Matching;
import edu.utexas.utmpc.beaconobserver.utility.StaconBeacon;

import static org.junit.Assert.assertEquals;

public class MatchingUnitTest {
    List<Beacon> beaconList;
    @Before
    public void generateTestData() {
        beaconList = new ArrayList<>();
        BitSet bs1 = new BitSet(ContextInformation.ContextType.values().length - 1);
        bs1.set(0);
        bs1.set(2);
        StaconBeacon sb1 = new StaconBeacon();
        sb1.setDisplayName("Node1");
        sb1.setCapabilities(bs1);
        beaconList.add(sb1);
        BitSet bs2 = new BitSet();
        bs2.or(bs1);
        bs2.set(1);
        StaconBeacon sb2 = new StaconBeacon();
        sb2.setDisplayName("Node2");
        sb2.setCapabilities(bs2);
        beaconList.add(sb2);
        BitSet bs3 = new BitSet();
        bs3.set(0);
        StaconBeacon sb3 = new StaconBeacon();
        sb3.setDisplayName("Node3");
        sb3.setCapabilities(bs3);
        beaconList.add(sb3);
    }

    @Test
    public void conversion_isCorrect() {
        int[][] mat = Matching.convertToMatrix(beaconList);
        int[][] expected = {{1,999,1,999}, {1,1,1,999}, {1,999,999,999}};
        assertEquals(expected, mat);
    }

    @Test
    public void matching_isCorrect() {
        Hungarian hungarian = new Hungarian(Matching.convertToMatrix(beaconList));

        int[] tasks = hungarian.getResult();
        for (int i = 0; i < beaconList.size(); ++i) {
            System.out.println(beaconList.get(i).getName() + " is assigned with " + tasks[i]);
        }
    }

    @Test
    public void matching_withIdleNodes() {
        BitSet bs4 = new BitSet();
        bs4.set(1);
        bs4.set(0);
        StaconBeacon sb4 = new StaconBeacon();
        sb4.setDisplayName("Node4");
        sb4.setCapabilities(bs4);
        beaconList.add(sb4);

        Hungarian hungarian = new Hungarian(Matching.convertToMatrix(beaconList));

        int[] tasks = hungarian.getResult();
        for (int i = 0; i < beaconList.size(); ++i) {
            StaconBeacon sb = (StaconBeacon)beaconList.get(i);
            if (sb.getCapabilities().get(tasks[i])) {
                System.out.println(
                        "Node " + beaconList.get(i).getName() + " is assigned with " + tasks[i]);
            }
        }
    }
}
