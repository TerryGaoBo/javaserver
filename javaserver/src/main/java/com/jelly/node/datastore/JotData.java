package com.jelly.node.datastore;

import java.sql.SQLException;

public abstract class JotData {
    public String guid;
    public byte[] bytes = null;
    public String tableName="entities";
    abstract public void doUpdate() throws SQLException;
    
    abstract public String getConnectionString();// like "allkeys002"
    
    @Override
    public int hashCode() {
    	return getConnectionString().hashCode() + guid.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
    	JotData d = (JotData)obj;
    	return guid.equals(d.guid) && getConnectionString().equals(d.getConnectionString());
    }
    
    @Override
    public String toString() {
    	return guid.toString() + ":" + getConnectionString();
    }
    
    public static class NopJotData extends JotData {

		@Override
		public void doUpdate() throws SQLException {
		}

		@Override
		public String getConnectionString() {
			return "";
		}
    }
}