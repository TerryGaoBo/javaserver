package com.dol.cdf.log;

import java.util.Queue;

import org.apache.log4j.Logger;

import com.dol.cdf.log.msg.BaseLogMessage;
import com.dol.cdf.log.msghd.HdBaseLog;

public class LogWriteTask implements Runnable{

	public static Logger logger = Logger.getLogger(CdfLog.class);
	
	private static final short EXECUTE_MSG_COUNT = 100;
	
	private Thread processorThread;
	
	private boolean stop = true; 
	
	public LogWriteTask() {
		processorThread = new Thread(this);
		stop = false;
		processorThread.start();
	}
	
	private static LogWriteTask instance;
	
	public static LogWriteTask getInstance(){
		if(instance == null){
			synchronized (LogWriteTask.class) {
				if(instance == null){
					instance = new LogWriteTask();
				}
			}
		}
		return instance;
	}
	

	@Override
	public void run() {
		while(!stop){
			try {
				for (int i = 0; i < EXECUTE_MSG_COUNT; i++) {
					Queue<HdBaseLog> msg = LogUtil.queue;
					HdBaseLog poll = msg.poll();
					if(poll == null) break;
//					if(poll instanceof CostLogMessage){
//						CostLogMessage.logger.debug(poll.toString());
//					}else{
						String urlStr = "?" + poll.toString();
						CdfLog.logger.info(urlStr);
//					}
					
				}
				Thread.sleep(1000);
			} catch (Exception e) {
				logger.error("",e);
			}
		}
		
	}

}
