package com.jelly.node.obj;
///*
// * Playdom, Inc. (c) 2009 - 2010 All Rights Reserved
// */
//package com.xin.node.obj;
//
//import java.util.Collection;
//import java.util.concurrent.ConcurrentHashMap;
//
//import com.dol.cdf.common.crypto.Guid;
//
///**
// * ObjectServers use this to keep track of what buckets they serve, and what's in them
// * 
// * @author alanwootton
// * 
// */
//
//public class ObjectCacheBucket {
//    public int bucket;
//
//    public ObjectCacheBucket(int i) {
//        bucket = i;
//    }
//
//    private ConcurrentHashMap<Guid, ObjectServerCachedObject> map = new ConcurrentHashMap<Guid, ObjectServerCachedObject>();
//    
//    public final ObjectServerCachedObject get(Guid guid){
//        return map.get(guid);
//    }
//    
//    public final void put(Guid guid, ObjectServerCachedObject osco){
//        map.put(guid, osco);
//    }
//    
//    public final void remove(Guid guid){
//        map.remove(guid);
//    }
//    
//    public final Collection<ObjectServerCachedObject> values(){
//        return map.values();
//    }
//}
