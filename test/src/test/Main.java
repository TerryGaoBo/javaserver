package test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Main {

	public static void main(String[] args) 
	{
//		System.out.println(172%6);
//		
//		String sk = " ";
//		String[] temp = sk.split(" ");
//		String kk = "";
//		for(int i = temp.length-1;i>=0;i--){
//			if(temp[i].equals("")){
//			}else{
//				kk = kk+temp[i] + " ";
//			}
//		}
//		System.out.println(kk);
//		
//		Integer mm = new Integer(4);
//		Integer mm2 = new Integer(5);
//		Integer mm3 = new Integer(5);
//		
//		ArrayList abc = new ArrayList();
//		abc.add(mm);
//		abc.add(mm3);
//		
//		if(mm.intValue() == mm2.intValue()){
//			System.out.print("sss");
//		}
//		
//		int kmmm = abc.indexOf(mm2);
//		System.out.print("kmmm");
		
//		Main.requestNewIOSAndroidFunc();
		
//		for(int i = 1;i<=172;i++)
//		{
//			String s = "create database ";
//			String n = "balli";
//			if(i> 1){
//				n+=i;
//			}
////			System.out.println(s+n+";");
//			
//			String use = "use " + n+";";
//			String b = "source /Users/DD/Downloads/android/ballitemp/";
//			b = b+n+"_2.sql";
//			System.out.println(use);
//			System.out.println(b);
//			
//		}
	}
	
	public static void requestNewIOSAndroidFunc()
	{
		String s1 = "http://123.56.166.89:"; // android
		String s2 = "http://123.57.13.252:"; // ios
		
		String ks = s1+"8082/gm/reloadModulesConfigFunc?key=AddIp930idjz1&moduletype=9999";
		
		try {
			String result = Main.requestUrl(ks);
			System.out.println(result);
		} catch (Exception e) {
			System.out.println("执行异常");
		}
		
		
		
//		String [] bans = {
//				"SzX8qoydM0rBU+s+vs55AsMuBTM","FZV4++dEv09vB9y1MrnghlCVxL8","TKNZ9PRW2EX80SUNtLTOaOiaRw","XH0o6nq3x5cW+ObbYu+1L0XraBc","ZSzr7c5mGei1sCk0fFyP8/54jkw","PPZCgwUq4weR9OqOpz37Ugf7MyI","xHbXQdDcww/QhnfXoEU6HZXBFds","XVexVFJlpqn3PC4lvhzX+qINaog","KJhmlbbd+mA10XUgR5jOBj5nFHk","sbmaC7ee2UI7ZJr+Cg+PGthIvvE","qcnTlJ1qG6JyAT4+2bRUM4zCjWc","CO7OcNdCfLzisbETLKKUlIQvXp8","u0m3j376V12C9RfL8eXXW+ATv8M","jnP1ySaoaHqglVjtm8f9KMSiIUw","myCNBHsswUBmdKnvMgsnKayfhYw","C+2o9JKSlpdkn364yRp2CyQfLrk","m+IUvNg/37bk8DOjDszrrRuYf0g","dea+BGidJQIqz/oJGA/zUeCQ1LE","4TyKcQe5fKT5BcqAIYfqiJVXcaA","iDQOSPZjjEat1EPBh5w4P5zihOw","tSRRRKhPh95UGNzufGYoV1wgFpA","idhwSuqxl74XWzM9XglBmT1D1JU","kLFDHX1sk7ZSFyR0wYooiuvfzaU","yn6MhJfS9Ft/uI/J35azVUz35RM","aHSakOf/rxfMSt/2EE8QqiUvLmw","p+toWW2f71UPLkbDJmXIkWr05lk","MpxN1eVFJsz65X4WVX1QlH3QkfU","Jgc2uTlQQK/Fq5IBKpNR8fpajG8","3ozbY+JyNpvjcqZ6TTPsuA5QiBg","TXf08UbCgvCbTfkyfg6/8PB1TWE","ZFFUAHWIb+nDVBHJFW10+pkNOxY","qzpfH1YsffDTOeV4MhzUYSzQONw","n8kTdZ6zqPYooKqlhxxChqkL/Rw","1AWL6QZh/JCvQuQM7bUOREFt89c","UcTlaqwy2dJtPaYq9Kz4AUI1xf8","sOWOxO8R0yVOkRmhFeec++gDrpc","ZB1RjLfGelmk6wJ7IQrPAy5dWy0","nVFq9WV+RZe3HuaUIyT8WZTf6sc","TuBTpEUMvLxWLLmBVBuim0nhNT0","ZTjtUEvQynHzEXKwS7OGnQ41fE0","tL+vzphaKfGVjX3xzhuK2OwftBo","oJhBEU0c++uzUB07JexZq3S9Z3M","ljL4OBIxp/WSCvYfJrwYhv9kH9c","","W6L9zFNyqvd1wXJCc6bT0arHrV0","/On8l7lLnLHl5y36etNUviRjksA","jOOGmDNo66RZQShuoItHPwTRr9s","8rRqteAH/xhDDA+tbOTIGpEyTfA","xODRLkXeaqjY7DQBo16qH8Ne5YY","yHSHsu8eSxpU2IOCzyn2+77YoXQ","dVm2VrhzddhUr/7zkLpH6thgs3Y","mVlRDm8M04bbTsoy4h3MMCJ8kEE","6aeBDVDciF9CEBxXfVNebyZUfDw","6c3gbwE0El+WLZfoqe4p08E7ajQ","puN8adNAetJfeC4VzrOWrWdczYA","aIvBxBJ88pVpT1dnhliFNVwA0VM","GgN+tQlo3XiKSQlqUktLwt1UCig","Jme/Dggjee+vAyfZBFLVshRnsLs","rAFE1fAn8Cq4NLNd3j/XqqoaO8g","bR4mZ5X/LezWnQlsm01K68cpfm8","xbwxD3oxF8S3sJJDXffVA/crhl4","LS9ve1+PX67Vt1Q60RYLObAT3yU","orKLCS3iK5kX/isRzMTzQtJvsOw","FzayTHQFDQJqpJQUSdICZh5PXuo","eGBhmM5KYTAFfDreq8Pxoxt6pxA","w/Lyq0G4lTt3GZsMtgKcaJAuLVQ","lO/+ewovqNCAN1ChxFDps7jUlmk","Nqo148kqknYFxSo+o7YvjSSTsR8","OipSi6+kcl++smgCTTms14h6aes","Zg2t4snhRFav2XAcIYhNefMraNw","uQ7/plEgfNrjG+jnSj25+OsuBGM","Al7chETYWy0oXY86TrmRxVJpXtE","qvxtPFVvokKQvHFe7AQo65MD00w","slc0CPn3syM0EcEJUiQmysi7EdU","hA7uG/L7aghrX+WI8CO+Getyey4","ea1tAJ2uyFSi/fvSnAWXEw5BnBU","KPZsDkjhch79sIeZUbK9aiVzx+w","ym6e8diUnhBh6nNtu0EYeUDk8GE","KOsM2YF02rIq/Ny9UCrOmOlm1qE","70Vq7itF3toRoHcy8A7Ffw/aQxg","o3xIPN2QI79DX8rTmaJuVGT9VvY","13ShlBgN4HE70kp7JSg3Ne6NX3A","zjnoyoM1KeS39wjDIJI7RwmtXe4","Ul+2ajHQuruWsg3fSYp7cWZERf4","d1QhSnrSEQrkqSHX2jFwgrwOO/I","e4bSag0zmm1/TYGY0tKNcGEbhv8","PyZ7zHdRUI5wtWYpLWowSLZ5R+A","1FpRVtQvqEVBvV0wJtJOo1wvreU","Cg/R7JiqvviijqCA8ud6/UrOEwo","wwh1WLVl+LyXon17Eum3POlNpoQ","Td5XZ9Uvw3V+80djQbunMSFmG9U","hmCgQmGdMiiBe4EpVRgSJaVlnyM","bTU5/ym6DZv89OKouFVriGXcSqI","J1p4tjEfE8Km7Z8dNQ1bPfTWya4","vBNj7lBYQ5PpynJlJ1jpmmHPMRs","r5VlhU3X9QP0o84dEH/jRyP+7K8","RGfRAWhskfkv9lViYkBsdW+6+VA","Rwp0FTsO3QEvnt49jLBRfXXuh8g","uIjx7x6Ol1+X2KOmILNg75j5pL8","ImNEXXUDJmsgZMeHl9oYSJJjwQE","b9OUU7jNR7hfvsOVN5dByw0KP3w","lW1hvTj+hC1N7ZIGSE5ccVQX1cU","OX5k+0//Qi9gSRkgI9L56Sh6XMY","BXV+s07y/xtWC5jP4Xls/Lw/gEo","DZxd2Q1K838I7oYeQs95toDOEBU","3NWnxSAu/IQj8sdUCINfYTxTllU","XnI0SWKK6sZCRW71QcQRgLZvOIA","uHETv9bxz5FAC1LxIzKoHXCV2uk","Gt4khjh2HdsWO8sZBhdIlDxjJKA","sgZOFq22T/HO62kc4PNFKidV2HM","ENMpzutfc3u1YnZU+btZkVTEpzk","0ate+7qgQPQI5PWY2aYD8cSZV+c","hGP6gVtpQQnF8qFllLI8qAxYPOU","BpmpNTtDEiu4QRLyM2WRkuWFZrg","I1NaKSG3v8qYwJsIG0hhenmKJmI","88XnVKF12Oa8+st371uHhQDuYaU","Ipi32jQ3Vj8w6+IF7WjIR3uajBA","3gmNH+Uk/DOZH5x13uQgXuWEwoA","HEpAUJOd1pL5wCkFVuqriyC7rZI","OMxiM09aoNOkAyYPdSNuk0TIov0","UaRsjrRYk+deYQMfdDc3WRnVi/Y","vN43t97NWFnHmme2Fm6of9etwhQ","h5Z4NTv6pCscO5fXAtRol7hdI7c","cmekY+KX1z9yHXPUP6/QQmobLyU","TjCvca1FxVSmZOp7X2TfQ8SE3lA","zqX3AWITIAnTyAf0IhOI+A5rvbs","nWjsxvB5s3OP3frd2TXK+E4azEA","hNyQm3zTQvzmCCdEVsQlsd6mOYc","DGPrPA8Jdcwo/tDB1vF3bUUcqeI","KQDKbQE10I2tfhCID7H++fjl5eI","udYV242I0nY/dA25huNkUXf/msc","nJKOuuAeDIAhSG3mHe24X1n4N9M","hJ2mJ8S3zajHcqAkFKZdDshKPZA","Dim/6hqo51BadSafPJ+YtsxZ4QE","crLXPbvO10hlouQCWWNyy1G1jDg","8NdlTAg7vSIBtDXZcUeHVPPwhL8","uICJ18JZyFqpNii2aA90yIAj3hU","UACsgsk2J0W411SZrduHTrickuI","xW/EzgFjkJmLaixQ3HwG4n7ibIE","b08OWcc9x+DM1P+H366cg6Zu4Y4","OHQroLNGXKu/KC+Tmiv40mPPYkE","Ru8ndV/mvRIiENAZPaA4hzK9neg","nLpZ21tqEASNfE2AxRKLkxWbS1o","HmKxhWyHLjYlBPEIuOnfhrLTOtU","7diMew/yLIDYYWWNJtLvrn8bQuQ","TEL4JUABTlcK+3PIZxDMbjTJIaw","xN656V5BHA2Ntm3wY9N5bNmLAbY","GJ4uz4dmYFvH3Ybfx/I4odeuFI8","8oJA01Gi/tOvJAw/DO2R3X1SKaM","3iEUOgqlV1nWm/7zHQnA95ZCZM0","LKoJrPi1xJm93Tsy26+rg1935a8","kszWYQIU+CDSpcdY7kvKskQTBxI","L5No3Ho3F48JRoFQ/7ZQ2/FEr8I","bOTcGTufu7Zyr7QwQjohKaD/lY4","oDcN7HkDAODRIMxqwX3f185zkrE","YSBphR7tdsHM1kiKHKAHN4WxJJg","4IdqME7G3t/ArZi+wReRdyeZuBo","Fofit70K5d/7tnnCHPkF5+wD6XQ","/05Tc87Lrfe9cySGxY6iCkB+BD8","Qd/RyECCqn8iLBxLHyZa+XGOxA8","4i9p29BN+Z+qeYiepu6NgDqgmSw","cpao35bS6GBqZJoDn/uAN2ipcQk","770UknjDLqdMMt4wfDyYH+C1Iks","zuGFXsIxvwjz1+1uX+gmPU3ToZ8","sby5ej2cYJW8w1n4RBzrWVqQLSw","tRuiuLNsymR81xNVNUoYqCSqjew","Jy77FjFQ72BN3fwcmqhKohSTVvg","Pkm+iacMsMbz6FVEllUP6yr/GMI","Il/ryHOBHxa6ImBfjgXtlexhk6Y","90KK3YDshLxFqFIaksLXFRFT5yk","CrXdLwr5hFdGNs+Q78AOS37cyik","fZs2hfDK7gQYx5rZdAYPBkjPwoU","eVz4r/1D9cUMDbGxIJxhJef6qQM","BkAKzaCR71mg7WhXJBYq0bKGvGo","NzoEhZqJuGVrIpsaYrysF3eYTfI","BuiiLYKZMWWPmuhUerBpemHJ2QQ","tKt621Qcy1F4MrlwkReO/NRFS70","2WPIr60J5OInnvB2jkLm64V1U/Y","cuqqPYKWo0BV8JwE4rTxZSQdsx8","wOEJ4YaOwZqjZ+N6tE/ynbuhfJE","cafoZPC2+S4LUq4JGtzImyIFvzQ","X58A7eMy5mUSNLZoApx/6Pt83hs","jdhu8/aYWUYTPOdPvzscnwJZbFw","OLr2pZTNxommEao/HciJqLiIv8M","GnkklJ8wxXY0PzbCOUZ4xOu/C8U","4Dm0WZvCNpMI6rvw6c4IvxciFkI","9VloWroLMtUouDTFDq3fjYb0hLo","2XHlF+enI9CFGxISE1HzFgxx7pI","x2aUDuZY5f1VtOE5pxAgXdXwGbI","nQeQWTFGQPWlXnBNC0/vWqHAlHk","Q2SIqhZyXf0HlI7ZEMVJoC8dtGE","BYCwfR1MagsniJfIos+tCeNDx4U","S+rqJuBbHMviKHDMv3GtnEb7p/Q","Kn8VwX7L+Fnh+1NQVvpUFQjlMPg","dJVIiqQYa5F1QGjG66pwSD1WSzs","BgdC7fbGHYBQjz5w/vcy2VXR4g8","ppqHZYIcVYqFOHw9eUNuZ3dTuRM","HiBqcZhSozWFsHYwBVGzRwhEdkk","zKctGnE8uS4TzIOD8uMjjoEw/9g","PTmf75DlEUgxxDfdPnlSTuhxXlQ","vtA2AXcR3nK2SZeDWTPTmOw7ep4","4rp3AakdMDZsscmQ8CS8isHxs1U","7UajaaX6KTzqyAaD4YTAYvPJIto","mJcIdXM1C5GKRcrtC7WgHLyDH90","fXZ2Msj4QiL1oJEo6YJDLKHSre8","Oth6gIKoqpYZ67TSnnDIAnOS3gI","DiF92bj4WF+ekiI6O5t/DncA8fk","4CaJIjjK9Xtu/HiAxeds1VM/syw","jsOQJHSiXkCHiRSw/ycRiQbWBdY","IJUgKjWsWcy/OG+ftpzZ69fL+no","DwH8saw8MiERSPAp1JCW1ifHzwM","LaDopstGGhobnF5PXO836nXD2FI","rHCqlvkPEtuJqge2lLaVlzhCJmY","QHCLlSluCa/WLhzmpFWI1lPE8J0","I94DAM/4lkShkjAOdI9Cf4IKzm0","isaWOnzq1p/4Xn+l5DnaeIoES8Y","0dqkIhaQksgaN+upU9vFSSJhTOM","Cg/gqJzjqzW3dTEJBVvL+BVwQSs","DMmf6q6JFGVUmZQz5+n7dyNoTek","9G+403XhtXBj+ke2mqi5Tu+Bi4s","L265q7LOLfWuqmusvTqI96IQNKA","Urj+7C8sFXI/aRBtcwrvXRui2AU","svQtgE4qpK/i4RWQqJ2mdqBhhbE","SpD3/QD/taEd9PEdiqV6WcLtzl0","yO2v2CfIpczIkw39FsApLEQ3RIs","iaZvBtbwkqYqyui0KJ3lPOPF4+0","BHpk3hUhuCSah04V4hSJmOlsihI","w81AfaD9DCMPmRSQfxGiYZsZE38","KIJ5XIM2TsoGqaQolQxM2UCSt6Q","T+AevIRY4NBM+ewkwXrF10b+K3k","B3pS66+wih0BkrGbSkJaS4YLeuQ","fF/2FH9JCR2rTg2RKfJUqjmmChk","raXmVF66i7Gq++Il7QZo5RVr89E","KTjvny1sTzPj5iFBe3V6JCiGJXQ","SLIIa40Hz0CLjYxLnDtO70BNaNs","m6sRC3ISx+Dep2pvMbRm674Vfo0","9mdYPCHWs7BF2KeuFdDnhRdlSqU","sE3WICmUbF7BWr4PQW3+CBXvjuo","FRpSFoN/CqffkoiZORxb2U+k+b0","jMQn82rDHUbaOJ/QaPWS6yrR2ic","OaPwjBkOEIaTIxdOUhpBwLiSpD0","sWQvK/dH49OARw75WdfWGdqIoWs","y5O1aCOU5XgXfuTcZGR4lwf3ABI","8F/wF795ZHslx/ntfJei1dkSQUw","R+9ljnoOQ0mxydT9z1YqgzwJQ58","5AaC5KDB5ls8aTUeLyQzHVV973I","9hZTNSoxflZqjEWYuTdIqdC8fhA","ISNNNhxP42y0uhqMH6lrGT3iqvg","ClE9dL134KjbXNx6IGnDmWDbhs4","SB0TdyYXKDG8mtlx4qReN1zWz4c","AB/aLiuFxfdJMuQZsr1BDZMXTKs","6KV5fncnfItLOkHPc8uZ65UeGQw","4fWrbBnrW5Nuhb+27JKYpwxiMh8","gqtnoE5YkBd50SH4Ls0H1aETzRA","J7WbwFFNojeS+OK+arH1w2z50VM","/UesCWD6wMHpzmKLYX/F/6UgqTY"
//		};
//		
//		for(int i = 0;i<bans.length;i++)
//		{
//			String dx = bans[i];
//			String ks = s1+"8082"+"/game/banRoleByGuid?guid="+dx;
//			try {
//				String result = Main.requestUrl(ks);
//				System.out.println(result);
//			} catch (Exception e) {
//				System.out.println("执行异常");
//			}
//		}
	}
	
	public static void requestIOSAndroidLyto(){
		String [] url = {
				"54.169.1.41",
				"54.254.158.197",
				"52.74.78.21",
				"52.74.122.212",
				"52.74.143.171",
				"52.74.60.18",
				"52.74.216.67",
				"52.74.211.164",
				"54.169.100.217",
				"52.74.99.98",
				"52.76.82.143",
				"54.255.207.194",
				"54.255.223.191",
				"54.251.168.84",
				"54.251.135.50",
				"54.255.182.121"
		};
		
		for(int i = 0;i<url.length;i++){
			String ks = url[i];
			String port = (9000+i+1)+"";
			
			ks = "http://"+ks+":"+port+"/game/updateVersion";
			System.out.println(i + "=== " + ks);
			try {
				String result = Main.requestUrl(ks);
				System.out.println(i + "服执s行结果ios " + result);
			} catch (Exception e) {
				System.out.println(i + "服执aa行异常ios！！！！！===>"+i);
			}
		}
	}
	
	public static void requestIOSAndroid() {

		String s1 = "http://123.56.166.89:"; // android
		String s2 = "http://123.57.13.252:"; // ios
		
		int sid = 80;
		int aid = 172;
		
		int k = aid;

		for (int i=1; i<=k; i++) {
			String kurl = s1;
			String port = "";
			if(i == 1){
				port = "8082";
			}else{
				port = (9000+i)+"";
			}

			kurl = kurl + port+"/game/clearTeamApplyData";
			System.out.println(i + "=== " + kurl);

			try {
				String result = Main.requestUrl(kurl);
				System.out.println(i + "服执s行结果ios " + result);
			} catch (Exception e) {
				System.out.println(i + "服执aa行异常ios！！！！！===>"+i);
			}
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
			con.setConnectTimeout(100000);
			con.setReadTimeout(100000);
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
