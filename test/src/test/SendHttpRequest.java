package test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;


public class SendHttpRequest {

	public static void main(String[] args) {
//		SendHttpRequest.requestAndroid();
//		SendHttpRequest.requestIOS();
	}
	
	
	//海外Android和iOS设置
	public static void requestLytoAndroidIosFunc()
	{
		String [] s = {
				"http://54.169.1.41:9001",
				"http://54.169.1.41:9002",
				"http://54.169.1.41:9003",
				"http://54.169.1.41:9004",
				"http://54.169.1.41:9005",
				"http://54.169.1.41:9006",
				"http://54.169.1.41:9007",
				"http://54.169.1.41:9008",
				"http://54.169.1.41:9009",
				"http://54.169.1.41:9010",
				"http://54.169.1.41:9011",
				"http://54.169.1.41:9012",
				"http://54.169.1.41:9013",
				"http://54.169.1.41:9014",
				"http://54.169.1.41:9015"
		};
		
		String kk = "/game/gameShedldPayPlatformQuDao?";
		
		String [] s2 = {
			"99908",
			"99921",
			"99922",
			"99925",
			"99924",
			"99940",
			"99941"
		};
		
		int [][] kvs = {
				{1,0},
				{2,0},
				{3,0}
		};
		
//		game/gameShedldPayPlatformQuDao?channelID=999051&payType=1&isShield=0
		
		String url = "";
		for (int i=0; i<s.length; i++) {
			for(int k=0;k<s2.length;k++){
				url = s[i]+kk + "channelID="+s2[k];
				for(int m = 0;m<kvs.length;m++)
				{
					int [] temp = kvs[m];
					String surl = url + "&payType="+temp[0]+"&isShield="+temp[1];
					try {
						String result = SendHttpRequest.requestUrl(surl);
						System.out.println(i + "服执行结果 " + result);
					} catch (Exception e) {
						System.out.println(i + "服执行异常requestLytoAndroidIosFunc！！！！！====>"+surl);;
					}
				}
			}
		}
	}
	
	/////////////////////////----------------------------
	
	/// 根据支付来进行设置
	public static void requestAndroidIOSHaiWaiFunc()
	{
		String [] s = {
				
				};
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
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static void requestAndroid() {
		
		String s1 = "http://120.132.54.212:";
		String s2 = "http://120.132.51.159:";
		String s3 = "http://120.132.51.160:";
		String s4 = "http://120.132.51.161:";
		String s5 = "http://120.132.51.162:";
		String s6 = "http://120.132.51.163:";
		
//		String url = "";
		for (int i=171; i<=171; i++) {
			
			String kurl = "";
			if (i % 6 == 0){
				kurl = s6;
			}else if(i % 6 == 1){
				kurl = s1;
			}else if(i%6 == 2){
				kurl = s2;
			}else if(i%6 == 3){
				kurl = s3;
			}else if(i%6 == 4){
				kurl = s4;
			}else if(i%6 == 5){
				kurl = s5;
			}
			
			String port = "";
			if(i == 1){
				port = "8082";
			}else{
				port = (9000+i)+"";
			}
			
			kurl = kurl + port+"/gm/removeOldData?key=AddIp930idjz1";
			System.out.println(i + "=== " + kurl);
			
			try {
				String result = SendHttpRequest.requestUrl(kurl);
				System.out.println(i + "服执s行结果android " + result);
			} catch (Exception e) {
				System.out.println(i + "服执aa行异常android！！！！！===>"+i);
			}
			
		}
	}
	
	public static void requestIOS() {
		
		String s1 = "http://112.126.84.186:";
		String s2 = "http://112.126.84.159:";
		String s3 = "http://112.126.85.108:";
		
		////////////
//		String url = "";
		for (int i=1; i<=83; i++) {
			
			String kurl = "";
			if (i % 3 == 0){
				kurl = s3;
			}else if(i % 3 == 1){
				kurl = s1;
			}else if(i%3 == 2){
				kurl = s2;
			}
			
			String port = "";
			if(i == 1){
				port = "8082";
			}else{
				port = (9000+i)+"";
			}
			
			kurl = kurl + port+"/gm/removeOldData?key=AddIp930idjz1";
			System.out.println(i + "=== " + kurl);
			
			try {
				String result = SendHttpRequest.requestUrl(kurl);
				System.out.println(i + "服执s行结果ios " + result);
			} catch (Exception e) {
				System.out.println(i + "服执aa行异常ios！！！！！===>"+i);
			}
			
//			if(true){
//				continue;
//			}
//			
//			if (i == 1) {
////				url = "http://s1.rzhd.hardtime.cn:8082/gm/removeOldData?key=AddIp930idjz1"; //game/teamArmyDataChangeFunc
//				url = s1+"8082/gm/removeOldData?key=AddIp930idjz1";
//			} else {
//				url = "http://s"+i+".rzhd.hardtime.cn:"+(9000+i)+"/gm/removeOldData?key=AddIp930idjz1"; //game/teamArmyDataChangeFunc
//			}
//			try {
//				String result = SendHttpRequest.requestUrl(url);
//				System.out.println(i + "服执s行结果ios " + result);
//			} catch (Exception e) {
//				System.out.println(i + "服执aa行异常ios！！！！！===>"+i);
//			}
		}
	}
	
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
