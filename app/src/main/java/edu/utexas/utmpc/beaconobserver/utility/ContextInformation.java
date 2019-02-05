package edu.utexas.utmpc.beaconobserver.utility;

import org.apache.commons.lang3.StringUtils;

public class ContextInformation {
    public enum ContextType {
        TEMPERATURE,
        HUMIDITY,
        Air_PRESSURE,
        COLOR,
    }

    private ContextType contextType;
    private float value1;
    private float value2;

    public ContextInformation(ContextType contextType, float value1, float value2) {
        this.contextType = contextType;
        this.value1 = value1;
        this.value2 = value2;
    }

    @Override
    public String toString() {
        return "Shared Context: " + StringUtils
                .capitalize(contextType.name()) + ", value: " + value1 + ", " + value2 + ".";
    }


}
