package org.zju.zhzhl.searcher;

import java.util.ArrayList;
import java.util.List;

/**
 *@author 张知临 zhzhl202@163.com
 *@version 创建时间:2011-12-21下午09:55:32
 *类说明:每个网址的信息。
 */
public class SiteInfo {
	
	private String website; //保存每个网站的查询网址(除了搜索词外)
	private String wordTag;
	private String encoding;
	private List<String> tags;
	public SiteInfo(String website,String wordTag,String encoding,List<String> tags){
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
	public List<String> getTags() {
		return tags;
	}
	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	
	

}
