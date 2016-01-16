/*
 * Playdom, Inc. (c) 2009 - 2010 All Rights Reserved
 */
package com.dol.cdf.common.crypto;

import java.io.Serializable;
import java.util.Random;

import org.apache.log4j.Logger;

/**
 * An object with 160 random bits.
 * 
 */

public final class Guid implements Comparable<Object>, Serializable {
	private static final long serialVersionUID = -43982979016022367L;
	public static Logger logger = Logger.getLogger(Guid.class);

	// 160 bits, long1 has the msb
	private long long1;
	private long long2;
	private int int1;

	private static final boolean tests = false;

	public Guid() {
	}

	public Guid(Random rand)// for testing
	{
		long1 = rand.nextLong();
		long2 = rand.nextLong();
		int1 = (int) rand.nextLong();
	}

	public Guid(Guid cloneMe) {
		long1 = cloneMe.long1;
		long2 = cloneMe.long2;
		int1 = cloneMe.int1;
	}

	// this is really just for an example, and for debugging
	// add trailing A's as necessary. It's SLOW
	public Guid(String in) {
		initFromString_dontUseThis(in);
	}

	@Override
	public final int hashCode() {
		return (int) (long1 >> 32);// big endian
	}

	public final int upper32() {
		return (int) (long1 >>> 32);
	}

	public final long upper64() {
		return long1;
	}

	/**
	 * hashCodeAux. A hash code with the same contract as hashCode except that it is completely orthogonal to hashCode.
	 * 
	 * @return
	 */

	public final int hashCode2() {
		return (int) (long2);
	}

	public final int hashCodeAux() {
		return (int) (long2);
	}

	public final int secondaryHash() {
		return (int) (long2);
	}

	// this is for forcing guids into known buckets
	// if the mask is an even power of two then it's
	// likely to destroy the desired properties of
	// hashCode()
	// this has to complement BucketMgr.getIndex()
	public void maskAndSet(int mask, int set) {
		int tmp = (int) (long1 >> 32);
		tmp = tmp / mask;
		tmp = (tmp * mask) + set;
		long1 = (long1 & 0xFFFFFFFFL) | (((long) tmp) << 32);
	}

	/**
	 * Returns long made up of bit at offset 64 through bit 127 (out of 160 bits).
	 */
 
	public long getSecondLong() {
		return long2;
	}
    
        public long getFirstLong() {
            return long1;
        }
    
	static double _32Bits = (double)(1L<<32);
 
	static double _64Bits = _32Bits * _32Bits;
	static double oo64Bits = 1.0 / _64Bits;

	public double asFraction() {
		double got = 0;
		got = (this.long1 >>> 32);// 32 bits
		got *= _32Bits;
		got += (this.long1) & 0x00000000FFFFFFFFL;
		got *= oo64Bits;
		return got;
	}

	/**
	 * Generate a fairly well hashed supposedly unique guid from this guid using the index supplied We only plan to use
	 * this up to an i of 4096 and level of 5
	 * 
	 * @param i
	 * @return
	 */
	public Guid getSequence(int i, int level) {
		Guid nn = new Guid();
		nn.int1 = int1 + level * 1103515245;
		nn.long2 = long2;
		nn.long1 = long1 + i * ((0x5DEECE66DL << 30));
		return nn;
	}

	/**
	 * At level 0 we merely wish sort a set of guids into one of 4096 buckets for sorting purposes. To do this we return
	 * the top 12 bits. Inside of the one of the level 0 buckets we want to do it again but they all have the same top
	 * 12 bits so we return the next 12 bits.
	 * 
	 * @param level
	 * @return 0xFFF & (int) (long1 >> (64 - 12 * level))
	 */

