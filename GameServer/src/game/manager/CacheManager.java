package game.manager;

public class CacheManager {
	
	public CacheManager()
	{
	}
	
	private static CacheManager _instance = null;
	
	public static CacheManager getInstance()
	{
		if(_instance == null)
			_instance = new CacheManager();
		return _instance;
	}
	
	
}
