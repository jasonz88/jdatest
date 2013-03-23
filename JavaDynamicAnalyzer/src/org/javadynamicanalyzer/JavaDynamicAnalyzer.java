package org.javadynamicanalyzer;

import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.util.ArrayList;

import javax.swing.JFrame;

import org.javadynamicanalyzer.graph.Graph;
import org.javadynamicanalyzer.graph.Graph.Visualizer;

public class JavaDynamicAnalyzer {
    // since inner classes cannot have static members
	static Graph<Integer> g=new Graph<Integer>();
    
    public static void constructGraph() {
    	int nodes=40;
    	int edges=40;
    	ArrayList<Integer> n=new ArrayList<Integer>();
    	for(int i=0; i<nodes; i++) {
			n.add(new Integer(i));
    	}
    	for(int i=0; i<edges; i++) {
    		int rand1=(int)(Math.random()*(nodes-1));
    		int rand2=(int)(Math.random()*(nodes-1));
    		g.addEdge(n.get(rand1),n.get(rand2));
    	}
    }

    public static void main(String[] args) {
    	constructGraph();
        //g.layout.setSize(new Dimension(500,500)); // sets the initial size of the layout space
        // The BasicVisualizationServer<V,E> is parameterized by the vertex and edge types
        JFrame frame = new JFrame("Simple Graph View");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(g.visual); 
        frame.pack();
        frame.setVisible(true);     
    }   
}