	public int get4kBucket(int level) {
		// for level 0 use the top 12 bits of guid
		// for level 1 the next 12 bits, etc
		if (level == 0) {
			return 0xFFF & (int) (long1 >> (64 - 12));
		} else {
			return 0xFFF & (int) (long1 >> (64 - 12 * level));
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		Guid guid = (Guid) o;
		if (guid.int1 != this.int1)
			return false;
		if (guid.long1 != this.long1)
			return false;
		if (guid.long2 != this.long2)
			return false;
		return true;
	}

	// I would prefer an unsigned ordering
	// is that possible without breaking everything ??
	// @Override
	public int XXcompareTo(Object o) {
		Guid guid = (Guid) o;
		long tmp = this.long1 - guid.long1;
		if (tmp != 0)
			return tmp > 0 ? 1 : -1;
		tmp = this.long2 - guid.long2;
		if (tmp != 0)
			return tmp > 0 ? 1 : -1;
		return this.int1 - guid.int1;
	}

	// this one should be ordered correctly

	@Override
	public int compareTo(Object o) {
		Guid guid = (Guid) o;

		// System.out.println(Long.toHexString(this.long1) + " vs " + Long.toHexString(guid.long1));
		long tmp1, tmp2;

		tmp1 = (this.long1 >>> 32);
		tmp2 = (guid.long1 >>> 32);
		if (tmp1 > tmp2)
			return 1;
		if (tmp1 != tmp2)
			return -1;

		tmp1 = (this.long1) & 0x00000000FFFFFFFFL;
		tmp2 = (guid.long1) & 0x00000000FFFFFFFFL;
		if (tmp1 > tmp2)
			return 1;
		if (tmp1 != tmp2)
			return -1;

		tmp1 = (this.long2 >>> 32);
		tmp2 = (guid.long2 >>> 32);
		if (tmp1 > tmp2)
			return 1;
		if (tmp1 != tmp2)
			return -1;

		tmp1 = (this.long2) & 0x00000000FFFFFFFFL;
		tmp2 = (guid.long2) & 0x00000000FFFFFFFFL;
		if (tmp1 > tmp2)
			return 1;
		if (tmp1 != tmp2)
			return -1;

		tmp1 = ((long) this.int1) & 0x00000000FFFFFFFFL;
		tmp2 = ((long) guid.int1) & 0x00000000FFFFFFFFL;
		if (tmp1 > tmp2)
			return 1;
		if (tmp1 != tmp2)
			return -1;

		return 0;
	}

	public void initFromString_dontUseThis(String in)// not for production
	{
		byte[] base64bytes = in.getBytes();
		if (base64bytes.length < 28) {// happens every time
			byte[] newb = new byte[28];
			System.arraycopy(base64bytes, 0, newb, 0, base64bytes.length);
			for (int i = base64bytes.length; i < 28; i++)
				newb[i] = (byte) 'A';
			base64bytes = newb;
		}
		byte[] bytes = Base64Coder.decode(base64bytes, 0, 28);
		setFrom20bytes(bytes, 0);
	}

	// it's make a Guid from 20 bytes, 160 bits, NOT read Base64
	public void setFrom20bytes(byte[] bytes, int offset) {
		// big endian
		long1 = ((int) bytes[offset++]);
		long1 <<= 8;
		long1 |= ((int) bytes[offset++]) & 0xFF;
		long1 <<= 8;
		long1 |= ((int) bytes[offset++]) & 0xFF;
		long1 <<= 8;
		long1 |= ((int) bytes[offset++]) & 0xFF;
		long1 <<= 8;
		long1 |= ((int) bytes[offset++]) & 0xFF;
		long1 <<= 8;
		long1 |= ((int) bytes[offset++]) & 0xFF;
		long1 <<= 8;
		long1 |= ((int) bytes[offset++]) & 0xFF;
		long1 <<= 8;
		long1 |= ((int) bytes[offset++]) & 0xFF;

		long2 = ((int) bytes[offset++]);
		long2 <<= 8;
		long2 |= ((int) bytes[offset++]) & 0xFF;
		long2 <<= 8;
		long2 |= ((int) bytes[offset++]) & 0xFF;
		long2 <<= 8;
		long2 |= ((int) bytes[offset++]) & 0xFF;
		long2 <<= 8;
		long2 |= ((int) bytes[offset++]) & 0xFF;
		long2 <<= 8;
		long2 |= ((int) bytes[offset++]) & 0xFF;
		long2 <<= 8;
		long2 |= ((int) bytes[offset++]) & 0xFF;
		long2 <<= 8;
		long2 |= ((int) bytes[offset++]) & 0xFF;

		int1 = ((int) bytes[offset++]);
		int1 <<= 8;
		int1 |= ((int) bytes[offset++]) & 0xFF;
		int1 <<= 8;
		int1 |= ((int) bytes[offset++]) & 0xFF;
		int1 <<= 8;
		int1 |= ((int) bytes[offset++]) & 0xFF;

	}

	// output the Guid to 20 bytes, 160 bits, NOT write to Base64
	public void writeTo20Bytes(byte[] bytes, int offset) {// big endian
		long tmp = long1;

		bytes[7 + offset] = (byte) tmp;
		tmp >>= 8;
		bytes[6 + offset] = (byte) tmp;
		tmp >>= 8;
		bytes[5 + offset] = (byte) tmp;
		tmp >>= 8;
		bytes[4 + offset] = (byte) tmp;
		tmp >>= 8;
		bytes[3 + offset] = (byte) tmp;
		tmp >>= 8;
		bytes[2 + offset] = (byte) tmp;
		tmp >>= 8;
		bytes[1 + offset] = (byte) tmp;
		tmp >>= 8;
		bytes[0 + offset] = (byte) tmp;

		tmp = long2;

		bytes[15 + offset] = (byte) tmp;
		tmp >>= 8;
		bytes[14 + offset] = (byte) tmp;
		tmp >>= 8;
		bytes[13 + offset] = (byte) tmp;
		tmp >>= 8;
		bytes[12 + offset] = (byte) tmp;
		tmp >>= 8;
		bytes[11 + offset] = (byte) tmp;
		tmp >>= 8;
		bytes[10 + offset] = (byte) tmp;
		tmp >>= 8;
		bytes[9 + offset] = (byte) tmp;
		tmp >>= 8;
		bytes[8 + offset] = (byte) tmp;

		int i = int1;
		bytes[19 + offset] = (byte) i;
		i >>= 8;
		bytes[18 + offset] = (byte) i;
		i >>= 8;
		bytes[17 + offset] = (byte) i;
		i >>= 8;
		bytes[16 + offset] = (byte) i;
	}

	public String toStringTrimmed()// not for production - for debugging only, super slow but convenient.
	{
		byte[] bytesin = new byte[20];// 128 bits
		writeTo20Bytes(bytesin, 0);
		byte[] resbytes = Base64Coder.encode(bytesin);
		String res = new String(resbytes).substring(0, 27);
		// trim trailing A's
		while (res.endsWith("AAAAAAAA"))
			res = res.substring(0, res.length() - 8);
		while (res.endsWith("AAAA"))
			res = res.substring(0, res.length() - 4);
		while (res.endsWith("A"))
			res = res.substring(0, res.length() - 1);
		return res;
	}

	@Override
	public String toString() {
		return toB64String();
	}

	public String toLongString()// not for production - for debugging only
	{
		byte[] bytesin = new byte[20];// 128 bits
		writeTo20Bytes(bytesin, 0);
		byte[] resbytes = Base64Coder.encode(bytesin);
		String res = new String(resbytes).substring(0, 27);
		return res;
	}

	public String toB64String()// for production
	{
		byte[] bb = new byte[27];
		toBase64(bb, 0);
		String res = new String(bb);
		if (tests) {
			String test = toLongString();
			if (!test.equals(res))
				System.out.println("fatal");
		}
		return res;
	}

	public String toHexString() {// not for production - for debugging only
		String s1 = Long.toHexString(long1);
		while (s1.length() < 16)
			s1 = "0" + s1;
		String s2 = Long.toHexString(long2);
		while (s2.length() < 16)
			s2 = "0" + s2;
		String s3 = Integer.toHexString(int1);
		while (s3.length() < 8)
			s3 = "0" + s3;

		return s1 + s2 + s3;
	}

	public String toHexString2() {// not for production - for debugging only
		byte[] bytes = new byte[20];
		writeTo20Bytes(bytes, 0);
		StringBuilder dest = new StringBuilder();
		for (byte b : bytes) {
			String s = Integer.toHexString(((int) b) & 0xFF);
			if (s.length() < 2)
				s = "0" + s;
			dest.append(s);
		}
		return dest.toString();
	}

	// write the low 24 bits of val into 4 bytes in the dest
	private final void do3bytes(int val, byte[] dest, int offset) {
		dest[offset - 0] = Base64Coder.map1[val & 0x3F];
		val >>= 6;
		dest[offset - 1] = Base64Coder.map1[val & 0x3F];
		val >>= 6;
		dest[offset - 2] = Base64Coder.map1[val & 0x3F];
		val >>= 6;
		dest[offset - 3] = Base64Coder.map1[val & 0x3F];
	}

	// write the Guid as 27 Base64 bytes
	// the fast version
	public final void toBase64(byte[] dest, int offset) {
		int pos = offset + 27;
		// in reverse order
		int tmp = int1;
		dest[pos - 1] = Base64Coder.map1[(tmp << 2) & 0x03F];// the last 4 bits
		tmp >>= 4;
		dest[pos - 2] = Base64Coder.map1[(tmp) & 0x3F];// last 10 bits
		tmp >>= 6;
		dest[pos - 3] = Base64Coder.map1[(tmp) & 0x3F];// last 16 bits
		tmp >>= 6;
		tmp = (tmp & 0xFFFF) + ((int) long2 << 16);
		do3bytes(tmp, dest, pos - 4);
		tmp = (int) long2 >> 8;
		do3bytes(tmp, dest, pos - 8);
		tmp = (int) (long2 >> 32);
		do3bytes(tmp, dest, pos - 12);
		tmp = ((tmp >> 24) & 0xFF) + ((int) long1 << 8);
		do3bytes(tmp, dest, pos - 16);
		tmp = (int) (long1 >> 16);
		do3bytes(tmp, dest, pos - 20);
		tmp = (int) (long1 >> 40);
		do3bytes(tmp, dest, pos - 24);

		if (tests) {
			String res = new String(dest, offset, 27);
			String test = toLongString();
			if (!test.equals(res)) {
				System.out.println("fatal");
				test = toLongString();
				res = new String(dest, offset, 27);
				toBase64(new byte[27], 0);
			}
		}
	}

	// returns 24 bits in an int while reading 4 bytes from src
	private final int do4base64bytes(byte[] src, int offset) {
		int tmp = Base64Coder.map2[src[offset]];
		tmp <<= 6;
		tmp |= Base64Coder.map2[src[offset + 1]];
		tmp <<= 6;
		tmp |= Base64Coder.map2[src[offset + 2]];
		tmp <<= 6;
		tmp |= Base64Coder.map2[src[offset + 3]];
		return tmp;
	}

	// fill this Guid from 27 Base64 bytes
	public final void fromBase64(byte[] src, int offset) {
		// the same, but much faster than, initFromString_dontUseThis(new String(src,offset,27));
		int pos = offset;
		long tmp1 = do4base64bytes(src, pos);
		long tmp2 = do4base64bytes(src, pos + 4);
		int tmp3 = do4base64bytes(src, pos + 8);
		long1 = (tmp1 << 40) + (tmp2 << 16) + (tmp3 >> 8);
		long tmp4 = do4base64bytes(src, pos + 12);
		long tmp5 = do4base64bytes(src, pos + 16);
		int tmp6 = do4base64bytes(src, pos + 20);
		long2 = ((long) tmp3 << 56) + (tmp4 << 32) + (tmp5 << 8) + (tmp6 >> 16);
		int tmp7 = Base64Coder.map2[src[pos + 24]];
		int tmp8 = Base64Coder.map2[src[pos + 25]];
		int tmp9 = Base64Coder.map2[src[pos + 26]];
		int1 = (tmp6 << 16) + (tmp7 << 10) + (tmp8 << 4) + (tmp9 >> 2);

		if (tests) {
			String b64 = new String(src, offset, 27);
			Guid tmp = new Guid(b64);
			if (!tmp.equals(this))
				System.out.println("fatal fatal fatal fatal fatal fatal ");
		}
	}

	// for debugging only
	public void smashAnd(byte[] bytes) {
		byte[] src = new byte[20];
		writeTo20Bytes(src, 0);
		for (int i = 0; i < bytes.length; i++) {
			src[i] &= bytes[i];
		}
		setFrom20bytes(src, 0);
	}

	public void smashAnd(long l1, long l2, int i) {
		long1 &= l1;
		long2 &= l2;
		int1 &= i;
	}

	// for debugging only
	public void smashOr(byte[] bytes) {
		byte[] src = new byte[20];
		writeTo20Bytes(src, 0);
		for (int i = 0; i < bytes.length; i++) {
			src[i] |= bytes[i];
		}
		setFrom20bytes(src, 0);
	}

	public void smashOr(long l1, long l2, int i) {
		long1 |= l1;
		long2 |= l2;
		int1 |= i;
	}
	
	public void increment( int amount){
		int1 += amount;
	}


	static public Guid SHA1(String stuff) {
		Guid guid = null;
		Crypto.CryptoLocalVars locals = Crypto.messageDigestThreadLocal.get();
		// locals.digest.reset();
		locals.digest.update(stuff.getBytes());
		byte[] key = locals.digest.digest();
		guid = new Guid();
		guid.setFrom20bytes(key, 0);
		return guid;
	}

	static public Guid createSecretGuid() {
		Crypto.CryptoLocalVars locals = Crypto.messageDigestThreadLocal.get();
		if (locals.resetCount-- > 0) {// quick method
			long tmp = locals.rand.nextLong();
			Guid ret = new Guid();
			ret.long1 = locals.lastRandomGuid.long1 ^= tmp;
			ret.long2 = locals.lastRandomGuid.long2 ^= tmp;
			ret.int1 = locals.lastRandomGuid.int1 ^= (int) tmp;
			return ret;
		}
		// the slow way
		locals.resetCount = 256;
		byte[] bytes = Crypto.createSecretKeyBytes();
		Guid guid = new Guid();
		guid.setFrom20bytes(bytes, 0);
		// if (true && tests) {
		// if (allGuids.contains(guid))
		// logger.fatal("guid already exists");
		// allGuids.add(guid);
		// if (allGuids.size() > 1000 * 1000)
		// allGuids.clear();
		// }
		locals.lastRandomGuid.long1 = guid.long1;
		locals.lastRandomGuid.long2 = guid.long2;
		locals.lastRandomGuid.int1 = guid.int1;
		return guid;
	}

	// static Set<Guid> allGuids = new HashSet<Guid>();

	static public Guid createHash(String seed) {
		return SHA1(seed);
	}

	/**
	 * We designate 4 billion as 'special' inasmuch as they will have special handling (logging) in various parts of the
	 * code. Only use these carefully in debugging situations.
	 * 
	 * @param sequence
	 * @return
	 */
	static public Guid makeSpecial(int sequence) {
		Guid guid = new Guid("specialSessionTrackingGuid");
		guid.int1 = sequence;
		return guid;
	}

	static private Guid special = new Guid("specialSessionTrackingGuid");

	public boolean isSpecial() {
		if (long1 != special.long1)
			return false;
		if (long2 != special.long2)
			return false;
		return true;
	}

	

	public static void main(String[] args) {
		for (int i = 0; i < 100; i++) {
			Guid g = createSecretGuid();
			Guid g2 = createSecretGuid();
			assert !g.equals(g2);
		}
		{
			Guid g = createHash("test string");
			Guid g2 = createHash("test string");
			assert g.equals(g2);
		}

		System.out.println(makeSpecial(100));
		System.out.println(makeSpecial(0));

	}
}
