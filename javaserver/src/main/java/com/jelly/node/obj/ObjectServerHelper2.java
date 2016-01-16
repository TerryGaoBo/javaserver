package com.jelly.node.obj;
///*
// * Playdom, Inc. (c) 2009 - 2010 All Rights Reserved
// */
//package com.xin.node.obj;
//
//import java.io.File;
//import java.io.FilenameFilter;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.atomic.AtomicInteger;
//
//import org.apache.log4j.Logger;
//
//import com.dol.cdf.common.crypto.Guid;
//import com.xin.node.datestore.DataStore;
//import com.xin.node.datestore.MySqlDataStore;
//
//public class ObjectServerHelper2 {
//    static public Logger logger = Logger.getLogger(ObjectServerHelper2.class);
//
//    public final static int ObjectServerBucketCount = 257;// a prime, was 256
//    
//    public final static int cacheExpireTime = 90 * 60 * 1000;// 90 minutes
//
//
//    public BucketMgr<ObjectCacheBucket> objCacheBuckets;// bucket to map
//
//    private static final boolean useBackupFileManager = true; // ALWAYS true atw 4/19/2011
//    
//    // for now we try the BackupFileManager (ie MySqlBucketedDataStore) and if we find the object
//    // the don't check the  dataSource
//    //private boolean checkBackfilemgrFirst = true;// implement me
//    
//    //public BackupFileManager backfilemgr = null;
//    public DataStore backfilemgr = null;
//    //public long cachedByteCount = 0;// get rid of this
//    //public long maxCachedByteCount = 4L * 1024 * 1024 * 1024;// 4 gig
//
//    // if not using the backfilemgr then use this data source
//    //delete me public DataSource dataSource = null;
//  //delete me public DataSource slaveDataSource = null;
//    
//    //private AsyncDbUpdater dbUpdater;
//
//    // from an item to a widget server - not to a widget
//    
//    public AtomicInteger pendingDbReads = new AtomicInteger(0);
//    
//    public long gotreads = 0;
//    public long missedreads = 0;
//    Map<Guid,Integer> missedSet = new ConcurrentHashMap<Guid, Integer>();
//
//    public ObjectServerHelper2() {
//
//        	logger.info("Starting data store for " + ggg);
//
//            if (useBackupFileManager) {
//				// backfilemgr = new BackupFileManager(g.peer.id.name.toB64String());
//            	
//            	// FIXME: use factory pattern instead of 'new'
//            	// FIXME: use dependency injection instead of this
//
//				if (backfilemgr == null && ConfigManager.mgr.getString("BackupFileManager.LargeObjectDataStoreAdapter.handler") != null) {
//
//					backfilemgr = new LargeObjectDataStoreAdapter();
//					backfilemgr.init(g.peer.id.name.toStringTrimmed());
//					if (! backfilemgr.ok())
//						backfilemgr = null;
//				}
//
//				if (backfilemgr == null && ConfigManager.mgr.getString("BackupFileManager.MySqlBucketedDataStore.path") != null) {
//
//					backfilemgr = new MySqlBucketedDataStore();
//					backfilemgr.init(g.peer.id.name.toStringTrimmed());
//					if (! backfilemgr.ok())
//						backfilemgr = null;
//				}
//				if (backfilemgr == null && ConfigManager.mgr.getStrings("BackupFileManager.MySqlShardDataStore.path").size() > 0 ) {
//
//					backfilemgr = new MySqlShardDataStore();
//					backfilemgr.init(g.peer.id.name.toStringTrimmed());
//					if (! backfilemgr.ok())
//						backfilemgr = null;
//				}
////				if (backfilemgr == null && ConfigManager.mgr.getString("BackupFileManager.Northscale.path") != null) {
////
////					backfilemgr = new NorthscaleStore();
////					backfilemgr.init(g.peer.id.name.toStringTrimmed());
////					if (! backfilemgr.ok())
////						backfilemgr = null;
////				}
//				if (backfilemgr == null && "true".equals(ConfigManager.mgr.getString("BackupFileManager.derby"))) {
//
//					logger.info("Starting backfilemgr with DerbyStore");
//					backfilemgr = new DerbyStore();
//					backfilemgr.init(g.peer.id.name.toStringTrimmed());
//
//				} 
//				// else use MySqlDataStore and the old ballistics.mysql.info config keys
//				if (backfilemgr == null ) {
//					
//					MySqlDataStore store = new MySqlDataStoreOldSchema();
//				 
//					store.useUpdater = true;
//					store.dbName = "balli";
//					store.tableName = "entities";
//					store.init(g.peer.id.name.toStringTrimmed());
//
//					backfilemgr = store;
//					
//					if (! backfilemgr.ok())
//						backfilemgr = null;
//				}
//
//				if (backfilemgr == null)
//					logger.error("unknown backfilemgr");
//				 
//				// TODO: read maxCachedByteCount from config
//				if (backfilemgr == null)
//					;//useBackupFileManager = false;
//				else
//					if ( useBackupFileManager )
//						logger.info("using backfilemgr useBackupFileManager with " + backfilemgr );
//			}
//
//
//        objCacheBuckets = new BucketMgr<ObjectCacheBucket>(ObjectServerBucketCount);
//        	
//    }
//    
//	public class MyJotData extends JotData {
//
//		@Override
//		public void doUpdate() throws SQLException {
//			logger.error("MyJotData not implemented");
//		}
//
//		@Override
//		public String getConnectionString() {
//			return "MyJotData not implemented";
//		}
//	}
//
//    //private final JotData emptyJotData = new MyJotData();
//	
//	void addToMissedSet( Guid missed ){
//		synchronized (missedSet) {
//			
//			Integer have = missedSet.get(missed);
//			if ( have != null ){
//				have = new Integer(have.intValue() +1);
//			}
//			else
//				have = new Integer(1);
//			
//			missedSet.put(missed,have);
//			
//			if ( missedSet.size() > 10* 1000 ){
//				missedSet.clear();
//			}
//		} 
//	}
//	
//	void removeFromMissedSet( Guid missed  ){
//		synchronized (missedSet) {
//
//			missedSet.remove(missed);
//		}
//	}
//    
// 
//    // the main 'find' in the object servers
//    // we're in a difficult position where this might take a long time
//    // so the callers need to be NOT in the main nio thread.
//	public CachedObject find(Guid guid) throws Exception {
//		long startTime = JotTime.get();
//		pendingDbReads.addAndGet(1);
//		ObjectServerCachedObject osco = null;
//		ObjectCacheBucket ocb = null;
//		try {
//			ocb = objCacheBuckets.find(guid);
//			if (ocb == null) {
//				// There should at least be a bucket for this guid.
//				// we'll just make one now
//				int index = objCacheBuckets.getIndex(guid);
//				ocb = new ObjectCacheBucket(index);
//				objCacheBuckets.set(ocb, guid);
//				// keep going
//			}
//			osco = ocb.get(guid);
//
//			if (osco == null) {
//				JotData jdata = new MyJotData();
//				if (useBackupFileManager) {
//					long backfilestartTime = JotTime.get();
//					jdata = backfilemgr.get(guid);
//					TimingStatCollector.backfileReadTime.add(backfilestartTime);
//				}
//				// if it fails, then we'll say that the object does not exist
//				if ( jdata == null || jdata.bytes == null) {
//					ocb.remove(guid);
//					missedreads ++;
//					return null;
//				}
//				osco = new ObjectServerCachedObject(guid, -9999, null);
//				osco.entity = (Entity) PlayscaleMojasiParser.bytes2Entity(jdata.bytes, g);
////				osco.entity = (Entity) MojasiParser.bytes2Object(jdata.bytes);
//				osco.entity.guid = guid;
//				osco.lastSerialization = jdata.bytes;
//				osco.version = jdata.version;
//				
//				ObjectCacheBucket oc = objCacheBuckets.find(guid);
//				if (oc == null) {
//					logger.error("missing bucket?? this should never ever happen" + guid);
//					missedreads ++;
//					return null;// this should never ever happen
//				}
//				oc.put(guid, osco);//
//				gotreads ++;
//				//messedSet.remove(osco.entity.guid);
//			}
//			
//			// we have the guid, but it's in the backfile.
//			if (osco.entity == null) {// does this case EVER happen ??? atw 2/23/10
//				// read it now
//				// boolean ok = backfilemgr.read(osco, false);
//				JotData jdata = new MyJotData();
//				if (useBackupFileManager)
//					jdata = backfilemgr.get(osco.guid);
//				// if it fails, then we'll say that the object does not exist
//				if (jdata.bytes == null) {
//					ocb.remove(guid);
//					return null;
//				}
//				osco.entity = (Entity) PlayscaleMojasiParser.bytes2Entity(jdata.bytes, g);
////				osco.entity = (Entity) MojasiParser.bytes2Object(jdata.bytes);
//				osco.lastSerialization = jdata.bytes;
//				osco.version = jdata.version;
//				
//				logger.error("dead code dead code dead code dead code dead code  " + guid);
//
//				// try to fetch from dataSource? if the above fails?
//				// if (failed) osco = load FromDataSource(guid, osco);
//			}
//		} catch (Exception e) {
//			
//			failed_in_find_err_count ++;
//			
//			String tr = "";
//			if ( failed_in_find_err_count < 1000 ){
//				StackTraceElement [] stack = e.getStackTrace();
//				
//				for (StackTraceElement stackTraceElement : stack) {
//					tr += stackTraceElement.toString() + "\n";
//				}
//			}
//			logger.error("failed in find " + guid + " " + e.getMessage() + tr);
//			if ( ocb != null )
//				ocb.remove(guid);
//			throw e;
//		}finally {
//			pendingDbReads.addAndGet(-1);
//		}
//
//		TimingStatCollector.objectReadTime.add(startTime);
//
//		if ( osco != null && osco.entity != null) {
//			if (!osco.entity.guid.equals(osco.guid) || !guid.equals(osco.guid))
//				logger.error("Serious guid mismatch for " + osco.entity.guid + " vs " + osco.guid);
//		}
//		if (osco != null)
//			osco.lastTouched = JotTime.get();
//		return osco;
//	}
//	
//	public static int failed_in_find_err_count = 0;
//
//
//    public boolean setForBackupManager(ObjectServerCachedObject osco) {
//        boolean wasnew = true;
//
//        ObjectCacheBucket oc = objCacheBuckets.find(osco.guid);
//        if (oc == null) {
//            oc = new ObjectCacheBucket(objCacheBuckets.getIndex(osco.guid));
//            objCacheBuckets.set(oc, osco.guid);
//        }
//
//        synchronized (oc) {
//            ObjectServerCachedObject prevosco = oc.get(osco.guid);
//            if (prevosco != null) {
//                wasnew = false;
//                if (osco.version < prevosco.version)
//                    logger.error("setForBackupManager regressing version " + osco.guid);
//                ;// logger.info("setForBackupManager replaced " + osco.guid + " v=" + prevosco.version + " to " +
//                // osco.version);
//            } else
//                ;// logger.info("setForBackupManager loaded " + osco.guid);
//            oc.put(osco.guid, osco);
//        }
//        return wasnew;
//    }
//
//    public ObjectCacheBucket getCache(Guid guid) {
//        ObjectCacheBucket oc = objCacheBuckets.find(guid);
//        return oc;
//    }
//
//    public CachedObject put(Guid guid, Entity entity, int version, byte[] recentBytes, boolean isCreate )
//    {
//        if (entity == null)
//            logger.error("missing data " + guid);
//        else
//            entity.version = version;// be sure
//
//        if (!guid.equals(entity.guid))
//            logger.fatal("guid mismatch " + guid + " vs " + entity.guid, new Exception("fatal"));
//
//        ObjectCacheBucket oc = objCacheBuckets.find(guid);
//        if (oc == null) {
//            oc = new ObjectCacheBucket(objCacheBuckets.getIndex(guid));
//            objCacheBuckets.set(oc, guid);
//        }
//        ObjectServerCachedObject osco = new ObjectServerCachedObject(guid, version, entity);
//        osco.guid = guid;
//        osco.version = version;
//        osco.entity = entity;
//        if (recentBytes != null)
//            osco.lastSerialization = recentBytes;
//
//        // let's check if it's already there
//        ObjectServerCachedObject prev = oc.get(guid);
//        if (prev != null) {
//            if (osco.version < prev.version) {// just bail = not allowed
//                logger.fatal("object server have failed put for old version " + guid + " v from " + prev.version
//                        + " to " + osco.version, new Exception("fatal"));
//                return null;
//            }
//            //if (prev.entity != null)
//            //    this.cachedByteCount -= prev.length();
//        }
//        oc.put(guid, osco);// overwrite prev
//
//        // write it to the disk
//        if (osco.entity != null && osco.entity.persistant()) {
////            if (dataSource != null) {
////                if ( isCreate )
////                	writeUsingDataSource(osco,recentBytes);
////                else
////                	writeUsingDataSource(osco,recentBytes);
////            }
//            if ( useBackupFileManager )
//            	if ( isCreate )
//            		backfilemgr.create(osco.guid, osco.version, osco.lastSerialization);
//            	else
//            		backfilemgr.put(osco.guid, osco.version, osco.lastSerialization);
//        }
//
//        // if it's not read only record the subscription to the widget
//
//        if (logger.isTraceEnabled())
//            logger.trace("object server have put " + guid + " bucket " + oc.bucket);
//        
//        return osco;
//    }
//
//
//    
//
//    static byte[] empty = new byte[0];
//
//    static class myFilter implements FilenameFilter {
//        // @Override
//        public boolean accept(File dir, String name) {
//            // return true if name is a long
//            try {
//                Long.parseLong(name);
//                return true;
//            } catch (NumberFormatException e) {
//            }
//            return false;
//        }
//    }
//
//
//
//    public int getSqlQueueSize() {
//        int val = 0;//(dbUpdater == null) ? 0 : dbUpdater.numPendingUpdates();
//        if ( backfilemgr != null )
//        	val += MySqlDataStore.getNumPendingUpdates();
//        return val;
//    }
//    
//     /**
//     * walk all the objects - delete really old ones
//     * 
//     * @param changer
//     */
//    public void forAllObjectsDeleteReallyOld() {
//
//        if (objCacheBuckets == null)
//            return;
//
//        long longLongAgo = JotTime.get() - cacheExpireTime; 
//        int size = objCacheBuckets.size();
//        int removedAmount = 0;
//
//        List<Guid> removeList = new ArrayList<Guid>();
//        for (int i = 0; i < size; i++) {
//            Object obj = objCacheBuckets.get(i);
//            if (obj == null)
//                continue;
//            ObjectCacheBucket ocb = (ObjectCacheBucket) obj;
//            removeList.clear();
//            for (ObjectServerCachedObject osco : ocb.values()) {
//                if (osco.lastTouched < longLongAgo) {
//                    // don't unload it if it's marked not unloadable
//                    if (osco.entity != null && osco.entity.unloadable()) {
//                        // don't unload it if there are subscriptions
//						removeList.add(osco.guid);
//                    }
//                }
//            }
//            removedAmount += removeList.size();
//            for (Guid guid : removeList) {
//                ocb.remove(guid);
//            }
//        }
//        if (removedAmount > 0)
//            logger.info("forAllObjectsDeleteReallyOld removed objects count=" + removedAmount);
//    }
//
//
//    
//    /** attempt to delete this object
//     * return false if the object does not exist (which may not really work right).
//     */
//    
//    public boolean delete(Guid guid) {
//        
//        {ObjectCacheBucket ocb = objCacheBuckets.find(guid);
//        if (ocb != null) {
//            ocb.remove(guid);
//        }}
//        
//        boolean exists = false;
//        
//        //if (dataSource != null) {
//        //    exists |= deleteFromDataSource(guid);
//        //}
//        
//        if ( useBackupFileManager )
//            exists |= backfilemgr.delete(guid);
//     
//        return exists;
//    }
//    
//}
