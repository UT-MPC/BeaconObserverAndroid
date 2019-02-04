package edu.utexas.utmpc.beaconobserver.utility;

public class ContextInformation {
    private int contextType;
    private float value1;
    private float value2;

    public ContextInformation(int contextType, float value1, float value2) {
        this.contextType = contextType;
        this.value1 = value1;
        this.value2 = value2;
    }

    @Override
    public String toString() {
        return "ContextInformation type: " + contextType + ", value: " + value1 + ", " + value2 + ".";
    }
}
