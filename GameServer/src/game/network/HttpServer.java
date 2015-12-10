package game.network;

import java.io.IOException;

import game.base.BaseAction;
import game.inter.IHttpServer;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

public class HttpServer implements IHttpServer  {
	
	private HttpServletResponse _response = null;
	private BaseAction _action = null;
	
	private void dataFunc(String indata) throws JSONException
	{
//		String json = "{\"name\":\"ant\"}";
		JSONObject o = new JSONObject(indata);
		System.out.println("c2s====>::"+o);
		String messageID = o.getString("moduleID");
		_action = MessageFac.getActionFac(messageID);
		_action.setData(indata);
		_action.registerMessage(this);
		_action.handle();
	}
	
	public void sendC2S(String message,HttpServletResponse response)
	{
		_response = response;
		try {
			dataFunc(message);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	public void sendS2C(String message)
	{
		if(_response != null){
			try {
//				_response.setStatus(200);// 设置一个成功返回的状态码
				_response.getOutputStream().write(message.getBytes());
				_response.getOutputStream().flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
