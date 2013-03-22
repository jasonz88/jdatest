package org.javadynamicanalyzer.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Graph<T> implements Iterable<Node<T>>{
	Set<Node<T>> rootNodes;
	Set<Node<T>> nodes;
	
	Graph(){
		rootNodes=(Set<Node<T>>) new HashSet<Node<T>>();
		nodes=(Set<Node<T>>) new HashSet<Node<T>>();
	}
	
	@Override
	public Iterator<Node<T>> iterator(){ return nodes.iterator(); }
	public Iterator<Node<T>> nodeIterator(){ return iterator(); }
	public Iterator<Edge<T>> edgeIterator() {
		if(rootNodes.size()==0) return null; //no edges!
		
        Iterator<Edge<T>> it = new Iterator<Edge<T>>() {
        	
        	Set<Node<T>> visited=new HashSet<Node<T>>();
        	Iterator<Node<T>> nItr=nodeIterator();
            Iterator<Edge<T>> eItr=nItr.next().iterator();

            @Override
            public boolean hasNext() {
            	if(eItr.hasNext()) return true; //there's another edge next!
            	if(nItr.hasNext()) return true; //no more edges within this node, but there are mode nodes!
                return false; //all edges explored
            }

            @Override
            public Edge<T> next() {
            	if
            }

            @Override
            public void remove() {
                // TODO Auto-generated method stub
            }
        };	
		return it;
	}	
}
