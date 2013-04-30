package org.javadynamicanalyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Parser extends ArrayList<String>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7909354614959430967L;

	Map<String,Integer> vals=new HashMap<String,Integer>();
	
	@Override
	public boolean add(String s){
		if(s.contains("=")){
			int eqIndex=s.indexOf("=");
			String key=s.substring(0, eqIndex);
			Integer value=Integer.decode(s.substring(eqIndex+1));
			vals.put(key, value);
			return false;
		}
		else
			return super.add(s);
	}
	
	public Integer get(String prop){ 
		Integer out=vals.get(prop); 
		if(out==null) return -1;
		else return out;
	}
	
	public void parse(String str){
		clear();
		if(str==null) return;
		if(str.charAt(0)!='-') return;
		
		str=str.substring(1);
		while(str.contains("-")){
			int endIndex=str.indexOf('-');
			add(str.substring(0, endIndex));
			str=str.substring(endIndex+1);
		}
		add(str);
	}
}
