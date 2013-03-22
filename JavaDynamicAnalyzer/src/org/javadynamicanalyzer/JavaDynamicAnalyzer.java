package org.javadynamicanalyzer;

import org.jgrapht.DirectedGraph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class JavaDynamicAnalyzer {
    public static void main(String [] args){
        UndirectedGraph<String, DefaultEdge> stringGraph = createStringGraph();

        // note undirected edges are printed as: {<v1>,<v2>}
        System.out.println(stringGraph.toString());

        // create a graph based on URL objects
        DirectedGraph<String, DefaultEdge> hrefGraph = createHrefGraph();

        // note directed edges are printed as: (<v1>,<v2>)
        System.out.println(hrefGraph.toString());
    }

    private static DirectedGraph<String, DefaultEdge> createHrefGraph(){
        DirectedGraph<String, DefaultEdge> g =
            new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);

        String amazon = "[http://www.amazon.com](http://www.amazon.com)";
        String yahoo = 	"[http://www.yahoo.com](http://www.yahoo.com)";
        String ebay =	"[http://www.ebay.com](http://www.ebay.com)";

        // add the vertices
        g.addVertex(amazon);
        g.addVertex(yahoo);
        g.addVertex(ebay);

        // add edges to create linking structure
        g.addEdge(yahoo, amazon);
        g.addEdge(yahoo, ebay);

        return g;
    }

    private static UndirectedGraph<String, DefaultEdge> createStringGraph(){
        UndirectedGraph<String, DefaultEdge> g =
            new SimpleGraph<String, DefaultEdge>(DefaultEdge.class);

        String v1 = "v1";
        String v2 = "v2";
        String v3 = "v3";
        String v4 = "v4";

        // add the vertices
        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        g.addVertex(v4);

        // add edges to create a circuit
        g.addEdge(v1, v2);
        g.addEdge(v2, v3);
        g.addEdge(v3, v4);
        g.addEdge(v4, v1);

        return g;
    }
}
