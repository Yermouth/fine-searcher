package org.zju.zhzhl.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * 读文本文件，并将其中的内容写入到字符串中去
 * @author 张知临
 *
 */
public class FileParser {
	
	public static void main(String args[]){
		List<String> list = new ArrayList<String>();
		list.add("1");
		list.add("2");
		for(int i = 0;i<10;i++){
			FileParser.writeToFile("conf/urls.txt", list, true);
		}
		
	}
    
	//存储文件内容
	private String content;
	//读取的文件名称
	private String inFileName;
	public FileParser(String inFilename){
		content=new String();
		this.setInFileName(inFilename);
	}
	public String ReadFiletoString(){
		try{
			
			File f=new File(inFileName);
			BufferedReader bReader =new BufferedReader (new FileReader(f));
			String str;
			while((str=bReader.readLine())!=null){
				content+=str;
			}
			bReader.close();
			return content;
		}catch(IOException e){
			e.printStackTrace();
			
			return null;
		}
		
		
	}
	
	public  static ArrayList<String> readFileToList(String fileName){
		ArrayList<String> aList=new ArrayList<String>();
		try{
			File f=new File(fileName);
			BufferedReader bReader= new BufferedReader(new FileReader(f));
			String str;
			while((str=bReader.readLine())!=null){
				aList.add(str);
			}
			
		}catch(IOException e){
			e.printStackTrace();
		}
		return aList;
		
	}
	
	public static void  writeMutliToFile(String filename ,List<List<String>> source){
		PrintWriter pw= null;
		try {
			pw = new PrintWriter (filename);
			for(List<String> s :source){
				writeToFile(pw,s);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(pw!=null)
				pw.close();
		}
		
	}
	
	public static void  writeToFile(PrintWriter pw,List<String> source){
		
		for(String s:source){
			pw.print(s+"\t");
		}
		pw.println();
		pw.flush();
	}
	
	public static void  writeToFile(String filename,List<String> source,boolean append){
		
		PrintWriter pw= null;
		try {
			try {
				pw =new PrintWriter(new FileWriter(new File(filename), append)) ;
				for(String s :source){
					pw.println(s);
					pw.flush();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
			
		}finally{
			if(pw!=null)
				pw.close();
		}
	}
	
	public static void  writeStrToFile(String filename,String source,boolean append){
		
		PrintWriter pw= null;
		try {
			try {
				pw =new PrintWriter(new FileWriter(new File(filename), append)) ;
					pw.println(source);
					pw.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
						
		}finally{
			if(pw!=null)
				pw.close();
		}
	}
	
	public String getContent() {
		return content;
	}
	
	public void setInFileName(String inFileName) {
		this.inFileName = inFileName;
	}
	
}

