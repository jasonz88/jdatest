package org.javadynamicanalyzer.graph;

public class Edge<T> {
	public T src;
	public T dst;
	public Edge(T source, T destination){
		src=source;
		dst=destination;
	}
	public boolean equals(Object o){
		if( !(o instanceof Edge) ) return false;
		@SuppressWarnings("unchecked")
		Edge<T> e = (Edge<T>)o;
		//nulls serve as wild Node connections
		return (e.src.equals(src) || src==null || e.src==null) && 
			   (e.dst.equals(dst) || dst==null || e.dst==null);
	}
	public int hashCode(){ return src.hashCode() + dst.hashCode(); }
	public String toString(){ return src.toString() + " -> " + dst.toString(); }
	
	public T getSource(){ return src; }
	public T getSrc(){ return src; }
	public T getDestination(){ return dst; }
	public T getDst(){ return dst; }
}