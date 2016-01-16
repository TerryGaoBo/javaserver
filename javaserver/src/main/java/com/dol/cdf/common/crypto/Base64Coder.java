/*
 * Playdom, Inc. (c) 2009 - 2010 All Rights Reserved
 */
package com.dol.cdf.common.crypto;

/**
 * A Base64 Encoder/Decoder.
 * 
 * <p>
 * This class is used to encode and decode data in Base64 format as described in RFC 1521.
 * 
 * <p>
 * This is "Open Source" software and released under the <a href="http://www.gnu.org/licenses/lgpl.html">GNU/LGPL</a>
 * license.<br>
 * It is provided "as is" without warranty of any kind.<br>
 * Copyright 2003: Christian d'Heureuse, Inventec Informatik AG, Switzerland.<br>
 * Home page: <a href="http://www.source-code.biz">www.source-code.biz</a><br>
 * 
 * <p>
 * Version history:<br>
 * 2003-07-22 Christian d'Heureuse (chdh): Module created.<br>
 * 2005-08-11 chdh: Lincense changed from GPL to LGPL.<br>
 * 2006-11-21 chdh:<br>
 * &nbsp; Method encode(String) renamed to encodeString(String).<br>
 * &nbsp; Method decode(String) renamed to decodeString(String).<br>
 * &nbsp; New method encode(byte[],int) added.<br>
 * &nbsp; New method decode(String) added.<br>
 * 
 * 2007-5-16 modified to use byte array instead of string
 */

public class Base64Coder {

    // Mapping table from 6-bit nibbles to Base64 characters.
    public static byte[] map1 = new byte[64];
    static {
        int i = 0;
        for (byte c = 'A'; c <= 'Z'; c++)
            map1[i++] = c;
        for (byte c = 'a'; c <= 'z'; c++)
            map1[i++] = c;
        for (byte c = '0'; c <= '9'; c++)
            map1[i++] = c;
        map1[i++] = '+';//62
        map1[i++] = '/';// 63
    }

    // Mapping table from Base64 characters to 6-bit nibbles.
    public static byte[] map2 = new byte[128];
    static {
        for (int i = 0; i < map2.length; i++)
            map2[i] = -1;
        for (int i = 0; i < 64; i++)
            map2[map1[i]] = (byte) i;
        // atw allow space to be the same as + 
        map2[' '] = map2['+'];
    }

    /**
     * Encodes a string into Base64 format. No blanks or line breaks are inserted.
     * 
     * @param s
     *                a String to be encoded.
     * @return A String with the Base64 encoded data.
     */
    public static String encodeString(String s) {
        return new String(encode(s.getBytes()));
    }

    /**
     * Encodes a byte array into Base64 format. No blanks or line breaks are inserted.
     * 
     * @param in
     *                an array containing the data bytes to be encoded.
     * @return A character array with the Base64 encoded data.
     */
    public static byte[] encode(byte[] in) {
        return encode(in, 0, in.length);
    }

    /**
     * Encodes a byte array into Base64 format. No blanks or line breaks are inserted.
     * 
     * @param in
     *                an array containing the data bytes to be encoded.
     * @param iLen
     *                number of bytes to process in <code>in</code>.
     * @return A character array with the Base64 encoded data.
     */
    public static byte[] encode(byte[] in, int offset, int iLen) {
        int oDataLen = (iLen * 4 + 2) / 3; // output length without padding
        int oLen = ((iLen + 2) / 3) * 4; // output length including padding
        byte[] out = new byte[oLen];
        int ip = 0;
        int op = 0;
        while (ip < iLen) {
            int i0 = in[ip++] & 0xff;
            int i1 = ip < iLen ? in[ip++] & 0xff : 0;
            int i2 = ip < iLen ? in[ip++] & 0xff : 0;
            int o0 = i0 >>> 2;
            int o1 = ((i0 & 3) << 4) | (i1 >>> 4);
            int o2 = ((i1 & 0xf) << 2) | (i2 >>> 6);
            int o3 = i2 & 0x3F;
            out[op++] = map1[o0];
            out[op++] = map1[o1];
            out[op] = (op < oDataLen ? map1[o2] : (byte) '=');
            op++;
            out[op] = (op < oDataLen ? map1[o3] : (byte) '=');
            op++;
        }
        return out;
    }

