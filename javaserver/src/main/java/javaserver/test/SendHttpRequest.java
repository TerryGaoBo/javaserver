package javaserver.test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class SendHttpRequest {

	public static void main(String[] args) {
//		SendHttpRequest.requestAndroid();
//		SendHttpRequest.requestIOS();
	}
	
	/// 根据支付来进行设置
	public static void requestAndroidIOSHaiWaiFunc()
	{
		String [] s = {};
		String url = "";
		for(int i=0;i<s.length;i++)
		{
			url =s[i];
			try {
				String result = SendHttpRequest.requestUrl(url);
				System.out.println(i + "服执行结果 " + result);
			} catch (Exception e) {
				System.out.println(i + "服执行异常！！！！！");
			}
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static String requestUrl(String url) {
		try {
			URL dataUrl = new URL(url);
			HttpURLConnection con = (HttpURLConnection) dataUrl.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("content-type", "text/json");
			con.setRequestProperty("Proxy-Connection", "Keep-Alive");
			// con.setRequestProperty("receipt-data", receipt);
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setConnectTimeout(200000);
			con.setReadTimeout(200000);
			// out = new OutputStreamWriter(con.getOutputStream());
			//out.write(String.format(Locale.CHINA, "{\"receipt-data\":\"" + receiptStr + "\"}"));
			//out.flush();
			//out.close();
			InputStream is = con.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String line = null;
			StringBuffer sb = new StringBuffer();
			while((line = reader.readLine()) != null){
				sb.append(line);
			}
			reader.close();
			is.close();
			con.disconnect();
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "error";
	}
}
