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
		if(path!=null) //aka JDAtools.trackPaths==true
			path.add(i);
	}
	public void concludePath(){
		if(path!=null) //aka JDAtools.trackPaths==true
			mn.addPath(path);
	}
	public void concludePath(long dt){
		if(path!=null){ //aka JDAtools.trackPaths==true
			path.addTime(dt);
			mn.addPath(path); //adds it to the method node time as well
		}
		else
			mn.addTime(dt);
	}
}
