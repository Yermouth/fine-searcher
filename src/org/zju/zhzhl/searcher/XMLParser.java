package org.zju.zhzhl.searcher;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;


public  class XMLParser {
	public static void main(String args[]) throws DocumentException{
//		SAXReader reader = new SAXReader();
//		Document doc=reader.read("./conf/searchConfig.xml");
//		Element root = doc.getRootElement();
//		for(Iterator i =root.element("baidu").elementIterator("options");i.hasNext();){
//			System.out.println(((Element)i.next()).getText());
//		}
//		System.out.println(doc.selectSingleNode("//Sites/baidu/baseUrl").getText());
		XMLParser xmlParser = new XMLParser();
		System.out.println(xmlParser.getSingleSite(xmlParser.getElement("baidu")));
	}
	
	//document object of the xml file	
	private Document doc=null;
	private String filename="./conf/searchConfig.xml";	
	//root element of the document 
	private Element root;
	
	public final String BASEURL="baseUrl";
	public final String NAME="name";
	public final String OPTIONS="options";
	public final String FILTERS="filters";
	public final String KEYWORD="keyWord";
	public final String ENCODING="encoding";
	public final String CONNECTTAG="&";
	public final String STATUS ="status";
	
	
	
	public XMLParser(){
		this.init();
	}
	
	private void init() {
		try{
			//读取config文件，获得文件名字
			//config 指的是具体的文件名字，如keyword.xml 或者是rule.xml
			SAXReader reader = new SAXReader();
			doc=reader.read(filename);
			root=doc.getRootElement();
		}catch(DocumentException e){
			e.printStackTrace();return;
		}
	}
	
	
	
	public Element getRoot() {
		return root;
	}

	public void setRoot(Element root) {
		this.root = root;
	}
	
	public SiteInfo ParserConfig(String site){
		for(Iterator it=this.getRoot().elementIterator();it.hasNext();){
			Element tempElement = (Element)it.next();
			if(tempElement.attribute(NAME).getValue().equals(site)){
				String webSite="";
				String encoding="";
				String wordTag="";
				List<String> tags = new ArrayList<String>();
				for(Iterator it2=tempElement.elementIterator();it2.hasNext();){
					Element element = (Element)it2.next();
					if(element.getName().equals(BASEURL))
						webSite = element.getText();
					else if(element.getName().equals(ENCODING))
						encoding = element.getText();
					else if(element.getName().equals(KEYWORD))
						wordTag = element.getText();
					else if(element.getName().equals(OPTIONS))
						for(Iterator it3=element.elementIterator();it3.hasNext();){
							Element options = (Element)it3.next();
							webSite+=options.attribute(NAME).getValue()+"=";
							webSite+=options.getText()+CONNECTTAG;
						}
					else if(element.getName().equals(FILTERS)){
						for(Iterator it4=element.elementIterator();it4.hasNext();){
							Element filter = (Element)it4.next();
							tags.add(filter.getText());
						}
					}
				}
			return new SiteInfo(webSite,wordTag,encoding,tags);
			}
			
		}
		return null;
	}
	
	/**
	 * 如果不指定具体的网站，则给出所有搜索引擎的信息
	 * @return
	 */
	public List<SiteInfo> ParserConfig(){
		List<SiteInfo> siteInfos  = new ArrayList<SiteInfo>();
		for(Iterator it=this.getRoot().elementIterator();it.hasNext();){
			Element tempElement = (Element)it.next();
			String webSite="";
			String encoding="";
			String wordTag="";
			String status="true";
			List<String> tags = new ArrayList<String>();
			for(Iterator it2=tempElement.elementIterator();it2.hasNext();){
				Element element = (Element)it2.next();
				if(element.getName().equals(STATUS))
					if(!(status=element.getText().toLowerCase()).equals("true"))
						break;
				if(element.getName().equals(BASEURL))
					webSite = element.getText();
				else if(element.getName().equals(ENCODING))
					encoding = element.getText();
				else if(element.getName().equals(KEYWORD))
					wordTag = element.getText();
				else if(element.getName().equals(OPTIONS))
					for(Iterator it3=element.elementIterator();it3.hasNext();){
						Element options = (Element)it3.next();
						webSite+=options.attribute(NAME).getValue()+"=";
						webSite+=options.getText()+CONNECTTAG;
					}
				else if(element.getName().equals(FILTERS)){
					for(Iterator it4=element.elementIterator();it4.hasNext();){
						Element filter = (Element)it4.next();
						tags.add(filter.getText());
					}
				}
			}
			if(status.equals("true"))
				siteInfos.add(new SiteInfo(webSite,wordTag,encoding,tags));
		}
		return siteInfos;
	}
	
	/**
	 * 给定XML配置中具体网址，得到其相应的XML节点。
	 * @param site
	 * @return
	 */
	public  Element getElement(String site){
		for(Iterator it=this.getRoot().elementIterator();it.hasNext();){
			Element element = (Element)it.next();
			if(element.attribute(NAME).getValue().equals(site))//找到属性=site的节点。
				return element;
		}
		return null;
	}
	
	/**
	 * 解析某一个具体网站的网址，生成相应的网址链接
	 * @param site 需要解析的网址
	 */
	public String getSingleSite(Element siteElement){
		String website="";
		for(Iterator it1 =siteElement.elementIterator();it1.hasNext();){
			Element element =(Element)it1.next();
			if(element.getName().equals(BASEURL)){//首先找到基网址
				website=element.getText();
			}
			if(element.getName().equals(OPTIONS)){//然后加入options中的选项与值
				for(Iterator it2=element.elementIterator();it2.hasNext();){
					Element option =(Element) it2.next();
					website+= option.attribute(NAME).getValue()+"=" ;
					website+= option.getText()+ CONNECTTAG ;
				}
			}
		}
		return website;
	}
	
	/**
	 * 给定需要解析的XML节点，返回其相所有的目标Tag
	 * @param siteElement
	 * @return
	 */
	public List<String> getSingleParser(Element siteElement){
		List<String> tags = new ArrayList<String>();
		Element filters = siteElement.element(FILTERS);
		for(Iterator it=filters.elementIterator();it.hasNext();){
			Element filter = (Element)it.next();
			tags.add(filter.getText());
		}
		return tags;

	}
	
	/**
	 * 给定需要解析的XML节点，返回在搜索网址中关键词所代表的符号。
	 * @param siteElement
	 * @return
	 */
	public String getSearchWord(Element siteElement){
		for(Iterator it = siteElement.elementIterator();it.hasNext();){
			Element element = (Element)it.next();
			if(element.getName().equals(KEYWORD))
				return element.getText();
			
		}
		return null;
		
	}
	
	/**
	 * 给定需要解析的XML节点，返回在搜索网址中关键词的编码。
	 * @param siteElement
	 * @return
	 */	
	public String getEncoding(Element siteElement){
		for(Iterator it = siteElement.elementIterator();it.hasNext();){
			Element element = (Element)it.next();
			if(element.getName().equals(ENCODING))
				return element.getText();
			
		}
		return null;
	}
	
	protected void update() {
		try {
			XMLWriter dWriter=new XMLWriter(new FileOutputStream(filename));
			dWriter.write(doc);
			dWriter.flush();
			dWriter.close();
		} catch (IOException e) {
			e.printStackTrace(); 
		}
	}
}
