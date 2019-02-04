package edu.utexas.utmpc.beaconobserver.utility;

public class Context {
    private int contextType;
    private float value1;
    private float value2;

    public Context(int contextType, float value1, float value2) {
        this.contextType = contextType;
        this.value1 = value1;
        this.value2 = value2;
    }

    @Override
    public String toString() {
        return "Context type: " + contextType + ", value: " + value1 + ", " + value2 + ".";
    }
}
