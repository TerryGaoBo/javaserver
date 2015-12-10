package game.modules.login;

import org.json.JSONException;
import org.json.JSONObject;

import game.base.BaseAction;
import game.manager.DBManager;

public class LoginAction extends BaseAction {
	
	@Override
	public void handle()
	{
		System.out.println("loginaction");
		
		testdb();
		handleComplete();
	}
	
	private void testdb()
	{
		DBManager db = new DBManager();
		db.testDb();
	}
	
	@Override
	public void handleComplete()
	{
		JSONObject o = new JSONObject();
		try {
			o.put("name", "ant");
			o.put("id", 10);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		this.setServerMessage(o.toString());
		this.sendS2C();
	}
}
