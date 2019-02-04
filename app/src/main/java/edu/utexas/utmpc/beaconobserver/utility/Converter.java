package edu.utexas.utmpc.beaconobserver.utility;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;

import static edu.utexas.utmpc.beaconobserver.utility.Constant.CONTEXT_TYPE_SIZE;
import static edu.utexas.utmpc.beaconobserver.utility.Constant.CONVERSION_FAIL;

public class Converter {
    private final static char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public static int bytesToInt(byte[] frame, int offset, int length) {
        if (offset + length >= frame.length) {
            return CONVERSION_FAIL;
        }
        if (length < 4) {
            int ret = 0;
            for (int i = offset; i < offset + length; ++i) {
                ret <<= 8;
                ret |= frame[i];
            }
            return ret;
        }
        return ByteBuffer.wrap(frame, offset, length).getInt();
    }

    public static float bytesToFloat(byte[] frame, int offset, int length) {
        if (length != 4 || offset + length >= frame.length) {
            return CONVERSION_FAIL;
        }
        return ByteBuffer.wrap(frame, offset, length).getFloat();
    }

    public static BitSet bytesToBitSet(byte[] frame, int offset, int length) {
        return bytesToBitSet(frame, offset, length, CONTEXT_TYPE_SIZE);
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static BitSet bytesToBitSet(byte[] frame, int offset, int length, int bitSetSize) {
        BitSet bs = new BitSet(bitSetSize);

        byte[] val = new byte[length];
        for (int i = 0; i < length; ++i) {
            val[i] = frame[offset + i];
        }
        return BitSet.valueOf(val);
    }
}
