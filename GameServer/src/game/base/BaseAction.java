package game.base;

import game.inter.IHttpServer;


public class BaseAction {
	
	public void handle()	// receive c2s data to handle
	{
	}
	private String data;
	
	public String messageData;	// s2c data
	
	public void setData(String data)
	{
		this.data = data;
	}
	
	public String getData()
	{
		return data;
	}
	
	public void setServerMessage(String m)
	{
//		this.messageData = "{\"name\":\"ant\"}";
		messageData = m;
	}
	
	public String getServerMessage()
	{
		return messageData;
	}
	
	// 当服务器处理完客户端发送过来的消息之后
	public void handleComplete()
	{
	}
	
	public IHttpServer imessage = null;
	
	public void registerMessage(IHttpServer i)
	{
		imessage = i;
	}
	
	public void sendS2C()
	{
		if(imessage != null){
			imessage.sendS2C(messageData);
		}
		imessage = null;
	}
}
