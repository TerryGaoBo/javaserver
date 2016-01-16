/*
 * Playdom, Inc. (c) 2009 - 2010 All Rights Reserved
 */
package com.jelly.node.datastore;



public interface DataStore {



    public boolean create(String guid, byte[] bytes);

    // this is actually update
    public boolean put(String guid, byte[] bytes);

    // retrieve. might return null, or JotData.data == null
    public byte[] get(String guid);
    
    // tries to return false if the object didn't exist.
    public boolean delete(String guid);
    
    // return false if the init failed or something
    public boolean ok();
    
    public void stop();
    
}
