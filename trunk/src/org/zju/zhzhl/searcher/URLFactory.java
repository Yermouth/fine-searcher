package org.zju.zhzhl.searcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.dom4j.Element;


/**
 *
 * 验证url的有效性（能否连通等）
 * 
 * @author zhzhl
 * @date 20110428
 */

public class URLFactory {
	
	public static void main(String args[]){
		
		System.out.println(URLFactory.validateUrl("http://www.csbiji.com"));
	}
	
	/**
	 * 验证该URL是否为有效的URL
	 * @param urlString
	 * @return
	 */
	public static boolean  validateUrl(String urlString ){
		URL url = null;
		try{
			url = new URL(urlString);
		}catch(MalformedURLException e){
//			e.printStackTrace();
			
			System.err.println("url地址错误或者是找不到主机");
			return false;
		}
		HttpURLConnection conn = null;
		try{
			conn = (HttpURLConnection)(url.openConnection());
			if(conn.getResponseCode()!=200){
				System.err.println("该站无法访问，错误代码："+conn.getResponseCode());
				return false;
			}
				
		}catch(IOException e){
//			e.printStackTrace();
			System.err.println("url地址错误或者是找不到主机");
			return false;
		}
		conn.setReadTimeout(5000);
		try{
			conn.connect();
		}catch(IOException e){
//			e.printStackTrace();
			System.err.println("url地址错误或者是找不到主机");
			return false;
		}
		return true;
		
		
	}
	
	public static  ArrayList<String> readUrls(String fileName){
		ArrayList<String> aList=new ArrayList<String>();
		String s=null;
		try{
			File f=new File(fileName);
			BufferedReader bReader=new BufferedReader(new FileReader(f));
			while((s=bReader.readLine()) != null){
				if(s.contains("\"http:")){
					while (s.contains("\"")) {
						if (s.lastIndexOf("\"") == s.length()-1)
							s = s.substring(s.indexOf("\""), s
									.lastIndexOf("\""));
						else
							s = s.substring(s.indexOf("\"")+1);
					}
				
					aList.add(s);
				}
				
			}
			
		}catch(IOException e){
			e.printStackTrace();
		}
		
		return aList;
	}


}
