package edu.utexas.utmpc.beaconobserver.utility;

import android.graphics.Color;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

import edu.utexas.utmpc.beaconobserver.R;

import static edu.utexas.utmpc.beaconobserver.utility.Converter.bytesToFloat;
import static edu.utexas.utmpc.beaconobserver.utility.StaconBeacon.CONTEXT_VALUE1_OFFSET;
import static edu.utexas.utmpc.beaconobserver.utility.StaconBeacon.CONTEXT_VALUE2_OFFSET;
import static edu.utexas.utmpc.beaconobserver.utility.StaconBeacon.CONTEXT_VALUE_LEN;

public class ContextInformation {
    public static final char CELCIUS = '\u2103';

    public enum ContextType {
        Temperature,
        Humidity,
        Air_Pressure,
        Color,
        VoC,
        Noise,
        Location,
        IDLE,
    }

    public static final Map<ContextType, Integer> ContextIconMap;

    static {
        ContextIconMap = new HashMap<>();
        ContextIconMap.put(ContextType.Temperature, R.drawable.icon_temp);
        ContextIconMap.put(ContextType.Humidity, R.drawable.icon_humidity);
        ContextIconMap.put(ContextType.Air_Pressure, R.drawable.icon_air_pressure);
        ContextIconMap.put(ContextType.Color, R.drawable.icon_color);
        ContextIconMap.put(ContextType.VoC, R.drawable.icon_voc);
        ContextIconMap.put(ContextType.Noise, R.drawable.icon_noise);
        ContextIconMap.put(ContextType.Location, R.drawable.icon_location);
        ContextIconMap.put(ContextType.IDLE, R.drawable.icon_idle);
    }

    public static final Map<ContextType, Integer> ContextColorMap;

    static {
        ContextColorMap = new HashMap<>();
        ContextColorMap.put(ContextType.Temperature, Color.parseColor("#ffe135"));
        ContextColorMap.put(ContextType.Humidity, Color.parseColor("#80c197"));
        ContextColorMap.put(ContextType.Air_Pressure, Color.parseColor("#915c83"));
        ContextColorMap.put(ContextType.Color, Color.parseColor("#89cff0"));
        ContextColorMap.put(ContextType.VoC, Color.parseColor("#767676"));
        ContextColorMap.put(ContextType.Noise, Color.parseColor("#b63c87"));
        ContextColorMap.put(ContextType.Location, Color.parseColor("#246bce"));
        ContextColorMap.put(ContextType.IDLE, Color.parseColor("#848482"));
    }

    private ContextType contextType;
    private float value1;
    private float value2;

    public ContextInformation(ContextType contextType, float value1, float value2) {
        this.contextType = contextType;
        this.value1 = value1;
        this.value2 = value2;
    }

    public ContextInformation(ContextType contextType, byte[] raw_bytes) {
        this.contextType = contextType;
        switch (contextType) {
            case Temperature:
                int tempDec = raw_bytes[CONTEXT_VALUE1_OFFSET] & 0xFF;
                int tempInt = raw_bytes[CONTEXT_VALUE1_OFFSET + 1];
                value1 = tempInt + (tempDec / 100.0f);
                break;
            case Humidity:
                value1 = (int) raw_bytes[CONTEXT_VALUE1_OFFSET];
                break;
            case Air_Pressure:
                int preInt = raw_bytes[CONTEXT_VALUE1_OFFSET] & 0xFF;
                int preDec = raw_bytes[CONTEXT_VALUE1_OFFSET + 1];
                value1 = preInt + (preDec / 100.f);
                break;
            case Color:
                value1 =
                        (int) ((raw_bytes[CONTEXT_VALUE1_OFFSET] << 8) + raw_bytes[CONTEXT_VALUE1_OFFSET + 1]) << 16;
                value1 +=
                        (int) ((raw_bytes[CONTEXT_VALUE1_OFFSET + 2] << 8) + raw_bytes[CONTEXT_VALUE1_OFFSET + 3]);
                value2 =
                        (int) ((raw_bytes[CONTEXT_VALUE2_OFFSET] << 8) + raw_bytes[CONTEXT_VALUE2_OFFSET + 1]) << 16;
                value2 +=
                        (int) ((raw_bytes[CONTEXT_VALUE2_OFFSET + 2] << 8) + raw_bytes[CONTEXT_VALUE2_OFFSET + 3]); // clear
                break;
            case VoC:
                value1 =
                        (int) (raw_bytes[CONTEXT_VALUE1_OFFSET] + (raw_bytes[CONTEXT_VALUE1_OFFSET + 1] << 8));
                value2 =
                        (int) (raw_bytes[CONTEXT_VALUE2_OFFSET] + (raw_bytes[CONTEXT_VALUE2_OFFSET + 1] << 8));
                break;
            case Noise:
                value1 = bytesToFloat(raw_bytes, CONTEXT_VALUE1_OFFSET, CONTEXT_VALUE_LEN);
            case Location:
                value1 = bytesToFloat(raw_bytes, CONTEXT_VALUE1_OFFSET, CONTEXT_VALUE_LEN);
                value2 = bytesToFloat(raw_bytes, CONTEXT_VALUE2_OFFSET, CONTEXT_VALUE_LEN);
                break;
            default:
                value1 = bytesToFloat(raw_bytes, CONTEXT_VALUE1_OFFSET, CONTEXT_VALUE_LEN);
        }
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
        StringBuilder sb =
                new StringBuilder(
                        "(Shared Context) " + StringUtils.capitalize(contextType.name()) + ": ");
        switch (contextType) {
            case Temperature:
                sb.append(String.format("%.2f ", value1));
                sb.append(CELCIUS);
                break;
            case Humidity:
                sb.append(String.format("%.0f %%RH", value1));
                break;
            case Air_Pressure:
                sb.append(String.format("%.2f hPa", value1));
                break;
            case Color:
                int red = (int) (value1) >> 16;
                int green = (int) (value1) & 0xFFFF;
                int blue = (int) (value2) >> 16;
                int clear = (int) (value2) & 0xFFFF;
                sb.append(String.format("Red: %d, Green %d, Blue %d (clear: %d)", red, green, blue,
                        clear));
                break;
            case VoC:
                sb.append(String.format("CO2: %.0f (ppm), VoC: %.0f (ppb)", value1, value2));
                break;
            case Noise:
                sb.append(String.format("Sound level: %.3f ", value1));
                break;
            case Location:
                sb.append(String.format("Lat: %3.10f, Lon: %3.10f", value1, value2));
//                sb.append("Lat: " + value1 + ", Lon: " + value2);
                break;
            default:
                sb.append("raw value").append(value1);
        }

        return sb.toString();
    }
}
