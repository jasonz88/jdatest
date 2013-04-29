package org.javadynamicanalyzer;

import java.awt.Container;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

import org.javadynamicanalyzer.timer.BetterLinkedList;
import org.javadynamicanalyzer.timer.TimestampList;

import GUI.BalloonLayoutDemo;

public class JDAtool {
	public static TimestampList tsl=new TimestampList();
	public static String currentMethodName=null;
	
	public static BetterLinkedList<MethodStackEntry> msl=new BetterLinkedList<MethodStackEntry>();
	
	public static Map<String,MethodNode> cfgMap=new HashMap<String,MethodNode>();
	
	public static MethodNode getMethodNode(String methodName){
		if(cfgMap.containsKey(methodName)==false){	
			MethodNode out=new MethodNode(methodName);			
			cfgMap.put(methodName, out);
			return out;
		}
		else{			
			return cfgMap.get(methodName);
		}
	}
	public static MethodStackEntry getLastMSE(){ return msl.getLast(); }
	public static void methodStackPop(){ msl.pop(); }
	public static void gui(){
		for(String key : cfgMap.keySet()){
			MethodNode mn=cfgMap.get(key);
			mn.getVisual(mn.getName()+mn.getMeanTime());
		}
	}
}
