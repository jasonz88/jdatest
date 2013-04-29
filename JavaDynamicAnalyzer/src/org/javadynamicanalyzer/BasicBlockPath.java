package org.javadynamicanalyzer;

import java.util.ArrayList;

//BasicBlockPath can't be an inner class of MethodNode because it will fuck up Javassist.
public class BasicBlockPath extends ArrayList<Integer> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//ArrayList<Integer> blist=new ArrayList<Integer>();
	long ttlTime=0;
	long ttlTraversals=0;
	
	//public void addBlock(int blockID) { blist.add(blockID); }
	public void addTime(long l){ ttlTime+=l; ++ttlTraversals; }
	public double getMeanTime() { 
		if(ttlTraversals==0) return 0;
		return (double)ttlTime/ttlTraversals; 
	}
	public long getTraversals(){ return ttlTraversals; }
	public long getTotalTime(){ return ttlTime; }
	//public List<Integer> getPath(){ return blist; }
	
	//public boolean equals(BasicBlockPath bbp){ return blist.equals(bbp.blist); }
}
