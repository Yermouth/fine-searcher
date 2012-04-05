package org.zju.zhzhl.searcher;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.zju.zhzhl.json.*;
import org.zju.zhzhl.json.JSONObject;
public class GoogleSearcher {
   private String keyword; 
   private int amountOfResults;
   private GoogleSearchResult[] searchResults;
   private final String generalUrl = "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&rsz=large&q=";

	public GoogleSearcher(){
		this(4);
	}
	public GoogleSearcher(int amount){
		this.amountOfResults=amount;
		this.searchResults=null;
	}
	
	 public static void main(String []args) throws IOException
	    {
		(new GoogleSearcher(100)).getGoogleUrls("conf/keyWords.txt","result/googleUrls.txt");
		 
/*	    	String keywords="浙江大学";
	    	
	    	//keywords = URLEncoder.encode(keywords, "GB2312");
	    	GoogleSearcher gsearch=new GoogleSearcher(50);
	    	gsearch.googlesearch(keywords);
	    	int num=gsearch.getAmountOfResults();
	    	System.out.println(num);
	    	GoogleSearchResult[] gresults=gsearch.getSearchResults();
	    	for(int i=0;i<num;i++)
	    	{
	    		System.out.println("This is No"+i);
	    		//System.out.println(gresults[i].getSearchResultClass());
	    		//System.out.println(gresults[i].getCacheUrl());
	    		System.out.println("link"+gresults[i].getUrl());//这个就是链接地址
	    		//System.out.println(gresults[i].getVisibleUrl());
	    		//System.out.println(gresults[i].getCacheUrl());
	    		System.out.println("title"+gresults[i].getTitle());//这个就是标题，可以将其中的标签再去掉
	    		//System.out.println(gresults[i].getTitleNoFormatting());
	    		//System.out.println(gresults[i].getContent());
	    		System.out.println("----------------------------");
	    		
	    		//Inputkey.save(gresults[i].getTitle(),gresults[i].getUrl());
	    	}
	*/    	
	    	
	}
	/**
	 * 输入特定的关键词，需要解析的记录条数。程序返回解析得到URL列表。
	 * @param keyword：需要输入的关键词
	 * @param recordNumber:需要解析的记录条数.
	 * @return :返回所有结果的URL。
	 */
	public  List<String> searchUrl(String keyword ,int recordNumber) {
			ArrayList<String> urlList = new ArrayList<String>();
			GoogleSearcher gsearch=new GoogleSearcher(recordNumber);
			try {
				gsearch.googlesearch(keyword);
				GoogleSearchResult[] gresults=gsearch.getSearchResults();
				int num=gsearch.getAmountOfResults();
				if(num>0){
					for(int i=0;i<num;i++){
						urlList.add(gresults[i].getUrl());		
					}
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return urlList;
	}
	public  void getGoogleUrls(String inFilename , String outFilename ) throws IOException{
		GoogleSearcher gsearch=new GoogleSearcher(50);
		File wf = new File(outFilename);
		FileWriter fileWriter = null;
		PrintWriter writer = null;
		if(wf.exists()){//文件如果存在，则以追加的形式写入
			fileWriter = new FileWriter(wf,true);
			
		}else {
			wf.createNewFile();
			fileWriter = new FileWriter(wf);
		}
		writer = new PrintWriter(fileWriter);
		BufferedReader bReader = new BufferedReader ( new FileReader ( new File(inFilename)));
		String keyWord;
		while((keyWord=bReader.readLine())!=null){
			gsearch.googlesearch(keyWord);
			//把结果写入文件
			int num=gsearch.getAmountOfResults();
			GoogleSearchResult[] gresults=gsearch.getSearchResults();
			for(int i=0;i<num;i++){
				writer.println(gresults[i].getUrl());
				writer.flush();

			}
    	    
		}
		if(fileWriter!=null)
			fileWriter.close();
		if(writer!=null)
			writer.close();
		if(bReader!=null)
			bReader.close();
		
	}
	


	public  void googlesearch(String keyword) throws UnsupportedEncodingException{
		keyword = URLEncoder.encode(keyword, "UTF-8");
		this.keyword=keyword;
		searchResults=new GoogleSearchResult[amountOfResults];
		URL url=null;
		String urlString=generalUrl+keyword.replace("\\s+", "%20");
		
		try{
			//JSONArray jsonArr=null;
			JSONArray jsonArr=new JSONArray();
			int index=0;
			for(int i=0;i<amountOfResults;i++)
			{
				if(jsonArr==null||index>=jsonArr.length())
				{
					url=new URL(urlString+"&start="+i);
					URLConnection conn = url.openConnection();					
			        conn.addRequestProperty("Referer","http://www.informatik.uni-trier.de/~ley/db/");
					StringBuilder builder = new StringBuilder();				
					BufferedReader br = new BufferedReader(			
					new InputStreamReader(conn.getInputStream(), "utf-8"));					
					String line;					
					while ((line = br.readLine()) != null) 
					{
					   // System.out.println(line);
						builder.append(line);
					                    
					}
					br.close();

					jsonArr=new JSONObject(builder.toString()).getJSONObject("responseData").getJSONArray("results");
					index=0;
				}
				JSONObject jsonObj = jsonArr.getJSONObject(index);			
				GoogleSearchResult result = new GoogleSearchResult();
				result.setGSearchResultClass(
				jsonObj.getString("GsearchResultClass"));
			    result.setUnescapedUrl(jsonObj.getString("unescapedUrl"));
				result.setUrl(jsonObj.getString("url"));
				result.setVisibleUrl(jsonObj.getString("visibleUrl"));
			    result.setCacheUrl(jsonObj.getString("cacheUrl"));
			    result.setTitle(jsonObj.getString("title"));
				result.setTitleNoFormatting(			
				jsonObj.getString("titleNoFormatting"));				
				result.setContent(jsonObj.getString("content"));			
				searchResults[i] = result;
				++index;
			}
		}catch(Exception e)
		{
			searchResults=null;
			e.printStackTrace();
		}		
		
	}
	
	public String getKeyWord(){
		return keyword;
	}
	public int getAmountOfResults(){
		return amountOfResults;
	}
	public void setAmountOfResults(int amountOfResults){
		this.amountOfResults=amountOfResults;
		this.searchResults=null;
	}
	public GoogleSearchResult[] getSearchResults(){
		return searchResults;
	}

}
