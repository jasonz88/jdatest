package org.javadynamicanalyzer;

import org.javadynamicanalyzer.graph.MethodNode;

//MethodStackEntry can't be an inner class of JDAtool because it will fuck up Javassist.
public class MethodStackEntry {
	final public MethodNode mn;
	public int blockIndex=-1;
	
	public MethodStackEntry(String name){
		mn=JDAtool.getMethodNode(name);
		JDAtool.msl.add(this); 
	}
	public void setBlockIndex(int i){
		System.out.println(mn.getName()+".BlockIndex="+i);
		blockIndex=i; 
	}
}
