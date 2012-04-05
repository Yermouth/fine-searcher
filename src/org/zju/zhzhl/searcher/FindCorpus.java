package org.zju.zhzhl.searcher;

import java.io.UnsupportedEncodingException;
import java.util.List;
import org.htmlparser.util.ParserException;
import org.zju.zhzhl.parser.FileParser;
import org.zju.zhzhl.parser.HTMLParser;

/**
 *@author 张知临 zhzhl202@163.com
 *@version 创建时间:2011-7-10下午09:05:36
 *类说明：
 *利用关键词从搜索引擎中搜索URL，先实现百度\Google。
 */
public class FindCorpus {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String inFilename="conf/keyWords.txt";
		
		String urlFilename = "result/urls20110711.txt";
		
		String savePath = "G:\\x项目\\Taobao\\工作交接内容\\程序\\parser-search\\result";
		String PrefixFilename = "2";
		String suffix=".txt";
		int startCount=2830;
		int length=5;
		
		int recordNumber=200; //每个关键词搜索的记录条数
		
//		构造FindCorpus对象
		FindCorpus fc = new FindCorpus(inFilename,recordNumber,urlFilename);
//		初始化保存文件的名称。
		fc.initFilename(savePath, PrefixFilename, suffix, startCount, length);
//		开始进行查找语料库
		fc.createCorpus();
		
//		fc.saveUrlContent("http://dict.idioms.moe.edu.tw/mandarin/fulu/dict/cyd/0/cyd00978.htm");

	}
	
	private String inFilename;//关键词所在文件名
	private String urlFilename;//已经处理过得Url集合保存的文件名称.
	private String savePath;//文件保存的路径。
	private String PrefixFilename;//文件名前缀
	private String suffix;//文件名后缀，如".txt"
	private int length; //文件名得长度。
	private int startCount;//系统的当前计数起始值。随着文件数增长，充当计数器
//	private String[] platForm; //巡检平台
//	private int recordNumber; //查找的记录条数。
	

	
	
	public FindCorpus(String inFilename,int recordNumber,String urlFilename){
		this.inFilename=inFilename;

		this.urlFilename = urlFilename;
	}
	
	public void createCorpus(){
		List<String> keywords = FileParser.readFileToList(inFilename);
		for(String keyword:keywords){
			System.out.println("现在巡检的关键词为："+ keyword);
			this.createCorpus(keyword);
		}
		
	}
	
	public void createCorpus(String keyword){
		List<String> urls = this.searchUrl(keyword);
		List<String> readerUrls = URLFactory.readUrls(urlFilename);
 		for(String url:urls){
			System.out.println("现在处理的网站：");
			System.out.println(url);	
			FileParser.writeStrToFile(urlFilename, url, true);
			readerUrls.add(url);
			if(URLFactory.validateUrl(url)){
				saveContent(parser(url)); //对每个url进行解析，然后存储在文件中。
		
			}				
		}
 		
	}
	
    public void saveUrlContent(String url){
    	if(URLFactory.validateUrl(url)){
//			&&!(new URLFactory(XMLConfig.FILTER_ADVTISE)).isExcludeUrl(url)
			saveContent(parser(url)); //对每个url进行解析，然后存储在文件中。
		
		}
    }
	
	/**
	 * 文件名的命名方式：前缀+[0...]+计数（文件名总长度固定，如果文件名长度不够，中间补0）
	 * @param length 文件名得长度。
	 * @return
	 */
	public String createFilename(){
		String filename=this.PrefixFilename;
		int nameLength=this.PrefixFilename.length()+String.valueOf(this.startCount).length();
		while(nameLength<this.length){
			filename+="0";
			nameLength++;
		}
		return this.savePath+"/"+filename+String.valueOf(this.startCount++)+this.suffix;
	}
	
	/**
	 * 初始化保存文件名称
	 * @param savePath
	 * @param PrefixFilename
	 * @param suffix
	 * @param startCount
	 * @param length
	 */
	public void initFilename(String savePath,String PrefixFilename,String suffix,int startCount,int length){
		this.savePath = savePath;
		this.PrefixFilename = PrefixFilename;
		this.suffix=suffix;
		this.startCount=startCount;
		this.length=length;
	}
	
	/**
	 * 利用搜索引擎，搜索url模块。
	 * @param keyword
	 * @return 
	 */
	public List<String> searchUrl(String keyword){
		return (new Searcher()).parserResult(keyword);
	}
	
	/**
	 * 
	 */
	public void saveContent(String content){
		String filename= this.createFilename();
		FileParser.writeStrToFile(filename, content, false);//以无追加方式写入content
	}
	
	/**
	 * 给定一个url，解析出其中的内容。
	 * @param url
	 * @return
	 */
	public String parser(String url){
		HTMLParser sh = new HTMLParser(url);
		return sh.getContent();
		
	}
	

}
