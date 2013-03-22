package org.javadynamicanalyzer.graph;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Node<T> implements Iterable<Edge<T>> {
	T ref;
	Set<Edge<T>> out;
	Set<Edge<T>> in;
	
	public Node(T object){
		ref=object;
		out=new HashSet<Edge<T>>();
		in=new HashSet<Edge<T>>();
	}
	
	public boolean equals(Object o){
		if( !(o instanceof Node<?>) ) return false; //not even a node...
		Node n = (Node) o;
		if(n.getRef().getClass()!=ref.getClass()) return false; //not the same T
		return ref.equals(n.ref); //use T.equals 
	}
	public T getRef(){ return ref; }

	@Override
	public Iterator<Edge<T>> iterator() {
		return out.iterator();
	}
}
