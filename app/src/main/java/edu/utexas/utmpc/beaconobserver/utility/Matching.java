package edu.utexas.utmpc.beaconobserver.utility;

import java.util.List;

public class Matching {
    public static int[][] convertToMatrix(List<Beacon> beaconList) {
        int m = beaconList.size();
        int n = ContextInformation.ContextType.values().length - 1;
        int[][] matrix = new int[m][n];
        for (int i = 0; i < m; ++i) {
            StaconBeacon sb = (StaconBeacon) beaconList.get(i);
            for (int j = 0; j < n; ++j) {
                matrix[i][j] = sb.getCapabilities().get(j) ? 1 : 999;
            }
        }
        return matrix;
    }

    public static String localSchedule(List<Beacon> beaconList) {
        Hungarian hungarian = new Hungarian(Matching.convertToMatrix(beaconList));
        StringBuilder sb = new StringBuilder();
        sb.append("Local Results:\n");
        int[] tasks = hungarian.getResult();
        for (int i = 0; i < beaconList.size(); ++i) {
            StaconBeacon node = (StaconBeacon) beaconList.get(i);
            if (node.getCapabilities().get(tasks[i])) {
                sb.append(node.getDisplayName()).append(":")
                        .append(ContextInformation.ContextType.values()[i].name());
                sb.append("\n");
            }
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }
}
