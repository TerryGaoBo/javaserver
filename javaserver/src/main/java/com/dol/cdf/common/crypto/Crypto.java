/*
 * Playdom, Inc. (c) 2009 - 2010 All Rights Reserved
 */
package com.dol.cdf.common.crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Random;

import sun.misc.BASE64Encoder;

public class Crypto {
    // we could get by with hostname, currentTime , and Random
    // it would be nice to have the ip address of the caller
    // The implementation of Random is probably pretty good. Random() uses System.nanoTime()

    // TODO: this is inefficient, and still doesn't reach the required entropy.
    // Write a url to inject entropy and simplify this code.

    private static Random rand = new Random();

    private static byte[] getStartingValue() {
        StringBuffer dest = new StringBuffer();
        dest.append(rand.nextLong());
        dest.append(rand.nextLong());
        dest.append(new Date().getTime());
        dest.append(System.currentTimeMillis());
        dest.append("secrfet phrase 89jkbn This should be unguessable. Wibble friggle wobble frack");
        return dest.toString().getBytes();
    }
    
    static class CryptoLocalVars {
        MessageDigest digest;
        Random rand = new Random();
        int resetCount = -1;
        Guid lastRandomGuid = new Guid();
        
        CryptoLocalVars(){
            try {
                digest = MessageDigest.getInstance("SHA");
                digest.reset();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
    }

    public static ThreadLocal<CryptoLocalVars> messageDigestThreadLocal = new ThreadLocal<CryptoLocalVars>() {
        protected synchronized CryptoLocalVars initialValue() {
            CryptoLocalVars tmp = new CryptoLocalVars();
            return tmp;
        }
    };
    
    private static  byte [] lastRandomBytes = getStartingValue();
    private static  byte [] injected = getStartingValue();
    
    public static void inject( byte [] bytes ){
    	CryptoLocalVars locals = messageDigestThreadLocal.get();
        locals.digest.update(bytes);
        locals.digest.update(injected);
        byte[] key = locals.digest.digest();
        injected = key;
    }

    static public String SHA1_Base64(byte[] source) {
        CryptoLocalVars locals = messageDigestThreadLocal.get();
        //locals.digest.reset();
        locals.digest.update(source);
        byte[] key = locals.digest.digest();

        BASE64Encoder en = new BASE64Encoder();
        String res = en.encode(key);

        return res;
    }

    static public byte[] SHA1(byte[] source) {
        CryptoLocalVars locals = messageDigestThreadLocal.get();
        //locals.digest.reset();
        locals.digest.update(source);
        byte[] key = locals.digest.digest();

        return key;
    }

    // 160 bits, very slow yet still not 160 bits of entropy
    static public byte[] createSecretKeyBytes() {
        CryptoLocalVars locals = messageDigestThreadLocal.get();
        //locals.digest.reset();
        int time = (int) System.currentTimeMillis();
        locals.digest.update((byte) time);
        locals.digest.update((byte) (time >> 8));
        locals.digest.update((byte) (time >> 16));
        locals.digest.update((byte) (time >> 24));
        locals.digest.update((byte)rand.nextInt());
        locals.digest.update((byte)rand.nextInt());
        locals.digest.update((byte)rand.nextInt());
        locals.digest.update((byte)rand.nextInt());
        locals.digest.update((byte)rand.nextInt());
        locals.digest.update((byte)rand.nextInt());
        locals.digest.update((byte)rand.nextInt());
        locals.digest.update((byte)rand.nextInt());
        locals.digest.update(lastRandomBytes);
        locals.digest.update(injected);
        byte[] key = locals.digest.digest();
        lastRandomBytes = key;
        return key;
    }

    // a base 64 string of 160 bits
    static public String createSecretKeyString() {
        byte[] bytes = createSecretKeyBytes();
        BASE64Encoder en = new BASE64Encoder();
        String res = en.encode(bytes);
        return res;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {

            String test = createSecretKeyString();
            String test2 = createSecretKeyString();

            assert !test.equals(test2);
        }

        {
            String test = SHA1_Base64("a test string".getBytes());
            String test2 = SHA1_Base64("a test string".getBytes());
            assert test.equals(test2);
        }
    }
}
