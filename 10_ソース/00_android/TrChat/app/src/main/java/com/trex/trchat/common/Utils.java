package com.trex.trchat.common;

import java.nio.charset.Charset;

public class Utils {
    public static byte[] str2bytes(String in) {
        if (in == null || in.isEmpty()) {
            return null;
        }
        byte[] out = in.getBytes(Charset.defaultCharset());
        return out;
    }

    public static String bytes2str(byte[] in) {
        if (in == null || in.length == 0) {
            return "";
        }
        String out = new String(in, Charset.defaultCharset());
        return out;
    }

    public static byte[] int2bytes(int in) {
        byte[] out = {(byte) (in >> 8), (byte) (in & 0xff)};
        return out;
    }

    public static int bytes2int(byte[] in) {
        if (in == null || in.length == 0) {
            return -100;
        }
        int out = (in[0] << 8 | in[1]);
        return out;
    }
}
