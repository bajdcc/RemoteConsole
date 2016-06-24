package com.bajdcc.cmd.remoteconsole.utils;

import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Digest Algorithms
 */
public class Digest {
    public static String getMD5(String val) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            Log.e(Digest.class.getSimpleName(), e.getMessage());
            return val;
        }
        md5.update(val.getBytes());
        byte[] m = md5.digest();
        return getString(m);
    }

    private static String getString(byte[] hash) {
        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            int number = b & 0xFF;
            if (number < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(number));
        }
        return hex.toString();
    }
}
