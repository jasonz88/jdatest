package org.javadynamicanalyzer.graph;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Graph<T> implements Iterable<Node<T>>{
	Set<Edge<T>> edges;
	Set<Node<T>> nodes;
	
	Graph(){
		edges=(Set<Edge<T>>) new HashSet<Edge<T>>();
		nodes=(Set<Node<T>>) new HashSet<Node<T>>();
	}
	
	@Override
	public Iterator<Node<T>> iterator(){ return nodeIterator(); }
	public Iterator<Node<T>> nodeIterator(){ return nodes.iterator(); }
	public Iterator<Edge<T>> edgeIterator() { return edges.iterator();}	
}
