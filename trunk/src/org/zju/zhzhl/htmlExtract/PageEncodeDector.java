package org.zju.zhzhl.htmlExtract;

/*需要下载cpdetector_1.0.5.jar 和 chardet.jar
 * */
/*
 * @author andy
 *///this.encode = new PageEncodeDector().getCharset(url);
import java.io.BufferedReader; 
import java.io.IOException; 
import java.io.InputStreamReader; 
import java.net.HttpURLConnection; 
import java.net.MalformedURLException;
import java.net.URL; 
import java.net.URLConnection;
import java.util.Iterator; 
import java.util.List; 
import java.util.Map; 
import java.util.Set;

import org.htmlparser.Parser;
import org.htmlparser.util.ParserException;

import sun.net.www.protocol.file.FileURLConnection;

import cpdetector.io.CodepageDetectorProxy; 
import cpdetector.io.HTMLCodepageDetector; 
import cpdetector.io.JChardetFacade;

public class PageEncodeDector { 
	
	public static void main(String args[]){
		try {
//			URL url = new URL("http://www.google.com");
//			System.out.println(url.getProtocol());
			System.out.println(new PageEncodeDector().getCharset("http://dict.idioms.moe.edu.tw/mandarin/fulu/dict/cyd/0/cyd00978.htm"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/*public String getCharset(String strurl){
		Parser par=new Parser();
		return par.getEncoding();
	}*/
	
	public String getCharset(String strurl){
		try {
			URL url = new URL(strurl);
			if(true){
				try {
					return this.HttpGetCharset(strurl);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return this.fileGetCharset(strurl);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return "gbk";
		
		
	}
	
	public String fileGetCharset(String url){
		try {
			Parser parser = new Parser(url);
			return parser.getEncoding();
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "GBK";
	}
	

private static CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();

static { 
detector.add(new HTMLCodepageDetector(false)); 
detector.add(JChardetFacade.getInstance()); 
}
/** 
* 测试用例 
* @author andy
* @param args 
*/ 
	/*
public static void main(String[] args) 
{ 
     PageEncodeDector web = new PageEncodeDector(); 
     try 
     { 
     System.out.println(web.getFileEncoding(new URL("file:///G:/x%E9%A1%B9%E7%9B%AE/y%E8%BF%9D%E6%B3%95%E8%AF%AD%E4%B9%89%E5%BA%93/x%E9%A1%B9%E7%9B%AE%E6%96%87%E6%A1%A3/00--%E5%B9%BF%E5%91%8A%E7%B1%BB%E6%B5%8B%E8%AF%95%E7%BD%91%E9%A1%B5/%E8%88%92%E7%AD%8B%E5%81%A5%E8%85%B0%E4%B8%B8%E6%97%A0%E4%BB%BB%E4%BD%95%E6%AF%92%E5%89%AF%E4%BD%9C%E7%94%A8..%E4%B8%AD%E5%9B%BD%E8%85%B0%E9%97%B4%E7%9B%98%E7%AA%81%E5%87%BA%E7%BD%91.htm"))); 
     } catch (IOException e)
     { 
        // TODO Auto-generated catch block 
       e.printStackTrace(); 
     } 
}
	
/** 
* @param strurl 
* 页面url地址,需要以 http://开始，例：http://www.linkzj.cn
* @return 
* @throws IOException 
*/ 
	
public String HttpGetCharset(String strurl) throws IOException 
{ 
    // 定义URL对象 
    URL url = new URL(strurl); 
    // 获取http连接对象 
    URLConnection urlConnection = null;
    if(url.getProtocol().equals("http")){
    	 urlConnection = (HttpURLConnection) url.openConnection(); 
        
    }
    else if(url.getProtocol().equals("file")){
    	urlConnection = (FileURLConnection) url.openConnection(); 
    	
    }
    if(urlConnection!=null)
    	urlConnection.connect(); 
    else return null;
    
    // 网页编码 
    String strencoding = null;
    /** 
    * 首先根据header信息，判断页面编码 
    */ 
    // map存放的是header信息(url页面的头信息) 

    Map<String, List<String>> map = urlConnection.getHeaderFields(); 
    Set<String> keys = map.keySet(); 
    Iterator<String> iterator = keys.iterator();
    // 遍历,查找字符编码 
    String key = null; 
    String tmp = null; 
    while (iterator.hasNext())
    { 
    key = iterator.next(); 
    tmp = map.get(key).toString().toLowerCase(); 
    // 获取content-type charset 
    if (key != null && key.equals("Content-Type")) 
    { 
        int m = tmp.indexOf("charset="); 
        if (m != -1) 
        { 
         strencoding = tmp.substring(m + 8).replace("]", ""); 
         return strencoding; 
        } 
    } 
    }//while
    /** 
    * 通过解析meta得到网页编码 
    */ 
    // 获取网页源码(英文字符和数字不会乱码，所以可以得到正确<meta/>区域) 

    StringBuffer sb = new StringBuffer(); 
    String line; 
    try { 
    BufferedReader in = new BufferedReader(new InputStreamReader(url .openStream())); 
    while ((line = in.readLine()) != null) 
    { 
    sb.append(line); 
    } //while
    in.close(); 
    } catch (Exception e) { // Report any errors that arise 
    System.err.println(e); 
    System.err.println("Usage: java HttpClient <URL> [<filename>]"); 
    } //try-catch
    
    String htmlcode = sb.toString(); 
    // 解析html源码，取出<meta />区域，并取出charset 
    String strbegin = "<meta"; 
    String strend = ">"; 
    String strtmp; 
    int begin = htmlcode.indexOf(strbegin); 
    int end = -1; 
    int inttmp; 
    while (begin > -1) 
    { 
    end = htmlcode.substring(begin).indexOf(strend); 
    if (begin > -1 && end > -1) 
    { 
    strtmp = htmlcode.substring(begin, begin + end).toLowerCase(); 
    inttmp = strtmp.indexOf("charset"); 
    if (inttmp > -1)
    { 
    strencoding = strtmp.substring(inttmp + 7, end).replace( "=", "").replace("/", "").replace("\"", "") .replace("\'", "").replace(" ", ""); 
    return strencoding; 
    } 
    } 
    htmlcode = htmlcode.substring(begin); 
    int beign1= begin;
    begin = htmlcode.indexOf(strbegin); 
    if(begin<=beign1)
    	break;
    }//while
    /** 
    * 分析字节得到网页编码 
    */ 

    strencoding = getFileEncoding(url);
    // 设置默认网页字符编码 
    if (strencoding == null)
    { 
    strencoding = "GBK"; 
    }
    System.out.println("编码方式为:"+strencoding);
    return strencoding; 
}
/** 
* 
*<br> 
* 方法说明：通过网页内容识别网页编码 
* 
*<br> 
* 输入参数：strUrl 网页链接; timeout 超时设置 
* 
*<br> 
* 返回类型：网页编码 
*/ 

public static String getFileEncoding(URL url) {
   java.nio.charset.Charset charset = null; 
   try { 
   charset = detector.detectCodepage(url); 
   } catch (Exception e)
   { 
   System.out.println(e.getClass() + "分析" + "编码失败"); 
   
   } 
   if (charset != null)  //
   return charset.name(); 
   return null;
  }
  


}