package org.zju.zhzhl.searcher;

import java.util.HashMap;
import java.util.Map;

/**
 *@author 张知临 zhzhl202@163.com
 *@version 创建时间:2012-4-5上午09:00:09
 *类说明
 */
public class Test {
	
	public static void main(String []args){
		Map<String, String> map = new HashMap<String,String>();
		map.put("hello", null);
		map.put("world", null);
		for(String s:map.keySet())
			System.out.println(s);
	}

}
