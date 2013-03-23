package org.javadynamicanalyzer.graph;

public class Edge<T> {
	T src;
	T dst;
	public Edge(T source, T destination){
		src=source;
		dst=destination;
	}
	public boolean equals(Object o){
		if( !(o instanceof Edge) ) return false;
		Edge e= (Edge)o;
		//nulls serve as wild Node connections
		return (e.src.equals(src) || src==null || e.src==null) && 
			   (e.dst.equals(dst) || dst==null || e.dst==null);
	}
}
