package org.javadynamicanalyzer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javassist.bytecode.analysis.ControlFlow.Block;

import org.javadynamicanalyzer.gui.GUIclass;

public class MethodNode {
	public class ExternalLink{ 
		public int blockIndex=-1;
		public MethodNode target=null;
		public int hashCode(){ return blockIndex+target.hashCode(); }
		public boolean equals(Object o){
			if(o instanceof ExternalLink) 
				return this.equals((ExternalLink)o);
			return false;
		}
		public boolean equals(ExternalLink el){ return blockIndex==el.blockIndex && target.equals(el.target); }
	}
	public class BasicBlock {
		Block b;
		public String toString(){ 
			String out=Integer.toString(b.index());
			Set<ExternalLink> outgoing=getExternalLinks();
			if(outgoing.size()>0){
				out+=":";
				for(ExternalLink el : outgoing)
					out+=", "+el.target.getName();
			}
			return out;
		}
		BasicBlock(Block b){ this.b=b; } 
		public int index(){ return b.index(); }
		public String getMethodName(){ return getName(); }
		public boolean equals(Object o){ return b.equals(o); }
		public boolean equals(BasicBlock that){ return b==that.b; } 
		public boolean equals(int i){ return index()==i; }
		public boolean equals(Integer i){ return i.equals(index()); }
		public Set<ExternalLink> getExternalLinks(){ return MethodNode.this.getExternalLinks(this); }
		public Set<BasicBlockPath> getPaths() { return MethodNode.this.getPaths(this.index()); }
		public int hashCode(){ return b.hashCode(); }
		public MethodNode getMethodNode(){return getThis();}
	}
	
	Set<ExternalLink> eLinks=new HashSet<ExternalLink>();
	Map<Block,BasicBlock> B2BBmap=new HashMap<Block,BasicBlock>();
	GUIclass<BasicBlock> cfg=new GUIclass<BasicBlock>();
	Set<BasicBlockPath> plist=new HashSet<BasicBlockPath>();
	
	long ttlTime=0;
	long ttlTraversals=0;
	
	public MethodNode(String name){ cfg.setName(name); }
	public MethodNode()			  { this("");   }
	
	public void addPath(BasicBlockPath bbp){ 
		if(plist.contains(bbp)==false){
			plist.add(bbp);
		}
		else{
			Iterator<BasicBlockPath> itr=plist.iterator();
			while(itr.hasNext()){
				BasicBlockPath plistBBP=itr.next();
				if(plistBBP.equals(bbp)){
					plistBBP.addTime(bbp.getTotalTime());
					break;
				}
			}
		}
		
		//update the method's timing
		addTime(bbp.getTotalTime());
	}
	public void addTime(long dt){
		ttlTime+=dt;
		++ttlTraversals;
	}
	public double getMeanTime() { 
		if(ttlTraversals==0) return 0;
		return (double)ttlTime/ttlTraversals; 
	}
	
	public BasicBlock getBasicBlock(Block b){
		if(B2BBmap.containsKey(b)) return B2BBmap.get(b);
		BasicBlock out=new BasicBlock(b);
		B2BBmap.put(b, out);
		return out;
	}
	
	public void addEdge(Block a, Block b){
		cfg.addEdge(getBasicBlock(a), getBasicBlock(b));
	}
	public void addNode(Block a){ cfg.addNode(getBasicBlock(a)); }
	public void addLink(int blockIndex, MethodNode target){
		ExternalLink elink=new ExternalLink();
		elink.blockIndex=blockIndex;
		elink.target=target;
		eLinks.add(elink);
	}
	
	public Set<ExternalLink> getExternalLinks(){ return eLinks; }
	public Set<ExternalLink> getExternalLinks(int BasicBlockID){
		Set<ExternalLink> out=new HashSet<ExternalLink>();
		for(ExternalLink el : eLinks)
			if(BasicBlockID==el.blockIndex)
				out.add(el);
		return out;
	}
	public Set<ExternalLink> getExternalLinks(BasicBlock b){ return getExternalLinks(b.index()); }
	public Set<ExternalLink> getExternalLinks(Block b){ return getExternalLinks(getBasicBlock(b)); }
	
	public Set<BasicBlockPath> getPaths(){ return plist; }
	public Set<BasicBlockPath> getPaths(int BasicBlockID){
		Set<BasicBlockPath> out=new HashSet<BasicBlockPath>();
		for(BasicBlockPath bbp : plist)
			if(bbp.contains(BasicBlockID))
				out.add(bbp);
		return out;
	}
	public Set<BasicBlockPath> getPaths(BasicBlock bb){ return getPaths(bb.index()); }
	
	public boolean equals(MethodNode mn){ return mn.cfg.getName().equals(cfg.getName()); }
	public int hashCode(){ return cfg.getName().hashCode(); }
	public String toString(){ return cfg.getName(); }
	public MethodNode getThis(){ return this;}
	
	//DELEGATE METHODS FROM GRAPH<T>
	/**
	 * @param name
	 * @see org.javadynamicanalyzer.graph.Graph#setName(java.lang.String)
	 */
	public void setName(String name) {
		cfg.setName(name);
	}
	/**
	 * @return
	 * @see org.javadynamicanalyzer.graph.Graph#getName()
	 */
	public String getName() {
		return cfg.getName();
	}
	/**
	 * @param name
	 * @see org.javadynamicanalyzer.graph.Graph#getVisual(java.lang.String)
	 */
	public void getVisual(String name) {
		cfg.getVisual(name);
	}
	/**
	 * 
	 * @see org.javadynamicanalyzer.graph.Graph#getVisual()
	 */
	public void getVisual() {
		cfg.getVisual();
	}
}
