/*
 * Playdom, Inc. (c) 2009 - 2010 All Rights Reserved
 */
package com.jelly.node.datastore;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;


/**
 * The is writes updates to DB with a separate thread pool so that the writer does not does not have to wait for the
 * update to complete.
 * 
 * Another advantage is that many times, the same key is updated many times a seconds. This updater will only
 * write the latest copy if DB is is slower than the rate of updates.
 */


public abstract class AsyncDbUpdater  implements Runnable {
	
	static public Logger logger = Logger.getLogger(AsyncDbUpdater.class);

	//   private HashMap<Guid, JotData> map = new HashMap<Guid, JotData>();
	//    private Queue<Guid> queue = new LinkedList<Guid>();

	private HashMap<JotData, JotData> map = new HashMap<JotData, JotData>();
	private Queue<JotData> queue = new LinkedList<JotData>();

    private int MAX_QUEUE_SIZE = 40 * 1024; // not sure of the the right limit yet.
	private int MAX_UPDATE_THREADS = 1;// before is 32 modify by zhl

    private int inFlightUpdates = 0;

    private ExecutorService threadPool = Executors.newCachedThreadPool();
    
    String name;

    public AsyncDbUpdater( String name ) {
    	this.name = name;
    }

    synchronized int numPendingUpdates() {
        if (map.size() != queue.size()) {
            // sanity check. could be removed.
            logger.fatal("Unexpected mismatch in map and queue sizes : " + map.size() + " " + queue.size());
        }
        return queue.size() + inFlightUpdates;
    }

    public void shutdown() {
        threadPool.shutdownNow();
    }

    /**
     * Queues an update. This will block if the max queue size is reached.
     */
    synchronized void addUpdate(JotData jdata) {
        //Guid guid = jdata.guid;
        while (true) {
        	//logger.info("addUpdate. ... . ");
            if (map.containsKey(jdata)) {
                // just update the value.
                 //logger.info("cache hit for " + jdata.guid);
                map.put(jdata, jdata);
                return; // done
            }

            if (queue.size() < MAX_QUEUE_SIZE) {
                map.put(jdata, jdata);
                queue.add(jdata);

                if (inFlightUpdates < MAX_UPDATE_THREADS) {
                    // see the comment in run().
                    inFlightUpdates++;
                    threadPool.execute(this);
                }
                return;
            }

            ;//logger.error(jdata.guid + " max queue size is reached. waiting.");

            try {
                wait(30 * 1000);
            } catch (InterruptedException ignored) {
            }
            ;
        }
    }

    public void run() {

        /*
         * We use newCachedThreadPool so that idle threads get removed. At the same time, we want to limit the max
         * threads to MAX_UPDATE_THREADS. This limit is enforced my inFlightUpdates, which essentially represents
         * number of active threads.
         * 
         * Each thread runs as long as the queue is not empty and whenever a new update is queued, a new thread is
         * is started if the number of update threads is less than max.
         * 
         * Such a thread pool is useful for Context() as well. We could make this a utility.
         */

        Thread.currentThread().setName(name); // could add a number.

        while (true) {
        	//logger.info("run. ... . ");
        	JotData key;
            JotData value;

            synchronized (this) {

                if (queue.isEmpty()) {
                    inFlightUpdates--;
                    return;
                }

                key = queue.remove();
                value = map.remove(key);
                notify();
            }
            if (value != null) {
                try {
                    //dbUpdate(value.guid, value.version, value.bytes);
                	doDbUpdate(value);
                } catch (SQLException e) {
                	logger.error("dbUpdate error key:" + key,e);
                }
            } else { // not expected.
                logger.error("Unexpected : " + key + " is missing in the map.");
            }
        }
    }
    
    private void doDbUpdate( JotData value ) throws SQLException{    	
    	value.doUpdate();
    }
}
