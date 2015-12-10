package game.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileHelper {
	
	/**
	 * 以指定的编码向指定文件中写入字符串(覆盖原文件)
	 * 
	 * @param path
	 *            文件路径
	 * @param content
	 *            要写入的文件内容
	 * @param charset
	 *            指定的编码，例如"utf-8","gbk"
	 * @throws IOException 
	 */
	public static void writeFile(String path, String content, String charset) throws IOException {
		File f=new File(path);
		FileOutputStream fos=new FileOutputStream(f);
		OutputStreamWriter osw=new OutputStreamWriter(fos,charset);
		osw.write(content);
		osw.close();
	}
	
	/**
	 * 以给定的编码区读取文件内容，将文件内容作为字符串返回
	 * 
	 * @param path
	 *            要读取的文件的路径
	 * @param charset
	 *            指定的字符集 如"utf-8","gbk"
	 * @return
	 * @throws IOException
	 */
	public static String readFile(String path, String charset)
			throws IOException {
		File f = new File(path);
		try {
			FileInputStream fis = new FileInputStream(f);
			InputStreamReader isr = new InputStreamReader(fis, charset);
			String result = "";
			int read;
			while ((read = isr.read()) != -1) {
				result += (char) read;
			}
			isr.close();
			return result;
		} catch (FileNotFoundException e) {
			// 文件不存在，返回null
			return null;
		}
	}
	
	
	///////////////////////////////////////////////////////////////////////////
	/**
	 * 抓取某个网页的源代码
	 * @param urlstr 要抓取网页的地址
	 * @param charset 网页所使用的编码 如"utf-8","gbk"
	 * @return
	 * @throws IOException
	 */
	public static String fetchHtml(String urlstr, String charset)
			throws IOException {
		URL url = new URL(urlstr);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		InputStream is = con.getInputStream();
		InputStreamReader isr = new InputStreamReader(is, charset);
		String result = "";
		int read;
		while ((read = isr.read()) != -1) {
			result += (char) read;
		}
		isr.close();
		return result;
	}
	
	/**
	 * 从指定文件的url下载文件
	 * @param url 指定文件的url
	 * @param filename 下载后存储的位置
	 * @throws IOException
	 * new FileFetcher().fetchFile("http://image.s1979.com/allimg/120712/15361I934-4.jpg","download.jpg");
	 */
	public void fetchFile(String url,String filename) throws IOException
	{
		//建立一个到需要下载文件的URL的http链接
		URL _URL=new URL(url);
		HttpURLConnection con=(HttpURLConnection) _URL.openConnection();
		
		InputStream is=con.getInputStream();
		
		//新建一个文件作为下载后的文件
		File downloadfile=new File(filename);
		FileOutputStream fos=new FileOutputStream(downloadfile);
		
		//将从http链接中获取的网络流读取的数据直接写入文件流
		byte[] buf=new byte[1000];
		int read;
		while((read=is.read(buf))!=-1)
		{
			fos.write(buf,0,read);
		}
		is.close();
		fos.close();
	}
}
