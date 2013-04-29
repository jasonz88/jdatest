package org.javadynamicanalyzer;


//MethodStackEntry can't be an inner class of JDAtool because it will fuck up Javassist.
public class MethodStackEntry {
	final public MethodNode mn;
	public int blockIndex=-1;
	BasicBlockPath path=null;
	
	public MethodStackEntry(String name){
		mn=JDAtool.getMethodNode(name);
		JDAtool.msl.add(this);
		if(JDAtool.trackPaths)
			path=new BasicBlockPath();
	}
	public void setBlockIndex(int i){
		blockIndex=i;
		if(path!=null) 
			path.addBlock(i);
	}
	public void concludePath(){
		mn.addPath(path);
	}
}
