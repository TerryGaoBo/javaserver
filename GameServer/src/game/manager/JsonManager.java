package game.manager;


public class JsonManager {

	public JsonManager() {
	}
	
	private static JsonManager _instance = null;
	
	public static JsonManager getInstance()
	{
		if(_instance == null){
			_instance = new JsonManager();
		}
		return _instance;
	}
	
}
