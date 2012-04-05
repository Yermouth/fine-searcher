package org.zju.zhzhl.parser;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.nodes.*;
import org.htmlparser.tags.*;
import org.htmlparser.util.*;
import org.zju.zhzhl.htmlExtract.ChannelLinkDO;
import org.zju.zhzhl.htmlExtract.PageContext;
import org.zju.zhzhl.htmlExtract.PageEncodeDector;
import org.zju.zhzhl.htmlExtract.TableColumnValid;
import org.zju.zhzhl.htmlExtract.TableValid;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class HTMLParser {
	 protected static final String lineSign = System.getProperty(
     "line.separator");
     protected static final int lineSign_size = lineSign.length();
     private ChannelLinkDO c;
     private String content;
     private String url;


     public static void main(String[] args) throws Exception {
    	 HTMLParser console = new HTMLParser("http://news.163.com/11/1222/18/7LT8Q0LR0001124J.html");
//         ChannelLinkDO c = new ChannelLinkDO();
//         console.makeContext(console.getChannel());
    	 System.out.println(console.gettitle());
    	 System.out.println("-----------------------------------");
    	 System.out.println(console.getContent());
     }
     
     public HTMLParser(String url) {
    	 try{
    		 this.url=url;
    	     c = new ChannelLinkDO();
             c.setEncode(new PageEncodeDector().getCharset(url));
             c.setLink(url);
             c.setLinktext("test");  
             this.makeContext(c);
    	 }catch (ParserException e) {
 			e.printStackTrace();
 		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	 
     }
     
     public ChannelLinkDO getChannel(){
    	 return c;
     }
     
     public String getUrl(){
    	 return this.url;
     }
     
     public String getContent(){
    	 return this.content;
     }
     
     public String gettitle(){
    	 
    	 return "";
     }
     
     /**
      * 
      * @param c
      * @throws Exception
      */
     public void makeContext(ChannelLinkDO c) throws Exception {
         //这里的解决方式是用正则表达式来替换原有的标记TAG    "<[^>]+>"
       
             String siteUrl = getLinkUrl(c.getLink());
             Parser parser = new Parser(c.getLink());
             parser.setEncoding(c.getEncode());
    
             for (NodeIterator e = parser.elements(); e.hasMoreNodes();) {
                 Node node = (Node) e.nextNode();

                 if (node instanceof Html) {
                     PageContext context = new PageContext();
                     context.setNumber(0);
                     context.setTextBuffer(new StringBuffer());
                     //抓取出内容
                     extractHtml(node, context, siteUrl);

                     String lineContext = context.getTextBuffer().toString();
                     this.content=lineContext.replaceAll("<[^>]+>", "\n");                    
                }
             }        
     }

     /**
      * 
      * @param link
      * @return
      */
     private String getLinkUrl(String link) {
         String urlDomaiPattern = "(http://[^/]*?" + "/)(.*?)";

         Pattern pattern = Pattern.compile(urlDomaiPattern,
                 Pattern.CASE_INSENSITIVE + Pattern.DOTALL);
         Matcher matcher = pattern.matcher(link);
         String url = "";

         while (matcher.find()) {
             int start = matcher.start(1);
             int end = matcher.end(1);

             url = link.substring(start, end - 1).trim();
         }

         return url;
     }

     /**
     * 递归钻取正文信息
     * @param nodeP
     * @return
     */
     protected List extractHtml(Node nodeP, PageContext context, String siteUrl)
     throws Exception {
     NodeList nodeList = nodeP.getChildren();
     boolean bl = false;

     if ((nodeList == null) || (nodeList.size() == 0)) {
         if (nodeP instanceof ParagraphTag) {
             ArrayList tableList = new ArrayList();
             StringBuffer temp = new StringBuffer();
             temp.append("<p style=\"TEXT-INDENT: 2em\">");
             tableList.add(temp);
             temp = new StringBuffer();
             temp.append("</p>").append(lineSign);
             tableList.add(temp);

             return tableList;
         }

         return null;
     }

     if ((nodeP instanceof TableTag) || (nodeP instanceof Div)) {
         bl = true;
     }

     if (nodeP instanceof ParagraphTag) {
         ArrayList tableList = new ArrayList();
         StringBuffer temp = new StringBuffer();
         temp.append("<p style=\"TEXT-INDENT: 2em\">");
         tableList.add(temp);
         extractParagraph(nodeP, siteUrl, tableList);

         temp = new StringBuffer();
         temp.append("</p>").append(lineSign);

         tableList.add(temp);

         return tableList;
     }

     ArrayList tableList = new ArrayList();

     try {
         for (NodeIterator e = nodeList.elements(); e.hasMoreNodes();) {
             Node node = (Node) e.nextNode();

             if (node instanceof LinkTag) {
                 tableList.add(node);
                 setLinkImg(node, siteUrl);
             } else if (node instanceof ImageTag) {
                 ImageTag img = (ImageTag) node;

                 if (img.getImageURL().toLowerCase().indexOf("http://") < 0) {
                     img.setImageURL(siteUrl + img.getImageURL());
                 } else {
                     img.setImageURL(img.getImageURL());
                 }

                 tableList.add(node);
             } else if (node instanceof ScriptTag ||
                     node instanceof StyleTag || node instanceof SelectTag) {
             } else if (node instanceof TextNode) {
                 if (node.getText().length() > 0) {
                     StringBuffer temp = new StringBuffer();
                     String text = collapse(node.getText()
                                                .replaceAll("&nbsp;", "")
                                                .replaceAll("　", ""));

                     temp.append(text.trim());

                     tableList.add(temp);
                 }
             } else {
                 if (node instanceof TableTag || node instanceof Div) {
                     TableValid tableValid = new TableValid();
                     isValidTable(node, tableValid);

                     if (tableValid.getTrnum() > 2) {
                         tableList.add(node);

                         continue;
                     }
                 }

                 List tempList = extractHtml(node, context, siteUrl);

                 if ((tempList != null) && (tempList.size() > 0)) {
                     Iterator ti = tempList.iterator();

                     while (ti.hasNext()) {
                         tableList.add(ti.next());
                     }
                 }
             }
         }
     } catch (Exception e) {
         return null;
     }

     if ((tableList != null) && (tableList.size() > 0)) {
         if (bl) {
             StringBuffer temp = new StringBuffer();
             Iterator ti = tableList.iterator();
             int wordSize = 0;
             StringBuffer node;
             int status = 0;
             StringBuffer lineStart = new StringBuffer(
                     "<p style=\"TEXT-INDENT: 2em\">");
             StringBuffer lineEnd = new StringBuffer("</p>" + lineSign);

             while (ti.hasNext()) {
                 Object k = ti.next();

                 if (k instanceof LinkTag) {
                     if (status == 0) {
                         temp.append(lineStart);
                         status = 1;
                     }

                     node = new StringBuffer(((LinkTag) k).toHtml());
                     temp.append(node);
                 } else if (k instanceof ImageTag) {
                     if (status == 0) {
                         temp.append(lineStart);
                         status = 1;
                     }

                     node = new StringBuffer(((ImageTag) k).toHtml());
                     temp.append(node);
                 } else if (k instanceof TableTag) {
                     if (status == 0) {
                         temp.append(lineStart);
                         status = 1;
                     }

                     node = new StringBuffer(((TableTag) k).toHtml());
                     temp.append(node);
                 } else if (k instanceof Div) {
                     if (status == 0) {
                         temp.append(lineStart);
                         status = 1;
                     }

                     node = new StringBuffer(((Div) k).toHtml());
                     temp.append(node);
                 } else {
                     node = (StringBuffer) k;

                     if (status == 0) {
                         if (node.indexOf("<p") < 0) {
                             temp.append(lineStart);
                             temp.append(node);
                             wordSize = wordSize + node.length();
                             status = 1;
                         } else {
                             temp.append(node);
                             status = 1;
                         }
                     } else if (status == 1) {
                         if (node.indexOf("</p") < 0) {
                             if (node.indexOf("<p") < 0) {
                                 temp.append(node);
                                 wordSize = wordSize + node.length();
                             } else {
                                 temp.append(lineEnd);
                                 temp.append(node);
                                 status = 1;
                             }
                         } else {
                             temp.append(node);
                             status = 0;
                         }
                     }
                 }
             }

             if (status == 1) {
                 temp.append(lineEnd);
             }

             if (wordSize > context.getNumber()) {
                 context.setNumber(wordSize);
                 context.setTextBuffer(temp);
             }

             return null;
         } else {
             return tableList;
         }
     }

     return null;
 }

     /**
     * 设置图象连接
     * @param nodeP
     * @param siteUrl
     */
     private void setLinkImg(Node nodeP, String siteUrl) {
         NodeList nodeList = nodeP.getChildren();

         try {
             for (NodeIterator e = nodeList.elements(); e.hasMoreNodes();) {
                 Node node = (Node) e.nextNode();

                 if (node instanceof ImageTag) {
                     ImageTag img = (ImageTag) node;

                     if (img.getImageURL().toLowerCase().indexOf("http://") < 0) {
                         img.setImageURL(siteUrl + img.getImageURL());
                     } else {
                         img.setImageURL(img.getImageURL());
                     }
                 }
             }
         } catch (Exception e) {
             return;
         }

         return;
     }

     /**
     * 钻取段落中的内容
     * @param nodeP
     * @param siteUrl
     * @param tableList
     * @return
     */
     private List extractParagraph(Node nodeP, String siteUrl, List tableList) {
         NodeList nodeList = nodeP.getChildren();

         if ((nodeList == null) || (nodeList.size() == 0)) {
             if (nodeP instanceof ParagraphTag) {
                 StringBuffer temp = new StringBuffer();
                 temp.append("<p style=\"TEXT-INDENT: 2em\">");
                 tableList.add(temp);
                 temp = new StringBuffer();
                 temp.append("</p>").append(lineSign);
                 tableList.add(temp);

                 return tableList;
             }

             return null;
         }

         try {
             for (NodeIterator e = nodeList.elements(); e.hasMoreNodes();) {
                 Node node = (Node) e.nextNode();

                 if (node instanceof ScriptTag || node instanceof StyleTag ||
                         node instanceof SelectTag) {
                 } else if (node instanceof LinkTag) {
                     tableList.add(node);
                     setLinkImg(node, siteUrl);
                 } else if (node instanceof ImageTag) {
                     ImageTag img = (ImageTag) node;

                     if (img.getImageURL().toLowerCase().indexOf("http://") < 0) {
                         img.setImageURL(siteUrl + img.getImageURL());
                     } else {
                         img.setImageURL(img.getImageURL());
                     }

                     tableList.add(node);
                 } else if (node instanceof TextNode) {
                     if (node.getText().trim().length() > 0) {
                         String text = collapse(node.getText()
                                                    .replaceAll("&nbsp;", "")
                                                    .replaceAll("　", ""));
                         StringBuffer temp = new StringBuffer();
                         temp.append(text);
                         tableList.add(temp);
                     }
                 } else if (node instanceof Span) {
                     StringBuffer spanWord = new StringBuffer();
                     getSpanWord(node, spanWord);

                     if ((spanWord != null) && (spanWord.length() > 0)) {
                         String text = collapse(spanWord.toString()
                                                        .replaceAll("&nbsp;", "")
                                                        .replaceAll("　", ""));

                         StringBuffer temp = new StringBuffer();
                         temp.append(text);
                         tableList.add(temp);
                     }
                 } else if (node instanceof TagNode) {
                     String tag = node.toHtml();

                     if (tag.length() <= 10) {
                         tag = tag.toLowerCase();

                         if ((tag.indexOf("strong") >= 0) ||
                                 (tag.indexOf("b") >= 0)) {
                             StringBuffer temp = new StringBuffer();
                             temp.append(tag);
                             tableList.add(temp);
                         }
                     } else {
                         if (node instanceof TableTag || node instanceof Div) {
                             TableValid tableValid = new TableValid();
                             isValidTable(node, tableValid);

                             if (tableValid.getTrnum() > 2) {
                                 tableList.add(node);

                                 continue;
                             }
                         }

                         extractParagraph(node, siteUrl, tableList);
                     }
                 }
             }
         } catch (Exception e) {
             return null;
         }

         return tableList;
     }

     protected void getSpanWord(Node nodeP, StringBuffer spanWord) {
         NodeList nodeList = nodeP.getChildren();

         try {
             for (NodeIterator e = nodeList.elements(); e.hasMoreNodes();) {
                 Node node = (Node) e.nextNode();

                 if (node instanceof ScriptTag || node instanceof StyleTag ||
                         node instanceof SelectTag) {
                 } else if (node instanceof TextNode) {
                     spanWord.append(node.getText());
                 } else if (node instanceof Span) {
                     getSpanWord(node, spanWord);
                 } else if (node instanceof ParagraphTag) {
                     getSpanWord(node, spanWord);
                 } else if (node instanceof TagNode) {
                     String tag = node.toHtml().toLowerCase();

                     if (tag.length() <= 10) {
                         if ((tag.indexOf("strong") >= 0) ||
                                 (tag.indexOf("b") >= 0)) {
                             spanWord.append(tag);
                         }
                     }
                 }
             }
         } catch (Exception e) {
         }

         return;
     }

     /**
     * 判断TABLE是否是表单
     * @param nodeP
     * @return
     */
     private void isValidTable(Node nodeP, TableValid tableValid) {
         NodeList nodeList = nodeP.getChildren();

         /**如果该表单没有子节点则返回**/
         if ((nodeList == null) || (nodeList.size() == 0)) {
             return;
         }

         try {
             for (NodeIterator e = nodeList.elements(); e.hasMoreNodes();) {
                 Node node = (Node) e.nextNode();

                 /**如果子节点本身也是表单则返回**/
                 if (node instanceof TableTag || node instanceof Div) {
                     return;
                 } else if (node instanceof ScriptTag ||
                         node instanceof StyleTag || node instanceof SelectTag) {
                     return;
                 } else if (node instanceof TableColumn) {
                     return;
                 } else if (node instanceof TableRow) {
                     TableColumnValid tcValid = new TableColumnValid();
                     tcValid.setValid(true);
                     findTD(node, tcValid);

                     if (tcValid.isValid()) {
                         if (tcValid.getTdNum() < 2) {
                             if (tableValid.getTdnum() > 0) {
                                 return;
                             } else {
                                 continue;
                             }
                         } else {
                             if (tableValid.getTdnum() == 0) {
                                 tableValid.setTdnum(tcValid.getTdNum());
                                 tableValid.setTrnum(tableValid.getTrnum() + 1);
                             } else {
                                 if (tableValid.getTdnum() == tcValid.getTdNum()) {
                                     tableValid.setTrnum(tableValid.getTrnum() +
                                         1);
                                 } else {
                                     return;
                                 }
                             }
                         }
                     }
                 } else {
                     isValidTable(node, tableValid);
                 }
             }
         } catch (Exception e) {
             return;
         }

         return;
     }

     /**
     * 判断是否有效TR
     * @param nodeP
     * @param TcValid
     * @return
     */
     private void findTD(Node nodeP, TableColumnValid tcValid) {
         NodeList nodeList = nodeP.getChildren();

         /**如果该表单没有子节点则返回**/
         if ((nodeList == null) || (nodeList.size() == 0)) {
             return;
         }

         try {
             for (NodeIterator e = nodeList.elements(); e.hasMoreNodes();) {
                 Node node = (Node) e.nextNode();

                 /**如果有嵌套表单**/
                 if (node instanceof TableTag || node instanceof Div ||
                         node instanceof TableRow ||
                         node instanceof TableHeader) {
                     tcValid.setValid(false);

                     return;
                 } else if (node instanceof ScriptTag ||
                         node instanceof StyleTag || node instanceof SelectTag) {
                     tcValid.setValid(false);

                     return;
                 } else if (node instanceof TableColumn) {
                     tcValid.setTdNum(tcValid.getTdNum() + 1);
                 } else {
                     findTD(node, tcValid);
                 }
             }
         } catch (Exception e) {
             tcValid.setValid(false);

             return;
         }

         return;
     }

     protected String collapse(String string) {
         int chars;
         int length;
         int state;
         char character;
         StringBuffer buffer = new StringBuffer();
         chars = string.length();

         if (0 != chars) {
             length = buffer.length();
             state = ((0 == length) || (buffer.charAt(length - 1) == ' ') ||
                 ((lineSign_size <= length) &&
                 buffer.substring(length - lineSign_size, length).equals(lineSign)))
                 ? 0 : 1;

             for (int i = 0; i < chars; i++) {
                 character = string.charAt(i);

                 switch (character) {
                 case '\u0020':
                 case '\u0009':
                 case '\u000C':
                 case '\u200B':
                 case '\u00a0':
                 case '\r':
                 case '\n':

                     if (0 != state) {
                         state = 1;
                     }

                     break;

                 default:

                     if (1 == state) {
                         buffer.append(' ');
                     }

                     state = 2;
                     buffer.append(character);
                 }
             }
         }

         return buffer.toString();
       }
}
