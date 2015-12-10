package game.manager;

public class DataManager {

	public DataManager() {
		
	}
	
	private static DataManager _instance;
	
	public static DataManager getInstance()
	{
		if(_instance == null){
			_instance = new DataManager();
		}
		return _instance;
	}
}
