package org.javadynamicanalyzer;

import java.util.ArrayList;
import java.util.Collection;

//BasicBlockPath can't be an inner class of MethodNode because it will fuck up Javassist.
public class BasicBlockPath {
	Collection<Integer> blist=new ArrayList<Integer>();
	long ttlTime=0;
	long ttlTraversals=0;
	
	public boolean equals(Object o){ return blist.equals(o); }
	
	public void addBlock(int blockID) { blist.add(blockID); }
	public void addTime(long l){ ttlTime+=l; ++ttlTraversals; }
	public double getMeanTime() { 
		if(ttlTraversals==0) return 0;
		return (double)ttlTime/ttlTraversals; 
	}
	public long getTraversals(){ return ttlTraversals; }
	public long getTotalTime(){ return ttlTime; }
	public Collection<Integer> getPath(){ return blist;}
}
