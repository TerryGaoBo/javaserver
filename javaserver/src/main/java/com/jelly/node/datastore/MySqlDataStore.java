package com.jelly.node.datastore;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.jelly.node.datastore.mapper.Naru;
import com.jelly.node.datastore.mapper.NaruMapper;

public class MySqlDataStore implements DataStore {

	static public Logger logger = LoggerFactory.getLogger(MySqlDataStore.class);


	private static AsyncDbUpdater dbUpdater;// singleton

	@Autowired
	NaruMapper mapper;
	
	private boolean useUpdater = true;

	private boolean ok = true;
	
	public MySqlDataStore() {
		if (dbUpdater == null)
			dbUpdater = new MySqlAsyncDbUpdater("MySqlUpdater");
	}
	
	@Override
	public byte[] get(String guid) {

		return dbSelect(guid);
	}

	@Override
	public boolean delete(String guid) {
		// wait for it to calm down?
		boolean exists = true;
		exists = dbDelete(guid);
		return exists;
	}

	// actually update
	@Override
	public boolean put(String guid, byte[] bytes) {
		boolean good = false;
		try {
			if (useUpdater) {

				JotData data = new MyJotData();
				data.guid = guid;
				data.bytes = bytes;
				dbUpdater.addUpdate(data);
			} else {
				dbDoUpdateOnly(guid, bytes);
			}
		} catch (Exception e) {
			logger.error("mysql put bytes error {}",e);
		}
		return good;
	}

	public class MyJotData extends JotData {

		@Override
		public void doUpdate() throws SQLException {
			dbDoUpdateOnly(guid, bytes);
		}

		@Override
		public String getConnectionString() {
			return "connStr";
		}
	}

	@Override
	public boolean create(String guid, byte[] bytes) {
		boolean good = false;
		try {
			dbInsert(guid, bytes);
			good = true;
		} catch (SQLException e) {
		}
		return good;
	}

	@Override
	public void stop() {
		while (dbUpdater.numPendingUpdates() > 0)
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		dbUpdater.shutdown();
		ok = false;

	}

	// synchronized DataSource getDataSource_static(String serverName) {
	//
	// return MySQLStore.getDataSource_static();
	// }

//	/*
//	 * mysql
//	 */
//	// static
//	protected void makeSchema() throws SQLException {
//
//		String sourceName = serverString;
//
//		String query = "show tables like '" + tableName + "'";
//		Connection con = null;
//		SqlSession session = null;
//		try {
//			session = dataSource.openSession(true);
//			con = session.getConnection();
//			Statement st = con.createStatement();
//			ResultSet rs = st.executeQuery(query);
//
//			if (rs.next()) {
//				logger.info("db " + sourceName + "/" + tableName
//						+ " already exists in " + dbName + ".");
//				closeDbConnection(session);
//				con = null;
//				return;
//			}
//			logger.info("Creating Table " + sourceName + "/" + tableName
//					+ " in " + dbName + ".");
//
//			String make = "CREATE TABLE IF NOT EXISTS `" + tableName + "`( \n";
//			make += "`guid` BINARY(27) NOT NULL,\n";
//			make += dataColumnName + " BLOB NOT NULL, \n";
//			make += "PRIMARY KEY (`guid`)\n";
//			make += ") ENGINE=InnoDB DEFAULT CHARSET=utf8;\n";// ENGINE=InnoDB,
//																// NO charset
//																// needed or
//																// wanted
//
//			logger.trace("Creating SQL table under '" + dbName + "' : " + make);
//
//			st = con.createStatement();
//			st.executeUpdate(make);
//			st.close();
//
//		} catch (SQLException ex) {
//			logger.warn("failed to contact mysql server : " + ex.getMessage());
//			throw ex;
//		} finally {
//			closeDbConnection(session);
//		}
//	}

	byte[] dbSelect(String guid) {
		Naru val = mapper.getVal(guid);
		if(val != null){
			return val.getVal();
		}else {
			return null;
		}
	
	}


	boolean dbDelete(String guid) {
		mapper.delete(guid);
		return true;
	}

	void dbDoUpdateOnly(String guid, byte[] bytes) throws SQLException {

		boolean ok = false;

		try {
			long start = System.currentTimeMillis();
			if (logger.isTraceEnabled())
				logger.trace("writing " + guid + " to datasource");

			mapper.update(guid, bytes);
			
			ok = true;

			long diff = System.currentTimeMillis() - start;
			if (diff > 5000) {
				logger.warn("Write to dataSource for " + guid
						+ " took a long time : " + diff + " ms.");
			}
		} catch (Exception ex) {
			logger.error("while writing " + bytes.length + " bytes for guid "
					+ guid + " to dataSource : ", ex);
			// throw ex;
		} 

		if (!ok) {

			logger.warn("had UPDATE when needed INSERT " + bytes.length
					+ " bytes for guid " + guid + " to dataSource : ");

			dbInsert(guid, bytes);
		}

	}

	// we know this works for update and/or insert because it's been in
	// production

	void dbUpdateOrInsert(String guid, byte[] bytes) throws SQLException {

		try {
			long start = System.currentTimeMillis();
			if (logger.isTraceEnabled())
				logger.trace("writing " + guid + " to datasource");

			mapper.insert(guid, bytes);
			long diff = System.currentTimeMillis() - start;
			if (diff > 5000) {
				logger.warn("Write to dataSource for " + guid
						+ " took a long time : " + diff + " ms.");
			}
		} catch (Exception ex) {
			logger.error("while writing " + bytes.length + " bytes for guid "
					+ guid + " to dataSource : ", ex);
		} 
	}

	void dbInsert(String guid, byte[] bytes) throws SQLException {
		dbUpdateOrInsert(guid, bytes);
	}

	/**
	 * The is writes updates to DB with a separate thread pool so that the
	 * writer does not does not have to wait for the update to complete.
	 * 
	 * Another advantage is that many times, the same key is updated many times
	 * a seconds. This updater will only write the latest copy if DB is is
	 * slower than the rate of updates.
	 */
	class MySqlAsyncDbUpdater extends AsyncDbUpdater {

		public MySqlAsyncDbUpdater(String name) {
			super(name);
			// this.MAX_QUEUE_SIZE = allowedPendingBacklog;
		}

	}

	public static int getNumPendingUpdates() {
		if (dbUpdater != null)
			return dbUpdater.numPendingUpdates();
		return 0;
	}

	@Override
	public boolean ok() {
		return ok;
	}

}
