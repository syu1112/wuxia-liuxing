package com.wuxia.liuxing.common;

import com.wuxia.liuxing.exception.RtException;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Codec {
    private static MessageDigest digest = newDigest();

    private static MessageDigest newDigest() {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RtException("digest instance, MD5", e);
        }
    }
    
    public static String encode(byte[] bytes) {
        return byte2Hex(digest.digest(bytes));
    }

    public static String encode(String origin) {
        return byte2Hex(digest.digest(origin.getBytes()));
    }

    public static String encode(String origin, String charset) {
        try {
            return byte2Hex(digest.digest(origin.getBytes(charset)));
        } catch (UnsupportedEncodingException e) {
            throw new RtException("md5, charset=" + charset, e);
        }
    }

    private static String byte2Hex(byte b[]) {
        StringBuffer sb = new StringBuffer(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            int v = b[i] & 255;
            if (v < 16)
                sb.append('0');
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toLowerCase();
    }

}
