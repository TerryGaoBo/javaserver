package javaserver.test;

import org.apache.log4j.PropertyConfigurator;

public class GameServer {

	public static void main(String[] args) {
//		PropertyConfigurator.configure(System.getProperty("log4j.configuration"));
		
		new TestScheduled();
	}

}
