package org.zju.zhzhl.searcher;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.NodeFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.zju.zhzhl.parser.SiteInfo;
import org.zju.zhzhl.parser.XMLParser;
/**
 *@author 张知临 zhzhl202@163.com
 *@version 创建时间:2011-6-12上午10:21:52
 *类说明:
 *搜索类，有一个函数，searchUrls
 */
public class Searcher {
	
	public static void main(String args[]){
		Searcher se = new Searcher();
		for(String s:se.parserResult( "张知临"))
			System.out.println(s);
	}
	
	private XMLParser xmlParser;
	
	public Searcher( ) {
		xmlParser = new XMLParser();
	}
	
	/**
	 * 根据给定搜索引擎的配置，以及搜索关键词，给出生成的搜索网站链接
	 * @param keyword 搜素关键词
	 * @param siteInfo 搜索引擎的地址
	 * @return 搜索网址
	 */
	public String getSearchSite(String keyword,SiteInfo siteInfo){
		String baseSite = siteInfo.getWebsite();
		String wordTag = siteInfo.getWordTag();
		String encoding = siteInfo.getEncoding();
		try {
			return baseSite + wordTag +"=" + URLEncoder.encode(keyword,encoding);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 函数重载
	 * 根据给定搜索引擎的名称和搜索关键词，给出生成的搜索网站链接
	 * @param keyword 搜素关键词
	 * @param siteInfo 搜索引擎的地址
	 * @return 搜索网址
	 */
	public String getSearchSite(String site,String keyword){
		SiteInfo siteInfo =  xmlParser.ParserConfig(site);
		String baseSite = siteInfo.getWebsite();
		String wordTag = siteInfo.getWordTag();
		String encoding = siteInfo.getEncoding();
		try {
			return baseSite + wordTag +"=" + URLEncoder.encode(keyword,encoding);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 输入需要搜索的网址和搜索关键词，给出搜索页面中的链接
	 * @param site
	 * @param keyword
	 * @return
	 */
	public List<String> parserResult(String site,String keyword){
		SiteInfo siteInfo  = xmlParser.ParserConfig(site);
		String url = this.getSearchSite(keyword,siteInfo);	
		NodeList nodeList=null;
		Parser parser = null;
		List<String> urlList = new ArrayList<String>();
		String encoding = siteInfo.getEncoding();
		Map<String,String> tags = siteInfo.getTags();
		Object[] keys = tags.keySet().toArray();
		
		try {//对结果页面进行解析。	
			parser = new Parser(url);
			parser.setEncoding(encoding);
			//生成结果页面中目标的Tag的Filter
			int length = keys.length;
			if(length==1){
				nodeList=parser.parse(new HasAttributeFilter((String)keys[0],tags.get((String)keys[0])));
			}
			else if(length>1){
				AndFilter tempFilter =  new AndFilter(new HasAttributeFilter((String)keys[0],tags.get((String)keys[0])),new HasAttributeFilter((String)keys[1],tags.get((String)keys[1])));
				for(int i =2;i<length;i++){
					tempFilter = new AndFilter(tempFilter,new HasAttributeFilter((String)keys[i],tags.get((String)keys[i])));
				}
				nodeList=parser.parse(tempFilter);
			}
			// 循环遍历nodeList每个节点
			if(nodeList!=null&& nodeList.size()>0)
			{
				for(int i=0;i<nodeList.size();i++){
					String urlLink = ((LinkTag)nodeList.elementAt(i)).getLink();
					urlList.add(urlLink);
				}
			}
		} catch (org.htmlparser.util.ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ArrayList<String>(new HashSet(urlList));
	}
	
	/**
	 * 输入需要搜索的搜索关键词，给出配置的所有搜索引擎结果页面中的链接
	 * @param keyword
	 * @return
	 */
	public List<String> parserResult(String keyword){
		List<SiteInfo> siteInfos  = xmlParser.ParserConfig();
		List<String> urlLists = new ArrayList<String>();
		for(SiteInfo siteInfo:siteInfos){
			
			String url = this.getSearchSite(keyword,siteInfo);	
			NodeList nodeList=null;
			Parser parser = null;			
			String encoding = siteInfo.getEncoding();
			Map<String,String> tags = siteInfo.getTags();
			Object[] keys = tags.keySet().toArray();
			List<String> urlList = new ArrayList<String>();
			
			try {//对结果页面进行解析。	
				parser = new Parser(url);
				parser.setEncoding(encoding);
				//生成结果页面中目标的Tag的Filter
				int length = keys.length;
				if(length==1){
					nodeList=parser.parse(new HasAttributeFilter((String)keys[0],tags.get((String)keys[0])));
				}
				else if(length>1){
					AndFilter tempFilter =  new AndFilter(new HasAttributeFilter((String)keys[0],tags.get((String)keys[0])),new HasAttributeFilter((String)keys[1],tags.get((String)keys[1])));
					for(int i =2;i<length;i++){
						tempFilter = new AndFilter(tempFilter,new HasAttributeFilter((String)keys[i],tags.get((String)keys[i])));
					}
					nodeList=parser.parse(tempFilter);
				}
				// 循环遍历nodeList每个节点
				if(nodeList!=null&& nodeList.size()>0)
				{
					for(int i=0;i<nodeList.size();i++){
						String urlLink = ((LinkTag)nodeList.elementAt(i)).getLink();
						urlList.add(urlLink);
					}
				}
			} catch (org.htmlparser.util.ParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			urlLists.addAll(urlList) ;
		}
		return new ArrayList<String>(new HashSet(urlLists));
	}
	
	

}
