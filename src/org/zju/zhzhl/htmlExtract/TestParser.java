package org.zju.zhzhl.htmlExtract;
import org.htmlparser.Parser;
import org.htmlparser.beans.StringBean;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.HtmlPage;
/**
 * 改程序仅仅为一个测试程序，用以测试htmlparser 的使用方法
 * @author 张知临
 *
 */
public class TestParser {

	public static void main(String []args) throws ParserException{
		String url="http://news.163.com/11/1222/19/7LTCAONR00014JB6.html"; 
		Parser parser;
		 HtmlPage hPage;
		 StringBean sBean;
		 String content;
		parser=new Parser(url);
		hPage = new HtmlPage(parser);
		sBean=new StringBean(); 
		sBean.setReplaceNonBreakingSpaces (true);
		sBean.setCollapse (true);
		sBean.setURL(url);
		content=sBean.getStrings();
		parser.visitAllNodesWith(hPage);
		
		System.out.println("the title of this page is:"+parser.getURL());
		System.out.println("the content of this page is:"+content);
	}
  	
}

