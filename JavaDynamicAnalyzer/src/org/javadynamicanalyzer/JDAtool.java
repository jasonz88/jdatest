package org.javadynamicanalyzer;

import java.util.HashMap;
import java.util.Map;

import javassist.bytecode.analysis.ControlFlow.Block;

import org.javadynamicanalyzer.graph.Graph;
import org.javadynamicanalyzer.timer.TimestampList;

public class JDAtool {
	public static TimestampList tsl=new TimestampList();
	static Map<String,Graph<Block>> cfgMap=new HashMap<String,Graph<Block>>();
	
	public static Graph<Block> getGraph(String methodName){
		if(cfgMap.containsKey(methodName)==false){	
			Graph<Block> out=new Graph<Block>();			
			cfgMap.put(methodName, out);
			return out;
		}
		else{			
			return cfgMap.get(methodName);
		}
	}
}
