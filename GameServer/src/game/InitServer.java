package game;

import game.manager.CacheManager;
import game.manager.DataManager;
import game.manager.JsonManager;
import game.manager.LogManager;
import game.manager.PropertiesManager;

import java.util.Properties;

public class InitServer {
	
	public InitServer()
	{
	}
	public void initGame()
	{
		System.out.println("init server !");
		
		this.initModules();
		this.initDatas();
		
		this.test();
	}
	
	private void initModules()
	{
		PropertiesManager.getInstance();
		CacheManager.getInstance();
		DataManager.getInstance();
		JsonManager.getInstance();
		LogManager.getInstance();
	}
	
	private void initDatas()
	{
	}
	
	private void test()
	{
		Properties pps = System.getProperties();
		pps.list(System.out);
	}
}
