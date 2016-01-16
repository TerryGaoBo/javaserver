package com.dol.cdf.log;

import io.nadron.app.Task;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dol.cdf.common.ContextConfig;
import com.dol.cdf.common.ContextConfig.RuntimeEnv;
import com.dol.cdf.common.config.AllGameConfig;
import com.dol.cdf.log.msghd.HdBaseLog;

public class HdLogThread implements Task{

	private static final Logger LOG = LoggerFactory.getLogger(HdLogThread.class);
	
	private static final short EXECUTE_MSG_COUNT = 100;
	
	//赫德日志服务器地址 内网:10.10.41.183
	private static final String SOCKET_SERVER = "120.132.55.203";
	//赫德日志服务器地址iOS
	private static final String SOCKET_SERVER_IOS = "10.51.113.172";
	//赫德socket端口
	private static final int SOCKET_PORT = 3040;
	//赫德socket测试端口
	private static final int SOCKET_PORT_TEST = 3041;
	
	private Thread processorThread;
	
	private boolean stop = true; 
	
	private Socket socket = null;
	
	private DataOutputStream dos = null;
	
	public HdLogThread() {
		processorThread = new Thread(this);
		stop = false;
		socket = createSocket();
		processorThread.start();
	}
	
	private static HdLogThread instance;
	
	public static HdLogThread getInstance(){
		if(instance == null){
			synchronized (HdLogThread.class) {
				if(instance == null){
					instance = new HdLogThread();
				}
			}
		}
		return instance;
	}
	
	@Override
	public void run() {
		while(!stop){
			try {
				if (socket == null) {
					socket = createSocket();
					if (socket == null) {
						Thread.sleep(1000);
						continue;
					}
				}
				if (socket.isClosed()) {
					LOG.error("赫德socket断开连接！！！");
					continue;
				}
				
				for (int i = 0; i < EXECUTE_MSG_COUNT; i++) {
					HdBaseLog log = LogUtil.queue.poll();
					if (log == null) {
						break;
					}
					byte[] bytearray = bytearray(log.toString());
					dos.write(bytearray, 0, bytearray.length);
					dos.flush();
				}
				Thread.sleep(1000);
			} catch (Exception e) {
				if (socket != null) {
					try {
						socket.close();
						dos.close();
						socket = null;
					} catch (Exception e1) {
						LOG.error("e1",e);
					}
				}
				LOG.error("e2",e);
			}
		}
	}
	
	private Socket createSocket() {
		String ip;
		int port;
		try {
			switch (AllGameConfig.getInstance().env) {
			case IOS_STAGE:	//	赫德IOS平台统计日志服务
				ip = SOCKET_SERVER_IOS;
				port = SOCKET_PORT;
				break;
			case STAGE:	//	赫德安卓平台统计日志服务
			default:
				ip = SOCKET_SERVER;
				port = SOCKET_PORT;
				break;
			}
			
			//	服务器ID大于等于990的都连接赫德测试日志服务
			if (ContextConfig.getFirstServerId() >= 990) {
				if (AllGameConfig.getInstance().env == RuntimeEnv.IOS_STAGE) {
					ip = "112.126.84.221";
					port = 3041;
				} else {
					port = SOCKET_PORT_TEST;
				}
			}
			
			LOG.info("开始连接赫德日志服务器IP=" + ip + ",端口=" + port);
			socket = new Socket(ip, port);
			socket.setKeepAlive(true);
			dos = new DataOutputStream(socket.getOutputStream());
			LOG.info("连接赫德日志服务器成功IP" + ip + ",端口=" + port);
			return socket;
		} catch (UnknownHostException e) {
			LOG.error("连接赫德日志服务器socket失败！ 错误描述: " + e.getMessage());
		} catch (SocketException e) {
			LOG.error("连接赫德日志服务器socket失败！ 错误描述: " + e.getMessage());
		} catch (IOException e) {
			LOG.error("连接赫德日志服务器socket失败！ 错误描述: " + e.getMessage());
		}
		return null;
	}
	
	//先发4字节的日志长度，再发日志内容
	private static byte[] bytearray(String str) throws UnsupportedEncodingException, IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byte[] messes=str.getBytes("UTF8");
		int length=messes.length;
		byte[] lengthbytes = integerToBytes(length, 4);
		byteArrayOutputStream.write(lengthbytes);
		byteArrayOutputStream.write(messes);
		return byteArrayOutputStream.toByteArray();
	}
	
	public static  byte[] integerToBytes(int integer, int len) {
	   ByteArrayOutputStream bo = new ByteArrayOutputStream();
	   for (int i = 0; i < len; i ++) {   
	    bo.write(integer);
	    integer = integer >> 8;
	   }
	   return bo.toByteArray();
	}

	@Override
	public Object getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setId(Object id) {
		// TODO Auto-generated method stub
		
	}

}