    public static void encode(byte[] in, int offset, int iLen, byte[] out, int outOffset) {
        int oDataLen = outOffset + (iLen * 4 + 2) / 3; // output length without padding
        // int oLen = ((iLen + 2) / 3) * 4; // output length including padding
        // byte[] out = new byte[oLen];
        int ip = 0;
        int op = outOffset;
        while (ip < iLen) {
            int i0 = in[ip++] & 0xff;
            int i1 = ip < iLen ? in[ip++] & 0xff : 0;
            int i2 = ip < iLen ? in[ip++] & 0xff : 0;
            int o0 = i0 >>> 2;
            int o1 = ((i0 & 3) << 4) | (i1 >>> 4);
            int o2 = ((i1 & 0xf) << 2) | (i2 >>> 6);
            int o3 = i2 & 0x3F;
            out[op++] = map1[o0];
            out[op++] = map1[o1];
            out[op] = (op < oDataLen ? map1[o2] : (byte) '=');
            op++;
            out[op] = (op < oDataLen ? map1[o3] : (byte) '=');
            op++;
        }
        // return out;
    }

    /**
     * Decodes a string from Base64 format.
     * 
     * @param s
     *                a Base64 String to be decoded.
     * @return A String containing the decoded data.
     * @throws IllegalArgumentException
     *                 if the input is not valid Base64 encoded data.
     */
    public static String decodeString(String s) {
        return new String(decode(s));
    }

    /**
     * Decodes a byte array from Base64 format.
     * 
     * @param s
     *                a Base64 String to be decoded.
     * @return An array containing the decoded data bytes.
     * @throws IllegalArgumentException
     *                 if the input is not valid Base64 encoded data.
     */
    public static byte[] decode(String s) {
        return decode(s.getBytes(), 0, s.length());
    }

    /**
     * Decodes a byte array from Base64 format. No blanks or line breaks are allowed within the Base64 encoded
     * data.
     * 
     * @param in
     *                a character array containing the Base64 encoded data.
     * @return An array containing the decoded data bytes.
     * @throws IllegalArgumentException
     *                 if the input is not valid Base64 encoded data.
     */
    public static byte[] decode(byte[] in, int offset, int len) {
        int iLen = len;// in.length;
        if (iLen % 4 != 0)
            throw new IllegalArgumentException("Length of Base64 encoded input string is not a multiple of 4.");
        while (iLen > 0 && in[offset + iLen - 1] == '=')
            iLen--;
        int oLen = (iLen * 3) / 4;
        byte[] out = new byte[oLen];
        int ip = 0;
        int op = 0;
        while (ip < iLen) {
            int i0 = in[offset + ip++];
            int i1 = in[offset + ip++];
            int i2 = ip < iLen ? in[offset + ip++] : 'A';
            int i3 = ip < iLen ? in[offset + ip++] : 'A';
            if (i0 > 127 || i1 > 127 || i2 > 127 || i3 > 127)
                throw new IllegalArgumentException("Illegal character in Base64 encoded data.");
            int b0 = map2[i0];
            int b1 = map2[i1];
            int b2 = map2[i2];
            int b3 = map2[i3];
            if (b0 < 0 || b1 < 0 || b2 < 0 || b3 < 0)
                throw new IllegalArgumentException("Illegal character in Base64 encoded data.");
            int o0 = (b0 << 2) | (b1 >>> 4);
            int o1 = ((b1 & 0xf) << 4) | (b2 >>> 2);
            int o2 = ((b2 & 3) << 6) | b3;
            out[op++] = (byte) o0;
            if (op < oLen)
                out[op++] = (byte) o1;
            if (op < oLen)
                out[op++] = (byte) o2;
        }
        return out;
    }

    public static void decode(byte[] in, int inpos, int len, byte[] out, int outpos) {
        int iLen = len;// in.length;
        if (iLen % 4 != 0)
            throw new IllegalArgumentException("Length of Base64 encoded input string is not a multiple of 4.");
        while (iLen > 0 && in[inpos + iLen - 1] == '=')
            iLen--;
        int oLen = (iLen * 3) / 4;
        // byte[] out = new byte[oLen];
        int ip = 0;
        int op = outpos;
        while (ip < iLen) {
            int i0 = in[inpos + ip++];
            int i1 = in[inpos + ip++];
            int i2 = ip < iLen ? in[inpos + ip++] : 'A';
            int i3 = ip < iLen ? in[inpos + ip++] : 'A';
            if (i0 > 127 || i1 > 127 || i2 > 127 || i3 > 127)
                throw new IllegalArgumentException("Illegal character in Base64 encoded data.");
            int b0 = map2[i0];
            int b1 = map2[i1];
            int b2 = map2[i2];
            int b3 = map2[i3];
            if (b0 < 0 || b1 < 0 || b2 < 0 || b3 < 0)
                throw new IllegalArgumentException("Illegal character in Base64 encoded data.");
            int o0 = (b0 << 2) | (b1 >>> 4);
            int o1 = ((b1 & 0xf) << 4) | (b2 >>> 2);
            int o2 = ((b2 & 3) << 6) | b3;
            out[op++] = (byte) o0;
            if (op < oLen)
                out[op++] = (byte) o1;
            if (op < oLen)
                out[op++] = (byte) o2;
        }
        // return out;
    }

    // Dummy constructor.
    private Base64Coder() {
    }

} // end class Base64Coder
