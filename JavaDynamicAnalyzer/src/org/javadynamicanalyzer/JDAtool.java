package org.javadynamicanalyzer;

import java.util.HashMap;
import java.util.Map;

import org.javadynamicanalyzer.timer.BetterLinkedList;
import org.javadynamicanalyzer.timer.TimestampList;

public class JDAtool {
	//Options
	public static boolean verbose=true;
	public static boolean trackBlocks=true;
	public static boolean trackPaths=false;
	public static boolean trackTime=true;
	
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
