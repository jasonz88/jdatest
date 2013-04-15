package org.javadynamicanalyzer.tests;

import java.util.ArrayList;

import org.javadynamicanalyzer.graph.Graph;

public class GraphTest {
    // since inner classes cannot have static members
	static Graph<Integer> g=new Graph<Integer>();
    
    public static void constructGraph() {
    	int nodes=50;
    	int edges=50;
    	ArrayList<Integer> n=new ArrayList<Integer>();
    	for(int i=0; i<nodes; i++)
			n.add(new Integer(i));
			
    	for(int i=0; i<edges; i++) {
    		int rand1=(int)(Math.random()*(nodes-1));
    		//int rand2=rand1+1+(int)(Math.random()*(nodes-rand1-2));
    		int rand2=(int)(Math.random()*(nodes-1));
    		g.addEdge(n.get(rand1),n.get(rand2));
    	}
    	
    }

    public static void main(String[] args) {
    	constructGraph();
    	//g.getVisual("Test Graph");
    }   
}