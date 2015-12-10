package game.manager;

public class PropertiesManager {
	private static PropertiesManager _instance =null;
	public static PropertiesManager getInstance()
	{
		if(_instance == null)
		{
			_instance =new PropertiesManager();
		}
		return _instance;
	}
	
	public PropertiesManager()
	{
		init();
	}
	
//	private Array configs  = null;
	private void init()
	{
		loadProperties();
	}
	
	private void loadProperties()
	{
		
	}
}
