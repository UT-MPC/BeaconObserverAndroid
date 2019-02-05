package edu.utexas.utmpc.beaconobserver.utility;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

import edu.utexas.utmpc.beaconobserver.R;

public class ContextInformation {
    public enum ContextType {
        TEMPERATURE,
        HUMIDITY,
        Air_PRESSURE,
        COLOR,
        IDLE,
    }

    public static final Map<ContextType, Integer> ContextIconMap;
    static{
        ContextIconMap = new HashMap<>();
        ContextIconMap.put(ContextType.TEMPERATURE, R.drawable.icon_temp);
        ContextIconMap.put(ContextType.HUMIDITY, R.drawable.icon_humidity);
        ContextIconMap.put(ContextType.Air_PRESSURE, R.drawable.icon_air_pressure);
        ContextIconMap.put(ContextType.COLOR, R.drawable.icon_color);
        ContextIconMap.put(ContextType.IDLE, R.drawable.icon_idle);
    }

    private ContextType contextType;
    private float value1;
    private float value2;

    public ContextInformation(ContextType contextType, float value1, float value2) {
        this.contextType = contextType;
        this.value1 = value1;
        this.value2 = value2;
    }

    public ContextType getContextType() {
        return contextType;
    }

    public void setContextType(
            ContextType contextType) {
        this.contextType = contextType;
    }

    public float getValue1() {
        return value1;
    }

    public void setValue1(float value1) {
        this.value1 = value1;
    }

    public float getValue2() {
        return value2;
    }

    public void setValue2(float value2) {
        this.value2 = value2;
    }

    @Override
    public String toString() {
        return "Shared Context: " + StringUtils
                .capitalize(contextType.name()) + ", value: " + value1 + ", " + value2 + ".";
    }
}
