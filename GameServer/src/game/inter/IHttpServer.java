package game.inter;

import javax.servlet.http.HttpServletResponse;

public interface IHttpServer {
	public void sendC2S(String message,HttpServletResponse response);
	public void sendS2C(String message);
}
