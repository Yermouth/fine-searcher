package org.zju.zhzhl.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *@author 张知临 zhzhl202@163.com
 *@version 创建时间:2011-12-21下午09:55:32
 *类说明:每个网址的信息。
 */
public class SiteInfo {
	
	private String website; //保存每个网站的查询网址(除了搜索词外)
	private String wordTag;
	private String encoding;
	private Map<String,String> tags;
	public SiteInfo(String website,String wordTag,String encoding,Map<String,String> tags){
		this.website=website;
		this.wordTag=wordTag;
		this.encoding=encoding;
		this.tags = tags;	
	}
	
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	public String getWordTag() {
		return wordTag;
	}
	public void setWordTag(String wordTag) {
		this.wordTag = wordTag;
	}
	public String getEncoding() {
		return encoding;
	}
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	public Map<String,String> getTags() {
		return tags;
	}
	public void setTags(Map<String,String> tags) {
		this.tags = tags;
	}
	
	

}
