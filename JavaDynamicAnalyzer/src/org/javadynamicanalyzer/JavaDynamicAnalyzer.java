package org.javadynamicanalyzer;

import java.util.ArrayList;

import javax.swing.JFrame;

import org.javadynamicanalyzer.graph.Graph;

public class JavaDynamicAnalyzer {
    // since inner classes cannot have static members
	static Graph<Integer> g=new Graph<Integer>();
    
    public static void constructGraph() {
    	int nodes=30;
    	int edges=30;
    	ArrayList<Integer> n=new ArrayList<Integer>();
    	for(int i=0; i<nodes; i++) {
			n.add(new Integer(i));
    	}
    	/*
    	for(int i=0; i<nodes-1; i++){
    		g.addEdge(n.get(i),n.get(i+1));
    	}
    	*/
    	for(int i=0; i<edges; i++) {
    		int rand1=(int)(Math.random()*(nodes-1));
    		int rand2=(int)(Math.random()*(nodes-1));
    		g.addEdge(n.get(rand1),n.get(rand2));
    	}
    	
    }

    public static void main(String[] args) {
    	constructGraph();
        JFrame frame = new JFrame("Simple Graph View");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(g.visual); 
        frame.pack();
        frame.setVisible(true);     
    }   
}