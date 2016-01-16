/*
 * Playdom, Inc. (c) 2009 - 2010 All Rights Reserved
 */
package com.jelly.node.obj;

import com.dol.cdf.common.crypto.Guid;

public class BucketMgr<E> {
    public int bucketCount = (8 * 8)-1;// this is actually better if it's NOT a power

    public Guid checksum = new Guid("unsetBucketMgrChecksum");

    public BucketMgr() {
        this(8 * 8);
    }

    @SuppressWarnings("unchecked")
    public BucketMgr(int count) {
        bucketCount = count;

        distribution = (E[]) new Object[bucketCount];
        for (int i = 0; i < distribution.length; i++) {
            distribution[i] = null;
        }
    }

    private E[] distribution;

    // this needs to be compatable with Guid.maskAndSet
    public final int getIndex(Guid guid) {
        int bits = guid.hashCode();
        bits %= bucketCount;
        if (bits<0)
        	bits += bucketCount;
        return bits;
    }

    public void set(E thing, Guid guid) {
        distribution[getIndex(guid)] = thing;
    }

    public void setI(E thing, int i) {
        distribution[i] = thing;
    }

    public E find(Guid guid) {
        return distribution[getIndex(guid)];
    }

    public E get(int i) {
        return distribution[i];
    }
    
    public int size(){
        return distribution.length;
    }

}
