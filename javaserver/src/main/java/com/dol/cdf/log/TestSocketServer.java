package com.dol.cdf.log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TestSocketServer extends Thread {
	private static ServerSocket server = null;
	private static Socket socket = null;

	public void run() {  
		while (true) {
			try {
				BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				//System.out.println("收到消息"+is.readLine());
				PrintWriter os=new PrintWriter(socket.getOutputStream());
				os.println("succ");
				os.flush();
			} catch (Exception e) {
				try {
					server.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String args[]) {
		try {
			server = new ServerSocket(3040);
			socket = server.accept();
		} catch (Exception e) {
			e.printStackTrace();
		}
		TestSocketServer ts = new TestSocketServer();
		ts.run();
	}
}
