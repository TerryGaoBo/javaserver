package game.manager;

public class LogManager {
	
	private static LogManager _instance = null;
	
	public static LogManager getInstance()
	{
		if(_instance == null)
			_instance = new LogManager();
		return _instance;
	}
	
	public LogManager()
	{
		
	}
	
}
