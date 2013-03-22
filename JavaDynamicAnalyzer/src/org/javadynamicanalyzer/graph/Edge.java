package org.javadynamicanalyzer.graph;

public class Edge<T> {
	Node<T> src;
	Node<T> dst;
	public Edge(Node<T> source, Node<T> destination){
		src=source;
		dst=destination;
	}
	@SuppressWarnings("unchecked")
	public boolean equals(Object o){
		if( !(o instanceof Edge) ) return false;
		Edge<T> e= (Edge<T>)o;
		//nulls serve as wild Node connections
		return (e.src==src || src==null || e.src==null) && 
			   (e.dst==dst || dst==null || e.dst==null);
	}
}
